package com.ssmath.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

private val ink = Color(0xFF30324D)
private val yellow = Color(0xFFFFD84F)
private val blue = Color(0xFF68C9E9)
private val green = Color(0xFF71C9A2)
private val orange = Color(0xFFFFA04D)
private val pink = Color(0xFFFFB5D8)
private val candyColors = listOf(pink, blue, yellow, green, Color(0xFFB69AFF))

internal fun DrawScope.drawCandyShower(progress: Float) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFFFFE6F2), Color(0xFFE4DFFF)), 0f, 220f),
        size = Size(320f, 220f))
    repeat(42) { index ->
        val phase = (progress * 2.8f + index * 0.137f) % 1f
        val x = 12f + (index * 73 % 296) + sin(phase * 6f + index) * 12f
        val y = -28f + phase * 280f
        val color = candyColors[index % candyColors.size]
        withTransform({
            translate(x, y)
            rotate(phase * 300f + index * 29f, pivot = Offset.Zero)
        }) {
            when (index % 3) {
                0 -> {
                    outlinedPath(Path().apply {
                        moveTo(-7f, 0f); lineTo(-17f, -8f); lineTo(-17f, 8f); close()
                        moveTo(7f, 0f); lineTo(17f, -8f); lineTo(17f, 8f); close()
                    }, color)
                    outlinedOval(color, -10f, -7f, 20f, 14f)
                    drawLine(Color.White, Offset(-3f, -5f), Offset(3f, 5f), 3f)
                }
                1 -> {
                    drawLine(ink, Offset(0f, 0f), Offset(0f, 23f), 4f, StrokeCap.Round)
                    drawLine(Color.White, Offset(0f, 0f), Offset(0f, 22f), 2f, StrokeCap.Round)
                    outlinedOval(color, -10f, -10f, 20f, 20f)
                    drawArc(Color.White, -90f, 280f, false, Offset(-6f, -6f), Size(12f, 12f),
                        style = Stroke(2f))
                    drawCircle(Color.White, 2f, Offset.Zero)
                }
                else -> {
                    outlinedOval(color, -8f, -8f, 16f, 16f)
                    drawArc(Color.White, 190f, 110f, false, Offset(-5f, -5f), Size(10f, 10f),
                        style = Stroke(3f))
                }
            }
        }
    }
}

