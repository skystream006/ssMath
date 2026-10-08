package com.ssmath.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.serialization.Serializable

enum class CelebrationCategory(val label: String) {
    POKEMONS("Pokémons"), OTHER("Other")
}

@Serializable
enum class Celebration(
    val label: String,
    val description: String,
    val category: CelebrationCategory = CelebrationCategory.OTHER,
    val ndex: Int? = null
) {
    DOLPHINS("Dolphins", "Dolphins jumping out of the water saying Hurray!!"),
    WHALES("Whales", "Whales jumping out of the water saying Hurray!!"),
    ANCHOVIES("Anchovies", "Anchovies jumping out of the water saying Hurray!!"),
    PARTY("Party", "Congratulations party with balloons and confetti"),
    CANDY_SHOWER("Candy shower", "A colorful shower of candy"),
    PIKACHU("Pikachu", "Pikachu running toward you and zapping lightning", CelebrationCategory.POKEMONS, ndex = 25),
    SQUIRTLE("Squirtle", "Squirtle shooting water from his mouth", CelebrationCategory.POKEMONS, ndex = 7),
    BULBASAUR("Bulbasaur", "Bulbasaur shooting leaves from his bulb", CelebrationCategory.POKEMONS, ndex = 1),
    CHARMANDER("Charmander", "Charmander shooting fire into the air", CelebrationCategory.POKEMONS, ndex = 4),
    JIGGLYPUFF("Jigglypuff", "Jigglypuff rolling and jumping", CelebrationCategory.POKEMONS, ndex = 39),
    PALAFIN("Palafin", "Palafin leaping through sparkling water", CelebrationCategory.POKEMONS, ndex = 964),
    FINIZEN("Finizen", "Finizen jumping through bubbles", CelebrationCategory.POKEMONS, ndex = 963),
    WAILMER("Wailmer", "Wailmer bouncing and spraying water", CelebrationCategory.POKEMONS, ndex = 320),
    WAILORD("Wailord", "Wailord gliding and spouting water", CelebrationCategory.POKEMONS, ndex = 321),
    BOUFFALANT("Bouffalant", "Bouffalant charging and leaping in celebration", CelebrationCategory.POKEMONS, ndex = 626),
    VELUZA("Veluza", "Veluza darting through the water", CelebrationCategory.POKEMONS, ndex = 976),
    MANTYKE("Mantyke", "Mantyke flapping and jumping above the waves", CelebrationCategory.POKEMONS, ndex = 458),
    MANTINE("Mantine", "Mantine soaring over the sea with a little fish", CelebrationCategory.POKEMONS, ndex = 226);

    val collectionLabel: String
        get() = ndex?.let { "#${it.toString().padStart(4, '0')} $label" } ?: label

    companion object {
        val pokemons: List<Celebration> = entries.filter { it.category == CelebrationCategory.POKEMONS }

        fun select(questionCount: Int, random: Random = Random.Default): Celebration =
            entries.filter { questionCount >= 15 || it.category != CelebrationCategory.POKEMONS }.random(random)
    }
}

internal const val CELEBRATION_DURATION_MS = 7_200

@Composable
internal fun CelebrationDialog(
    celebration: Celebration,
    replay: Boolean = false,
    onPresented: () -> Unit = {},
    onFinished: () -> Unit
) {
    val progress = remember(celebration) { Animatable(0f) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val finish by rememberUpdatedState(onFinished)
    val presented by rememberUpdatedState(onPresented)
    LaunchedEffect(celebration, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            presented()
            val remaining = ((1f - progress.value) * CELEBRATION_DURATION_MS).toInt().coerceAtLeast(1)
            progress.animateTo(1f, tween(remaining, easing = LinearEasing))
            finish()
        }
    }
    AlertDialog(
        onDismissRequest = onFinished,
        title = { Text(if (replay) celebration.label else "Congratulations!") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (!replay) Text("You answered every question!", modifier = Modifier.padding(bottom = 16.dp))
                CelebrationScene(celebration, progress = { progress.value })
            }
        },
        confirmButton = {
            TextButton(onClick = onFinished, modifier = Modifier.testTag(if (replay) "close-celebration" else "view-results")) {
                Text(if (replay) "Close" else "View results")
            }
        }
    )
}

