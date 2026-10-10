package com.ssmath.app

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

internal enum class ProgressIcon(val label: String) {
    ROCKET("Rocket"),
    STAR("Star"),
    BALLOON("Balloon"),
    BALL("Ball"),
    BUTTERFLY("Butterfly"),
    FISH("Fish"),
    FROG("Frog"),
    BUNNY("Bunny"),
    CAT("Cat"),
    ROBOT("Robot")
}

internal fun DrawScope.drawProgressIcon(icon: ProgressIcon) {
    val unit = minOf(size.width, size.height) / 100f
    translate((size.width - 100f * unit) / 2f, (size.height - 100f * unit) / 2f) {
        scale(unit, pivot = Offset.Zero) {
            when (icon) {
                ProgressIcon.ROCKET -> {
                    drawPath(progressPolygon(35f, 48f, 16f, 73f, 36f, 70f), Color(0xFFEF5350))
                    drawPath(progressPolygon(65f, 48f, 84f, 73f, 64f, 70f), Color(0xFFEF5350))
                    drawPath(progressPolygon(39f, 69f, 50f, 95f, 61f, 69f), Color(0xFFFFB300))
                    drawPath(Path().apply {
                        moveTo(50f, 5f)
                        cubicTo(28f, 25f, 29f, 53f, 35f, 73f)
                        lineTo(65f, 73f)
                        cubicTo(71f, 53f, 72f, 25f, 50f, 5f)
                        close()
                    }, Color(0xFF26A6C2))
                    drawCircle(Color.White, 13f, Offset(50f, 39f))
                    drawCircle(Color(0xFF3949AB), 8f, Offset(50f, 39f))
                }
                ProgressIcon.STAR -> {
                    drawPath(progressPolygon(50f, 5f, 63f, 33f, 94f, 38f, 71f, 60f,
                        77f, 92f, 50f, 77f, 23f, 92f, 29f, 60f, 6f, 38f, 37f, 33f),
                        Color(0xFFFFC107))
                    progressFace(50f, 51f)
                }
                ProgressIcon.BALLOON -> {
                    drawPath(Path().apply {
                        moveTo(50f, 68f)
                        cubicTo(73f, 79f, 28f, 81f, 51f, 94f)
                    }, Color(0xFF546E7A), style = Stroke(3f, cap = StrokeCap.Round))
                    drawPath(progressPolygon(50f, 63f, 43f, 73f, 57f, 73f), Color(0xFFAD1457))
                    drawOval(Color(0xFFEC407A), Offset(22f, 5f), Size(56f, 62f))
                    drawOval(Color(0xFFFFB9D2), Offset(31f, 15f), Size(10f, 21f))
                }
                ProgressIcon.BALL -> {
                    drawCircle(Color(0xFFFF8F00), 42f, Offset(50f, 50f))
                    val seam = Color(0xFF6D3A16)
                    drawCircle(seam, 42f, Offset(50f, 50f), style = Stroke(3f))
                    drawLine(seam, Offset(8f, 50f), Offset(92f, 50f), 3f)
                    drawLine(seam, Offset(50f, 8f), Offset(50f, 92f), 3f)
                    listOf(22f, 78f).forEach { x ->
                        drawPath(Path().apply {
                            moveTo(x, 19f)
                            quadraticTo(100f - x, 50f, x, 81f)
                        }, seam, style = Stroke(3f))
                    }
                }
                ProgressIcon.BUTTERFLY -> {
                    drawOval(Color(0xFFAB47BC), Offset(5f, 15f), Size(42f, 48f))
                    drawOval(Color(0xFFAB47BC), Offset(53f, 15f), Size(42f, 48f))
                    drawOval(Color(0xFFEC407A), Offset(15f, 54f), Size(32f, 34f))
                    drawOval(Color(0xFFEC407A), Offset(53f, 54f), Size(32f, 34f))
                    listOf(26f, 74f).forEach { x ->
                        drawCircle(Color(0xFFFFD54F), 10f, Offset(x, 38f))
                    }
                    drawLine(Color(0xFF45275A), Offset(47f, 28f), Offset(38f, 8f), 3f, StrokeCap.Round)
                    drawLine(Color(0xFF45275A), Offset(53f, 28f), Offset(62f, 8f), 3f, StrokeCap.Round)
                    drawOval(Color(0xFF45275A), Offset(43f, 26f), Size(14f, 58f))
                }
                ProgressIcon.FISH -> {
                    drawPath(progressPolygon(65f, 50f, 94f, 25f, 94f, 75f), Color(0xFFEF6C00))
                    drawPath(progressPolygon(35f, 31f, 52f, 12f, 64f, 35f), Color(0xFFFFB300))
                    drawOval(Color(0xFFFF9800), Offset(6f, 27f), Size(72f, 50f))
                    drawPath(progressPolygon(43f, 48f, 60f, 42f, 55f, 64f), Color(0xFFEF6C00))
                    drawCircle(Color.White, 9f, Offset(25f, 45f))
                    drawCircle(Color(0xFF263238), 4f, Offset(23f, 45f))
                }
                ProgressIcon.FROG -> {
                    val green = Color(0xFF43A047)
                    drawOval(green, Offset(9f, 73f), Size(30f, 17f))
                    drawOval(green, Offset(61f, 73f), Size(30f, 17f))
                    drawOval(Color(0xFF66BB6A), Offset(10f, 29f), Size(80f, 55f))
                    listOf(30f, 70f).forEach { x ->
                        drawCircle(green, 17f, Offset(x, 29f))
                        drawCircle(Color.White, 11f, Offset(x, 27f))
                        drawCircle(Color(0xFF263238), 5f, Offset(x, 27f))
                    }
                    drawArc(Color(0xFF1B5E20), 0f, 180f, false, Offset(34f, 48f),
                        Size(32f, 20f), style = Stroke(3f, cap = StrokeCap.Round))
                }
                ProgressIcon.BUNNY -> {
                    val fur = Color(0xFFD1C4E9)
                    listOf(25f, 57f).forEach { x ->
                        drawOval(fur, Offset(x, 5f), Size(18f, 51f))
                        drawOval(Color(0xFFF48FB1), Offset(x + 5f, 12f), Size(8f, 34f))
                    }
                    drawOval(fur, Offset(15f, 40f), Size(70f, 52f))
                    progressFace(50f, 62f)
                    drawCircle(Color(0xFFF06292), 4f, Offset(50f, 68f))
                }
                ProgressIcon.CAT -> {
                    val fur = Color(0xFFFFB74D)
                    drawPath(progressPolygon(16f, 47f, 14f, 9f, 43f, 31f), fur)
                    drawPath(progressPolygon(57f, 31f, 86f, 9f, 84f, 47f), fur)
                    drawPath(progressPolygon(22f, 34f, 21f, 20f, 34f, 32f), Color(0xFFEF6C73))
                    drawPath(progressPolygon(66f, 32f, 79f, 20f, 78f, 34f), Color(0xFFEF6C73))
                    drawOval(fur, Offset(12f, 29f), Size(76f, 62f))
                    progressFace(50f, 55f)
                    drawPath(progressPolygon(45f, 61f, 55f, 61f, 50f, 67f), Color(0xFF8D4B38))
                    listOf(64f, 73f).forEach { y ->
                        drawLine(Color(0xFF8D4B38), Offset(6f, y - 3f), Offset(30f, y), 2f)
                        drawLine(Color(0xFF8D4B38), Offset(70f, y), Offset(94f, y - 3f), 2f)
                    }
                }
                ProgressIcon.ROBOT -> {
                    val blue = Color(0xFF42A5F5)
                    drawLine(Color(0xFF455A64), Offset(50f, 12f), Offset(50f, 30f), 4f)
                    drawCircle(Color(0xFFEF5350), 6f, Offset(50f, 11f))
                    drawRoundRect(blue, Offset(5f, 41f), Size(90f, 26f), CornerRadius(5f))
                    drawRoundRect(Color(0xFF90CAF9), Offset(16f, 26f), Size(68f, 59f), CornerRadius(10f))
                    listOf(35f, 65f).forEach { x ->
                        drawCircle(Color.White, 10f, Offset(x, 47f))
                        drawCircle(Color(0xFF263238), 4f, Offset(x, 47f))
                    }
                    drawRoundRect(Color(0xFF455A64), Offset(32f, 65f), Size(36f, 9f), CornerRadius(3f))
                    drawRoundRect(blue, Offset(28f, 84f), Size(14f, 10f), CornerRadius(3f))
                    drawRoundRect(blue, Offset(58f, 84f), Size(14f, 10f), CornerRadius(3f))
                }
            }
        }
    }
}

private fun progressPolygon(vararg coordinates: Float) = Path().apply {
    moveTo(coordinates[0], coordinates[1])
    for (index in 2 until coordinates.size step 2) lineTo(coordinates[index], coordinates[index + 1])
    close()
}

private fun DrawScope.progressFace(x: Float, y: Float) {
    val ink = Color(0xFF263238)
    drawCircle(ink, 3.5f, Offset(x - 10f, y))
    drawCircle(ink, 3.5f, Offset(x + 10f, y))
    drawArc(ink, 0f, 180f, false, Offset(x - 8f, y + 5f), Size(16f, 12f),
        style = Stroke(3f, cap = StrokeCap.Round))
}