internal fun DrawScope.drawPikachuCelebration(progress: Float) {
    drawStage(Color(0xFFFFF0BD), progress)
    // Approach the viewer first, then plant both feet for the lightning celebration.
    val approach = (progress / 0.45f).coerceIn(0f, 1f)
    val stride = if (approach < 1f) sin(approach * 10f * PI.toFloat()) else 0f
    val zoom = 0.4f + approach * 0.65f
    val center = Offset(160f, 130f + approach * 18f - abs(stride) * 5f)
    drawOval(ink.copy(alpha = 0.12f), Offset(160f - zoom * 47f, 194f),
        Size(zoom * 94f, 10f))
    withTransform({
        translate(center.x, center.y)
        scale(zoom, zoom, pivot = Offset.Zero)
    }) {
        outlinedPath(Path().apply {
            moveTo(23f, 22f); lineTo(49f, 13f); lineTo(39f, -1f); lineTo(62f, -14f)
            lineTo(47f, -39f); lineTo(32f, -18f); lineTo(41f, -4f); lineTo(23f, 8f); close()
        }, yellow)
        outlinedOval(yellow, -26f, -12f, 52f, 58f)
        outlinedOval(yellow, -31f, 34f + stride * 7f, 27f, 13f)
        outlinedOval(yellow, 4f, 34f - stride * 7f, 27f, 13f)
        rotate(stride * 24f, pivot = Offset(-21f, 4f)) {
            outlinedOval(yellow, -42f, -1f, 24f, 15f)
        }
        rotate(-stride * 24f, pivot = Offset(21f, 4f)) {
            outlinedOval(yellow, 18f, -1f, 24f, 15f)
        }
        outlinedPath(Path().apply {
            moveTo(-25f, -27f); quadraticTo(-43f, -56f, -34f, -79f)
            quadraticTo(-15f, -68f, -12f, -32f); close()
            moveTo(12f, -32f); quadraticTo(15f, -68f, 34f, -79f)
            quadraticTo(43f, -56f, 25f, -27f); close()
        }, yellow)
        drawPath(Path().apply {
            moveTo(-34f, -78f); quadraticTo(-40f, -66f, -34f, -57f)
            lineTo(-24f, -61f); quadraticTo(-27f, -73f, -34f, -78f); close()
            moveTo(34f, -78f); quadraticTo(40f, -66f, 34f, -57f)
            lineTo(24f, -61f); quadraticTo(27f, -73f, 34f, -78f); close()
        }, ink)
        outlinedOval(yellow, -34f, -43f, 68f, 55f)
        drawEye(-14f, -22f, 4.5f)
        drawEye(14f, -22f, 4.5f)
        drawCircle(Color(0xFFF36C68), 7f, Offset(-25f, -7f))
        drawCircle(Color(0xFFF36C68), 7f, Offset(25f, -7f))
        drawCircle(ink, 2f, Offset(0f, -13f))
        drawSmile(0f, -4f, 11f)
    }
    if (progress > 0.45f) {
        val charge = ((progress - 0.45f) / 0.12f).coerceIn(0f, 1f)
        val sway = sin(progress * 6f * PI.toFloat()) * 5f
        for (side in listOf(-1f, 1f)) {
            val start = Offset(center.x + side * 25f * zoom, center.y - 7f * zoom)
            val bolt = Path().apply {
                moveTo(start.x, start.y)
                lineTo(start.x + side * 28f * charge, start.y - 24f * charge)
                lineTo(start.x + side * 20f * charge, start.y - 3f * charge)
                lineTo(start.x + side * 55f * charge, start.y - (38f + sway) * charge)
                lineTo(start.x + side * 47f * charge, start.y - 12f * charge)
                lineTo(start.x + side * 91f * charge, start.y - (49f + sway) * charge)
            }
            drawPath(bolt, yellow.copy(alpha = 0.3f), style = Stroke(12f, cap = StrokeCap.Round))
            drawPath(bolt, Color(0xFFE5A900), style = Stroke(5f, cap = StrokeCap.Round))
            drawPath(bolt, Color(0xFFFFFFD6), style = Stroke(2f, cap = StrokeCap.Round))
        }
    }
}

internal fun DrawScope.drawSquirtleCelebration(progress: Float) {
    drawStage(Color(0xFFD8F5FF), progress)
    val bob = sin(progress * 6f * PI.toFloat()) * 3f
    val mouth = Offset(131f, 130f + bob)
    drawOval(blue.copy(alpha = 0.3f), Offset(218f, 197f), Size(83f, 10f))
    withTransform({ translate(93f, 154f + bob) }) {
        outlinedOval(blue, -51f, 12f, 31f, 25f)
        drawArc(ink, -90f, 280f, false, Offset(-46f, 16f), Size(19f, 16f), style = Stroke(2f))
        outlinedOval(Color(0xFFAE734F), -34f, -22f, 57f, 65f)
        outlinedOval(Color(0xFFFFE5AB), -20f, -16f, 47f, 59f)
        drawLine(Color(0xFFBA915E), Offset(-17f, 9f), Offset(24f, 9f), 2f)
        drawLine(Color(0xFFBA915E), Offset(-14f, 26f), Offset(22f, 26f), 2f)
        drawLine(Color(0xFFBA915E), Offset(4f, -13f), Offset(4f, 40f), 2f)
        outlinedOval(blue, -27f, 33f, 26f, 15f)
        outlinedOval(blue, 13f, 32f, 26f, 15f)
        outlinedOval(blue, 10f, -5f, 35f, 16f)
        outlinedOval(blue, -20f, -59f, 60f, 49f)
        outlinedOval(blue, 24f, -35f, 18f, 18f)
        drawEye(19f, -40f, 7f)
        drawCircle(Color(0xFFE993B1), 5f, Offset(9f, -24f))
        outlinedOval(Color(0xFF245978), 33f, -29f, 9f, 10f)
    }
    val reach = (progress / 0.16f).coerceIn(0f, 1f)
    val stream = Path().apply {
        moveTo(mouth.x, mouth.y)
        cubicTo(mouth.x + 55f * reach, mouth.y - 48f * reach,
            mouth.x + 105f * reach, mouth.y - 41f * reach,
            mouth.x + 143f * reach, mouth.y + 63f * reach)
    }
    drawPath(stream, Color(0xFF259EDB), style = Stroke(12f, cap = StrokeCap.Round))
    drawPath(stream, Color(0xFFACF2FF), style = Stroke(6f, cap = StrokeCap.Round))
    repeat(22) { index ->
        val phase = (progress * 4f + index / 22f) % 1f
        val t = phase * reach
        val x = mouth.x + 143f * t
        val y = mouth.y - 110f * t + 173f * t * t
        drawCircle(Color.White.copy(alpha = 1f - phase * 0.6f), 2f + phase,
            Offset(x, y + sin(index * 2f + progress * 20f) * phase * 7f))
    }
    repeat(7) { index ->
        val t = (progress * 3f + index / 7f) % 1f
        drawCircle(blue.copy(alpha = (1f - t) * reach), 3f,
            Offset(274f + (index - 3) * t * 9f, 198f - sin(t * PI.toFloat()) * 21f))
    }
}