@Composable
internal fun CelebrationScene(celebration: Celebration, progress: () -> Float) {
    Box(Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(16.dp))
        .testTag("celebration-${celebration.name}").semantics {
            contentDescription = celebration.description
            role = Role.Image
        }) {
        Canvas(Modifier.matchParentSize()) {
            drawCelebrationArtwork(celebration, progress())
        }
        Column(Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(color = Color.White, contentColor = Color(0xFF123C61), shape = RoundedCornerShape(16.dp)) {
                Text("Hurray!!", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            Canvas(Modifier.size(16.dp, 8.dp)) {
                drawPath(Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                }, Color.White)
            }
        }
    }
}

internal fun DrawScope.drawCelebrationArtwork(celebration: Celebration, progress: Float) {
    withTransform({ scale(size.width / 320f, size.height / 220f, pivot = Offset.Zero) }) {
        when (celebration) {
            Celebration.DOLPHINS, Celebration.WHALES, Celebration.ANCHOVIES -> drawOcean(celebration, progress)
            Celebration.PARTY -> drawParty(progress)
            Celebration.CANDY_SHOWER -> drawCandyShower(progress)
            Celebration.PIKACHU -> drawPikachuCelebration(progress)
            Celebration.SQUIRTLE -> drawSquirtleCelebration(progress)
            Celebration.BULBASAUR -> drawBulbasaurCelebration(progress)
            Celebration.CHARMANDER -> drawCharmanderCelebration(progress)
            Celebration.JIGGLYPUFF -> drawJigglypuffCelebration(progress)
            Celebration.PALAFIN -> drawPalafinCelebration(progress)
            Celebration.FINIZEN -> drawFinizenCelebration(progress)
            Celebration.WAILMER -> drawWailmerCelebration(progress)
            Celebration.WAILORD -> drawWailordCelebration(progress)
            Celebration.BOUFFALANT -> drawBouffalantCelebration(progress)
            Celebration.VELUZA -> drawVeluzaCelebration(progress)
            Celebration.MANTYKE -> drawMantykeCelebration(progress)
            Celebration.MANTINE -> drawMantineCelebration(progress)
        }
    }
}

private fun DrawScope.drawOcean(celebration: Celebration, progress: Float) {
    drawRect(Color(0xFFDDF5FF), size = Size(320f, 220f))
    drawCircle(Color(0xFFFFDA70), 19f, Offset(281f, 31f))
    drawRect(Color(0xFF58BEE0), topLeft = Offset(0f, 165f), size = Size(320f, 55f))
    val count = when (celebration) {
        Celebration.WHALES -> 2
        Celebration.ANCHOVIES -> 7
        else -> 3
    }
    repeat(count) { index ->
        val phase = (progress * 3f + index * 0.16f) % 1f
        val jump = sin(phase * PI).toFloat()
        val x = 320f * (index + 1) / (count + 1)
        val y = 201f - jump * (if (celebration == Celebration.ANCHOVIES) 105f else 108f)
        withTransform({
            translate(x, y)
            rotate(-42f + phase * 84f, pivot = Offset.Zero)
        }) {
            when (celebration) {
                Celebration.DOLPHINS -> drawDolphin()
                Celebration.WHALES -> drawWhale()
                Celebration.ANCHOVIES -> drawAnchovy()
                else -> Unit
            }
        }
        if (phase < 0.2f || phase > 0.8f) {
            repeat(5) { drop ->
                val angle = PI * (drop + 1) / 6
                val spread = 14f + jump * 24f
                drawCircle(Color.White.copy(alpha = 0.8f), 2f,
                    Offset(x + cos(angle).toFloat() * spread, 168f - sin(angle).toFloat() * spread))
            }
        }
    }
    val wave = Path().apply {
        moveTo(0f, 176f)
        for (x in 0..320 step 8) {
            lineTo(x.toFloat(), 176f + sin(x * 0.045f + progress * 6f * PI.toFloat()) * 4f)
        }
    }
    drawPath(wave, Color(0xFFCCF5FF), style = Stroke(3f))
    wave.lineTo(320f, 220f)
    wave.lineTo(0f, 220f)
    wave.close()
    drawPath(wave, Brush.verticalGradient(listOf(Color(0xFF199FD0), Color(0xFF086AA6)), 176f, 220f))
}

