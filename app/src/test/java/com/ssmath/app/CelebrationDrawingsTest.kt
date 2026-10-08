package com.ssmath.app

import android.app.Application
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CelebrationDrawingsTest {
    private val drawings = mapOf<String, DrawScope.(Float) -> Unit>(
        "Candy shower" to DrawScope::drawCandyShower,
        "Pikachu" to DrawScope::drawPikachuCelebration,
        "Squirtle" to DrawScope::drawSquirtleCelebration,
        "Bulbasaur" to DrawScope::drawBulbasaurCelebration,
        "Charmander" to DrawScope::drawCharmanderCelebration,
        "Jigglypuff" to DrawScope::drawJigglypuffCelebration,
        "Palafin" to DrawScope::drawPalafinCelebration,
        "Finizen" to DrawScope::drawFinizenCelebration,
        "Wailmer" to DrawScope::drawWailmerCelebration,
        "Wailord" to DrawScope::drawWailordCelebration,
        "Bouffalant" to DrawScope::drawBouffalantCelebration,
        "Veluza" to DrawScope::drawVeluzaCelebration,
        "Mantyke" to DrawScope::drawMantykeCelebration,
        "Mantine" to DrawScope::drawMantineCelebration
    )

    @Test fun everyNewSceneDrawsChangingFramesThroughoutPlayback() {
        drawings.forEach { (name, draw) ->
            var previous = render(draw, 0f)
            assertTrue("$name must fill its first frame", previous.all { (it ushr 24) == 255 })
            listOf(0.2f, 0.4f, 0.6f, 0.8f, 1f).forEach { progress ->
                val frame = render(draw, progress)
                val changed = frame.indices.count { frame[it] != previous[it] }
                assertTrue("$name must animate at progress $progress", changed > 300)
                assertTrue("$name must fill the scene", frame.all { (it ushr 24) == 255 })
                previous = frame
            }
        }
    }

    @Test fun pikachuApproachesBeforeZappingLightning() {
        val early = render(DrawScope::drawPikachuCelebration, 0f)
        val arrived = render(DrawScope::drawPikachuCelebration, 0.45f)
        val zapping = render(DrawScope::drawPikachuCelebration, 0.7f)
        val yellow = Color(0xFFFFD84F).toArgb()
        val lightning = Color(0xFFE5A900).toArgb()
        assertTrue(arrived.count { it == yellow } > early.count { it == yellow } * 3)
        assertEquals(0, early.count { it == lightning })
        assertTrue(zapping.count { it == lightning } > 100)
    }

    @Test fun waterLeavesAndFireTravelOutwardFromTheirCharacters() {
        val water = render(DrawScope::drawSquirtleCelebration, 0.5f)
        val leaves = render(DrawScope::drawBulbasaurCelebration, 0.5f)
        val fire = render(DrawScope::drawCharmanderCelebration, 0.5f)
        val waterColor = Color(0xFF259EDB).toArgb()
        val leafColor = Color(0xFF55B85A).toArgb()
        val fireColor = Color(0xFFFF7540).toArgb()
        assertTrue(water.indices.count { it % 320 > 200 && water[it] == waterColor } > 100)
        assertTrue(leaves.indices.count { it % 320 > 170 && leaves[it] == leafColor } > 100)
        assertTrue(fire.indices.count { it / 320 < 100 && fire[it] == fireColor } > 100)
    }

    @Test fun jigglypuffRollsAcrossTheSceneThenJumpsUp() {
        val start = render(DrawScope::drawJigglypuffCelebration, 0f)
        val rolled = render(DrawScope::drawJigglypuffCelebration, 0.48f)
        val jumping = render(DrawScope::drawJigglypuffCelebration, 0.61f)
        val pink = Color(0xFFFFB5D8).toArgb()
        fun center(pixels: IntArray, axis: (Int) -> Int) =
            pixels.indices.filter { pixels[it] == pink }.map(axis).average()
        assertTrue(center(rolled) { it % 320 } > center(start) { it % 320 } + 100)
        assertTrue(center(jumping) { it / 320 } < center(rolled) { it / 320 } - 40)
    }

    @Test fun jigglypuffDoesNotClipAtTheBottomWhileRolling() {
        val pink = Color(0xFFFFB5D8).toArgb()
        val outline = Color(0xFF30324D).toArgb()
        for (step in 0..48) {
            val progress = step / 100f
            val frame = render(DrawScope::drawJigglypuffCelebration, progress)
            assertFalse("Jigglypuff clips at progress $progress",
                frame.takeLast(320).any { it == pink || it == outline })
        }
    }

    @Test fun newPokemonMoveTheirBodiesNotOnlyTheBackground() {
        val bodyColors = mapOf(
            "Palafin" to Color(0xFF53B9E1),
            "Finizen" to Color(0xFF77CDEB),
            "Wailmer" to Color(0xFF609CE6),
            "Wailord" to Color(0xFF4D89CB),
            "Bouffalant" to Color(0xFFAA754B),
            "Veluza" to Color(0xFFC6D3DE),
            "Mantyke" to Color(0xFF4ABAE0),
            "Mantine" to Color(0xFF457BAC)
        )
        bodyColors.forEach { (name, color) ->
            val draw = drawings.getValue(name)
            val argb = color.toArgb()
            var previous = render(draw, 0f)
            listOf(0.2f, 0.4f, 0.6f, 0.8f, 1f).forEach { progress ->
                val frame = render(draw, progress)
                assertTrue("$name must have a visible body", frame.count { it == argb } > 500)
                val moved = frame.indices.count { (frame[it] == argb) != (previous[it] == argb) }
                assertTrue("$name's body must move at progress $progress", moved > 150)
                previous = frame
            }
        }
    }

    @Test fun palafinHasAHeartMarkingThatFinizenDoesNotHave() {
        val palafin = render(DrawScope::drawPalafinCelebration, 0f)
        val finizen = render(DrawScope::drawFinizenCelebration, 0f)
        val heart = Color(0xFFF36EAB).toArgb()
        assertTrue(palafin.indices.count {
            it % 320 in 140..185 && it / 320 in 100..140 && palafin[it] == heart
        } > 100)
        assertEquals(0, finizen.count { it == heart })
    }

    @Test fun wailmerIsRoundWhileWailordIsLong() {
        val wailmer = colorBounds(render(DrawScope::drawWailmerCelebration, 0f), Color(0xFF609CE6))
        val wailord = colorBounds(render(DrawScope::drawWailordCelebration, 0f), Color(0xFF4D89CB))
        // The blue region excludes their pale bellies, but still distinguishes the silhouettes.
        assertTrue(wailmer.width < wailmer.height * 2f)
        assertTrue(wailord.width > wailord.height * 2.3f)
        assertTrue(wailord.width > wailmer.width * 1.6f)
    }

    @Test fun bouffalantHasAnAfroAndHornsAndVeluzaHasGreenFins() {
        val bouffalant = render(DrawScope::drawBouffalantCelebration, 0f)
        val veluza = render(DrawScope::drawVeluzaCelebration, 0f)
        assertTrue(bouffalant.count { it == Color(0xFF50372F).toArgb() } > 2500)
        assertTrue(bouffalant.count { it == Color(0xFFFFF2CF).toArgb() } > 250)
        val fins = Color(0xFF88D948).toArgb()
        assertTrue(veluza.indices.count { it / 320 < 98 && veluza[it] == fins } > 300)
        assertTrue(veluza.indices.count { it / 320 > 132 && veluza[it] == fins } > 100)
    }

    @Test fun mantineHasLargerWingsAndAnAccompanyingFish() {
        val mantyke = render(DrawScope::drawMantykeCelebration, 0f)
        val mantine = render(DrawScope::drawMantineCelebration, 0f)
        val small = colorBounds(mantyke, Color(0xFF4ABAE0))
        val large = colorBounds(mantine, Color(0xFF457BAC))
        assertTrue(large.width > small.width * 1.6f)
        val fish = Color(0xFFE7CE7A).toArgb()
        assertTrue(mantine.indices.count {
            it % 320 > 190 && it / 320 > 140 && mantine[it] == fish
        } > 150)
        assertEquals(0, mantyke.count { it == fish })
    }

    private fun colorBounds(pixels: IntArray, color: Color): Size {
        val indices = pixels.indices.filter { pixels[it] == color.toArgb() }
        assertTrue("Expected visible pixels of $color", indices.isNotEmpty())
        return Size(
            (indices.maxOf { it % 320 } - indices.minOf { it % 320 } + 1).toFloat(),
            (indices.maxOf { it / 320 } - indices.minOf { it / 320 } + 1).toFloat()
        )
    }

    private fun render(draw: DrawScope.(Float) -> Unit, progress: Float): IntArray {
        val image = ImageBitmap(320, 220)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(image), Size(320f, 220f)) {
            draw(progress)
        }
        return IntArray(320 * 220).also { image.readPixels(it) }
    }
}