internal fun DrawScope.drawBulbasaurCelebration(progress: Float) {
    drawStage(Color(0xFFE3F6D2), progress)
    val bob = sin(progress * 8f * PI.toFloat()) * 2f
    withTransform({ translate(118f, 164f + bob) }) {
        outlinedOval(green, -47f, -22f, 84f, 52f)
        outlinedOval(green, -40f, 13f, 22f, 28f)
        outlinedOval(green, 10f, 13f, 24f, 28f)
        outlinedPath(Path().apply {
            moveTo(-35f, -18f)
            cubicTo(-68f, -47f, -36f, -61f, -15f, -80f)
            cubicTo(-3f, -62f, 31f, -49f, 7f, -18f); close()
        }, Color(0xFF4B9F5C))
        drawPath(Path().apply {
            moveTo(-15f, -77f); quadraticTo(-41f, -40f, -22f, -20f)
            moveTo(-15f, -77f); quadraticTo(9f, -41f, -10f, -20f)
        }, Color(0xFF286C45), style = Stroke(2f))
        outlinedPath(Path().apply {
            moveTo(-7f, -24f); lineTo(-10f, -52f); lineTo(10f, -38f)
            lineTo(35f, -37f); lineTo(52f, -53f); lineTo(54f, -21f); close()
        }, green)
        outlinedOval(green, -14f, -39f, 75f, 57f)
        drawEye(5f, -17f, 7f, Color(0xFFA43E65))
        drawEye(40f, -17f, 7f, Color(0xFFA43E65))
        drawSmile(24f, -2f, 16f)
        drawPath(Path().apply {
            moveTo(14f, -31f); lineTo(23f, -34f); lineTo(29f, -24f); lineTo(19f, -21f); close()
            moveTo(-37f, -5f); lineTo(-27f, -11f); lineTo(-24f, 1f); close()
            moveTo(-33f, 23f); lineTo(-25f, 20f); lineTo(-25f, 30f); close()
        }, Color(0xFF328777))
        for (x in listOf(-36f, 16f)) {
            drawLine(Color(0xFFFFF4D9), Offset(x, 36f), Offset(x + 11f, 36f), 3f)
        }
    }
    val reach = (progress / 0.15f).coerceIn(0f, 1f)
    repeat(16) { index ->
        val t = ((progress * 2.7f + index / 16f) % 1f) * reach
        val origin = Offset(103f, 86f + bob)
        val x = origin.x + (125f + index % 4 * 21f) * t
        val y = origin.y - (92f + index % 3 * 15f) * t + 153f * t * t
        withTransform({
            translate(x, y)
            rotate(-55f + t * 250f + index * 17f, pivot = Offset.Zero)
            scale(0.3f + t * 0.7f, 0.3f + t * 0.7f, pivot = Offset.Zero)
        }) {
            outlinedPath(Path().apply {
                moveTo(-13f, 0f); quadraticTo(0f, -17f, 15f, 0f)
                quadraticTo(0f, 13f, -13f, 0f); close()
            }, if (index % 2 == 0) Color(0xFF55B85A) else Color(0xFF9BD65B))
            drawLine(Color(0xFF286C45), Offset(-10f, 0f), Offset(11f, 0f), 1.5f)
        }
    }
}