private fun DrawScope.drawDolphin() {
    val body = Color(0xFF4E8EB2)
    drawPath(Path().apply {
        moveTo(-30f, 2f); lineTo(-47f, -10f); lineTo(-42f, 4f); lineTo(-48f, 15f); close()
    }, body)
    drawPath(Path().apply {
        moveTo(-12f, -12f); quadraticTo(-11f, -30f, -5f, -28f); lineTo(6f, -11f); close()
    }, body)
    drawPath(Path().apply {
        moveTo(-34f, 3f)
        cubicTo(-22f, -20f, 4f, -21f, 23f, -8f)
        quadraticTo(30f, -6f, 41f, -4f)
        quadraticTo(46f, 0f, 39f, 3f)
        lineTo(23f, 3f)
        cubicTo(12f, 18f, -10f, 16f, -34f, 3f)
        close()
    }, body)
    drawOval(Color(0xFFBDE6F0), Offset(-18f, 2f), Size(38f, 8f))
    drawPath(Path().apply {
        moveTo(3f, 7f); quadraticTo(-4f, 23f, 2f, 21f); lineTo(18f, 6f); close()
    }, Color(0xFF316989))
    drawCircle(Color(0xFF123C61), 2.5f, Offset(22f, -4f))
    drawLine(Color(0xFF123C61), Offset(25f, 4f), Offset(36f, 2f), strokeWidth = 1.5f)
}

private fun DrawScope.drawWhale() {
    val body = Color(0xFF426BB0)
    drawPath(Path().apply {
        moveTo(-36f, 5f); lineTo(-55f, -9f); lineTo(-51f, 6f)
        lineTo(-62f, 13f); quadraticTo(-45f, 20f, -32f, 12f); close()
    }, body)
    drawOval(body, Offset(-42f, -23f), Size(84f, 46f))
    drawOval(Color(0xFFBEDCF4), Offset(-23f, 8f), Size(60f, 14f))
    drawPath(Path().apply {
        moveTo(-5f, 9f); quadraticTo(-3f, 35f, 6f, 26f); lineTo(21f, 10f); close()
    }, Color(0xFF2F5090))
    drawCircle(Color(0xFF102951), 3f, Offset(26f, -2f))
    drawCircle(Color.White, 1f, Offset(27f, -3f))
    drawArc(Color(0xFF102951), 0f, 100f, false, Offset(18f, 0f), Size(17f, 12f), style = Stroke(1.5f))
    drawPath(Path().apply {
        moveTo(9f, -24f); quadraticTo(9f, -44f, -3f, -39f)
        moveTo(9f, -24f); quadraticTo(12f, -46f, 23f, -38f)
    }, Color(0xFF73CDEA), style = Stroke(3f))
}

private fun DrawScope.drawAnchovy() {
    drawPath(Path().apply {
        moveTo(-14f, 0f); lineTo(-25f, -9f); lineTo(-22f, 0f); lineTo(-25f, 9f); close()
    }, Color(0xFF7799AF))
    drawOval(Color(0xFFD6E8EF), Offset(-18f, -6f), Size(36f, 12f))
    drawLine(Color(0xFF527F9F), Offset(-15f, -2f), Offset(10f, -2f), strokeWidth = 3f)
    drawLine(Color(0xFF7799AF), Offset(7f, -4f), Offset(7f, 4f), strokeWidth = 1f)
    drawCircle(Color(0xFF123C61), 2f, Offset(12f, -1f))
}

private val partyColors = listOf(Color(0xFFFFCA55), Color(0xFFFF78AC), Color(0xFF66E0D2),
    Color(0xFFB495FF), Color(0xFF77CFFF))

private fun DrawScope.drawParty(progress: Float) {
    drawRect(Color(0xFF293563), size = Size(320f, 220f))
    repeat(5) { index ->
        val x = 34f + index * 63f
        val y = 116f + sin(progress * 4f * PI.toFloat() + index) * 20f
        drawPath(Path().apply {
            moveTo(x, y + 20f)
            cubicTo(x - 12f, y + 40f, x + 12f, y + 50f, x, y + 78f)
        }, Color.White.copy(alpha = 0.7f), style = Stroke(1.5f))
        drawOval(partyColors[index], Offset(x - 19f, y - 25f), Size(38f, 48f))
        drawOval(Color.White.copy(alpha = 0.4f), Offset(x - 11f, y - 18f), Size(8f, 14f))
        drawPath(Path().apply {
            moveTo(x, y + 21f); lineTo(x - 4f, y + 27f); lineTo(x + 4f, y + 27f); close()
        }, partyColors[index])
    }
    repeat(48) { index ->
        val phase = (progress * 3f + index * 0.073f) % 1f
        val x = ((index * 71) % 320).toFloat() + sin(phase * 6f + index) * 10f
        val y = -15f + phase * 250f
        rotate(phase * 360f + index * 19f, pivot = Offset(x, y)) {
            drawRect(partyColors[index % partyColors.size], Offset(x, y), Size(5f, 9f))
        }
    }
}