internal fun DrawScope.drawCharmanderCelebration(progress: Float) {
    drawStage(Color(0xFFFFE2CF), progress)
    val bob = sin(progress * 6f * PI.toFloat()) * 2f
    withTransform({ translate(128f, 159f + bob) }) {
        outlinedPath(Path().apply {
            moveTo(-22f, 16f); quadraticTo(-65f, 43f, -71f, 2f)
            quadraticTo(-52f, 23f, -18f, 2f); close()
        }, orange)
        withTransform({ translate(-71f, -3f) }) {
            drawFlame(0.75f + sin(progress * 25f) * 0.1f)
        }
        outlinedOval(orange, -30f, -21f, 60f, 64f)
        outlinedOval(Color(0xFFFFE3A2), -16f, -9f, 36f, 49f)
        outlinedOval(orange, -37f, 32f, 32f, 15f)
        outlinedOval(orange, 11f, 32f, 32f, 15f)
        outlinedOval(orange, -40f, -11f, 25f, 15f)
        outlinedOval(orange, 18f, -19f, 29f, 16f)
        rotate(-22f, pivot = Offset(7f, -40f)) {
            outlinedOval(orange, -23f, -63f, 59f, 49f)
            outlinedOval(orange, 18f, -45f, 26f, 24f)
            drawEye(13f, -48f, 7f, Color(0xFF247B82))
            outlinedOval(Color(0xFF9E403D), 32f, -38f, 12f, 14f)
            drawCircle(Color(0xFFF27C65), 4f, Offset(6f, -30f))
        }
        for (x in listOf(-30f, 19f)) {
            drawLine(Color(0xFFFFF4D9), Offset(x, 43f), Offset(x + 13f, 43f), 3f)
        }
    }
    val mouth = Offset(166f, 115f + bob)
    val reach = (progress / 0.18f).coerceIn(0f, 1f)
    val plume = Path().apply {
        moveTo(mouth.x, mouth.y + 4f)
        quadraticTo(mouth.x + 14f * reach, mouth.y - 25f * reach,
            mouth.x + 61f * reach, mouth.y - 67f * reach)
        quadraticTo(mouth.x + 53f * reach, mouth.y - 40f * reach,
            mouth.x + 65f * reach, mouth.y - 45f * reach)
        quadraticTo(mouth.x + 33f * reach, mouth.y - 10f * reach, mouth.x, mouth.y + 4f)
        close()
    }
    drawPath(plume, Color(0xFFFF7540))
    repeat(13) { index ->
        val t = ((progress * 3f + index / 13f) % 1f) * reach
        withTransform({
            translate(mouth.x + 71f * t + sin(t * 9f + index) * t * 5f, mouth.y - 73f * t)
            rotate(30f, pivot = Offset.Zero)
        }) {
            drawFlame(0.22f + 0.48f * t)
        }
    }
}

internal fun DrawScope.drawJigglypuffCelebration(progress: Float) {
    drawStage(Color(0xFFF6E0FF), progress)
    // Two complete rolls, followed by two upright jumps, ending in a standing pose.
    val rolling = (progress / 0.48f).coerceIn(0f, 1f)
    val jumping = ((progress - 0.48f) / 0.52f).coerceIn(0f, 1f)
    val hop = abs(sin(jumping * 2f * PI.toFloat()))
    val x = 61f + rolling * 151f - jumping * 52f
    val rollLift = sin(rolling * PI.toFloat()) * 16f
    val y = 164f - rollLift - hop * 54f
    drawOval(ink.copy(alpha = 0.12f), Offset(x - 35f + hop * 9f, 200f),
        Size(70f - hop * 18f, 8f))
    withTransform({
        translate(x, y)
        rotate(rolling * 720f, pivot = Offset.Zero)
    }) {
        outlinedOval(pink, -39f, 24f, 31f, 15f)
        outlinedOval(pink, 8f, 24f, 31f, 15f)
        outlinedOval(pink, -47f, -6f - hop * 8f, 22f, 16f)
        outlinedOval(pink, 25f, -6f - hop * 8f, 22f, 16f)
        outlinedPath(Path().apply {
            moveTo(-31f, -20f); lineTo(-33f, -53f); quadraticTo(-12f, -50f, -9f, -31f); close()
            moveTo(9f, -31f); quadraticTo(12f, -50f, 33f, -53f); lineTo(31f, -20f); close()
        }, pink)
        drawPath(Path().apply {
            moveTo(-27f, -29f); lineTo(-28f, -44f); lineTo(-16f, -32f); close()
            moveTo(16f, -32f); lineTo(28f, -44f); lineTo(27f, -29f); close()
        }, Color(0xFF69486E))
        outlinedOval(pink, -36f, -36f, 72f, 72f)
        outlinedPath(Path().apply {
            moveTo(-15f, -27f); cubicTo(-25f, -52f, 24f, -51f, 15f, -28f)
            cubicTo(9f, -12f, -13f, -22f, -2f, -33f)
            quadraticTo(3f, -38f, 7f, -32f)
        }, pink)
        drawEye(-16f, -5f, 10f, Color(0xFF318DAD))
        drawEye(16f, -5f, 10f, Color(0xFF318DAD))
        drawCircle(Color(0xFFF58AB8), 6f, Offset(-26f, 12f))
        drawCircle(Color(0xFFF58AB8), 6f, Offset(26f, 12f))
        drawSmile(0f, 15f, 12f)
    }
}

private fun DrawScope.drawStage(sky: Color, progress: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.White, sky), 0f, 220f), size = Size(320f, 220f))
    drawOval(Color.White.copy(alpha = 0.6f), Offset(-20f, 182f), Size(360f, 66f))
    repeat(9) { index ->
        val x = 21f + (index * 97 % 280)
        val y = 68f + (index * 41 % 116)
        val radius = 2.5f + sin(progress * 4f * PI.toFloat() + index) * 1.5f
        drawLine(Color.White, Offset(x - radius, y), Offset(x + radius, y), 2f, StrokeCap.Round)
        drawLine(Color.White, Offset(x, y - radius), Offset(x, y + radius), 2f, StrokeCap.Round)
    }
}

private fun DrawScope.outlinedOval(color: Color, x: Float, y: Float, width: Float, height: Float) {
    drawOval(color, Offset(x, y), Size(width, height))
    drawOval(ink, Offset(x, y), Size(width, height), style = Stroke(2f))
}

private fun DrawScope.outlinedPath(path: Path, color: Color) {
    drawPath(path, color)
    drawPath(path, ink, style = Stroke(2f))
}

private fun DrawScope.drawEye(x: Float, y: Float, radius: Float, iris: Color = ink) {
    drawOval(Color.White, Offset(x - radius, y - radius * 1.2f), Size(radius * 2f, radius * 2.4f))
    drawOval(iris, Offset(x - radius * 0.8f, y - radius), Size(radius * 1.6f, radius * 2f))
    drawOval(ink, Offset(x - radius * 0.4f, y - radius * 0.8f), Size(radius * 0.8f, radius * 1.6f))
    drawCircle(Color.White, radius * 0.3f, Offset(x - radius * 0.25f, y - radius * 0.5f))
}

private fun DrawScope.drawSmile(x: Float, y: Float, width: Float) {
    drawArc(ink, 0f, 180f, false, Offset(x - width / 2f, y - 3f), Size(width, 8f),
        style = Stroke(2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawFlame(scale: Float) {
    withTransform({ scale(scale, scale, pivot = Offset.Zero) }) {
        drawPath(Path().apply {
            moveTo(0f, 8f); cubicTo(-20f, 4f, -13f, -12f, -9f, -18f)
            lineTo(-5f, -9f); quadraticTo(4f, -20f, 2f, -32f)
            cubicTo(24f, -11f, 15f, 7f, 0f, 8f); close()
        }, Color(0xFFF36B38))
        drawPath(Path().apply {
            moveTo(0f, 5f); quadraticTo(-10f, 1f, -4f, -11f)
            lineTo(0f, -6f); lineTo(5f, -19f); quadraticTo(17f, 1f, 0f, 5f); close()
        }, yellow)
    }
}
