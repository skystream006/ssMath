package com.ssmath.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawKantoEarly(ndex: Int, motion: Float) {
    val sway = motion.coerceIn(-1f, 1f)
    when (ndex) {
        2, 3 -> earlyGardenToad(ndex == 3, sway)
        5, 6 -> earlyFireLizard(ndex == 6, sway)
        8, 9 -> earlyTurtle(ndex == 9, sway)
        10 -> earlyCaterpie(sway)
        11, 14 -> earlyCocoon(ndex == 14)
        12, 49 -> earlyWingedInsect(ndex == 49, sway)
        13 -> earlyWeedle(sway)
        15 -> earlyBeedrill(sway)
        16, 17, 18 -> earlyCrestedBird(ndex - 16, sway)
        19, 20 -> earlyRodent(ndex == 20, sway)
        21, 22 -> earlyLongBeakBird(ndex == 22, sway)
        23, 24 -> earlySnake(ndex == 24, sway)
        26 -> earlyRaichu(sway)
        27, 28 -> earlySandArmor(ndex == 28, sway)
        29, 30, 31 -> earlyNido(ndex - 29, false, sway)
        32, 33, 34 -> earlyNido(ndex - 32, true, sway)
        35, 36 -> earlyMoonFairy(ndex == 36, sway)
        37, 38 -> earlyFox(ndex == 38, sway)
        40 -> earlyWigglytuff(sway)
        41, 42 -> earlyBat(ndex == 42, sway)
        43, 44, 45 -> earlyFlowerWalker(ndex - 43, sway)
        46, 47 -> earlyMushroomCrab(ndex == 47, sway)
        48 -> earlyVenonat(sway)
        50 -> earlyDiglett(sway)
        else -> error("Unsupported early Kanto drawing: $ndex")
    }
}

private const val EARLY_CREAM: Long = 0xFFFFEDBA
private const val EARLY_WHITE: Long = 0xFFFFFCF3

private fun DrawScope.earlyShape(color: Long, outline: Boolean = true, shape: Path.() -> Unit) {
    val path = Path().apply(shape)
    drawPath(path, Color(color))
    if (outline) {
        drawPath(path, Color(KANTO_INK), style = Stroke(2.6f, join = StrokeJoin.Round))
    }
}

private fun DrawScope.earlyCurve(color: Long, width: Float = 2.5f, shape: Path.() -> Unit) {
    drawPath(
        Path().apply(shape), Color(color),
        style = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

private fun DrawScope.earlyEye(x: Float, y: Float, radius: Float = 5f, iris: Long = KANTO_INK) {
    kantoOval(EARLY_WHITE, x - radius, y - radius * 1.2f, radius * 2f, radius * 2.4f)
    drawCircle(Color(iris), radius * 0.62f, Offset(x + 0.5f, y))
    drawCircle(Color.White, radius * 0.23f, Offset(x - 0.5f, y - radius * 0.35f))
}

private fun DrawScope.earlyClaws(x: Float, y: Float, spacing: Float = 6f) {
    for (i in -1..1) {
        val cx = x + i * spacing
        kantoPolygon(EARLY_WHITE, cx - 2.4f, y, cx + 2.4f, y, cx, y + 5f)
    }
}

private fun DrawScope.earlyGardenToad(large: Boolean, motion: Float) {
    val skin = if (large) 0xFF62B4A3 else 0xFF72C4B0
    val leaf = 0xFF3E9670
    val bloomX = if (large) 0f else 15f
    if (large) {
        for (side in listOf(-1f, 1f)) {
            kantoOval(skin, side * 42f - 14f, 25f, 29f, 24f)
            earlyClaws(side * 43f, 44f)
        }
        kantoOval(skin, -59f, -3f, 118f, 48f)
        kantoPolygon(0xFF419785, -49f, 7f, -39f, 1f, -32f, 11f)
        kantoPolygon(0xFF419785, 39f, 16f, 48f, 6f, 54f, 20f)
        kantoPolygon(0xFF9B7852, -10f, -29f, 11f, -29f, 17f, 11f, -15f, 11f)
    } else {
        kantoOval(skin, -28f, 1f, 76f, 39f)
        for (x in listOf(-31f, -8f, 23f, 40f)) {
            kantoOval(skin, x - 9f, 26f, 19f, 23f)
            earlyClaws(x, 44f, 4.5f)
        }
        kantoPolygon(0xFF369384, 28f, 15f, 37f, 7f, 41f, 21f)
    }
    val leafY = if (large) -16f else -13f
    val leafReach = if (large) 69f else 47f
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(
            leaf, bloomX, leafY - 17f,
            bloomX + side * leafReach, leafY - 18f + motion * 2f,
            bloomX + side * (leafReach - 15f), leafY - 5f,
            bloomX + side * leafReach, leafY + 5f,
            bloomX + side * 19f, leafY + 7f,
        )
        kantoLine(0xFF246C54, 2f, bloomX, leafY - 10f, bloomX + side * (leafReach - 9f), leafY - 8f)
    }
    if (large) {
        for ((x, y) in listOf(-38f to -44f, -17f to -58f, 17f to -52f, 31f to -29f, -23f to -24f)) {
            kantoOval(0xFFED7D86, x - 19f, y - 7f, 39f, 25f)
            drawCircle(Color(EARLY_CREAM), 3f, Offset(x - 6f, y + 3f))
            drawCircle(Color(EARLY_CREAM), 2.5f, Offset(x + 7f, y + 9f))
        }
        kantoOval(0xFFE7C970, -14f, -39f, 30f, 24f)
        for (x in listOf(-8f, 0f, 8f)) kantoLine(0xFF9F813D, 2f, x, -35f, x + 2f, -26f)
        kantoPolygon(skin, -35f, 7f, -39f, -13f, -19f, 1f)
        kantoPolygon(skin, 15f, 1f, 37f, -13f, 33f, 11f)
        kantoOval(skin, -40f, -3f, 78f, 44f)
        earlyEye(-23f, 12f, 7f, 0xFFBA4355)
        earlyEye(22f, 12f, 7f, 0xFFBA4355)
        kantoLine(KANTO_INK, 2.5f, -31f, 6f, -16f, 10f)
        kantoLine(KANTO_INK, 2.5f, 16f, 10f, 30f, 6f)
        kantoSmile(0f, 27f, 24f)
        kantoPolygon(EARLY_WHITE, -23f, 28f, -17f, 29f, -20f, 35f)
        kantoPolygon(EARLY_WHITE, 17f, 29f, 23f, 28f, 20f, 35f)
    } else {
        earlyShape(0xFFE97791) {
            moveTo(-6f, -27f)
            quadraticTo(-9f, -43f, 6f, -54f)
            lineTo(14f, -63f)
            lineTo(23f, -53f)
            quadraticTo(40f, -44f, 34f, -27f)
            quadraticTo(16f, -16f, -6f, -27f)
            close()
        }
        earlyCurve(0xFFAD456D, 2f) {
            moveTo(14f, -58f); quadraticTo(7f, -39f, 15f, -24f)
            moveTo(23f, -50f); quadraticTo(30f, -39f, 24f, -25f)
        }
        kantoPolygon(skin, -53f, -6f, -55f, -28f, -36f, -15f)
        kantoPolygon(skin, -17f, -12f, 1f, -28f, 0f, 0f)
        kantoOval(skin, -58f, -17f, 65f, 46f)
        earlyEye(-43f, 0f, 6f, 0xFFBC405C)
        earlyEye(-9f, 0f, 6f, 0xFFBC405C)
        kantoPolygon(0xFF368B7E, -31f, -10f, -22f, -13f, -25f, -3f)
        kantoPolygon(0xFF368B7E, -48f, 15f, -41f, 11f, -39f, 20f)
        kantoSmile(-25f, 14f, 15f)
        kantoPolygon(EARLY_WHITE, -40f, 14f, -34f, 16f, -36f, 21f)
    }
}

private fun DrawScope.earlyFlame(x: Float, y: Float, motion: Float) {
    kantoPolygon(
        0xFFF18136, x - 11f, y + 14f, x - 15f, y + 1f,
        x - 8f, y - 12f, x - 4f, y - 3f,
        x + motion * 2f, y - 22f, x + 8f, y - 6f,
        x + 12f, y - 12f, x + 14f, y + 4f, x + 6f, y + 16f,
    )
    earlyShape(0xFFFFD96A, outline = false) {
        moveTo(x - 6f, y + 11f); quadraticTo(x - 8f, y + 2f, x, y - 7f)
        quadraticTo(x + 9f, y + 5f, x + 5f, y + 13f); close()
    }
}

private fun DrawScope.earlyFireLizard(winged: Boolean, motion: Float) {
    val skin = if (winged) 0xFFEA9851 else 0xFFE77361
    if (winged) {
        for (side in listOf(-1f, 1f)) {
            kantoPolygon(
                skin, side * 16f, -9f, side * 45f, -61f + motion * 3f,
                side * 85f, -40f + motion * 4f, side * 75f, -36f,
                side * 89f, 8f + motion * 3f, side * 61f, -5f,
                side * 47f, 17f, side * 33f, 5f,
            )
            kantoPolygon(
                0xFF458F9B, side * 24f, -10f, side * 45f, -52f + motion * 3f,
                side * 76f, -38f + motion * 3f, side * 68f, -31f,
                side * 78f, 0f, side * 59f, -13f, side * 46f, 8f, side * 35f, -3f,
            )
            kantoLine(KANTO_INK, 2f, side * 44f, -49f, side * 58f, -14f)
        }
    }
    earlyShape(skin) {
        moveTo(-12f, 32f)
        cubicTo(-47f, 54f, -74f, 28f, -68f, -6f + motion * 3f)
        quadraticTo(-55f, 18f, -19f, 16f)
        close()
    }
    earlyFlame(-68f, -17f + motion * 2f, motion)
    kantoOval(skin, -23f, -6f, 49f, 52f)
    kantoOval(EARLY_CREAM, -13f, 2f, 29f, 38f)
    for (side in listOf(-1f, 1f)) {
        kantoOval(skin, side * 21f - 13f, 34f, 29f, 15f)
        earlyClaws(side * 24f, 43f)
        kantoPolygon(
            skin, side * 18f, -3f, side * 36f, 5f + motion * side * 3f,
            side * 39f, 19f + motion * side * 3f, side * 30f, 21f,
            side * 23f, 10f,
        )
        earlyClaws(side * 34f, 16f + motion * side * 3f, 4f)
    }
    if (winged) {
        kantoPolygon(skin, -19f, -36f, -23f, -63f, -7f, -47f)
        kantoPolygon(skin, 9f, -45f, 23f, -63f, 22f, -31f)
        kantoOval(skin, -23f, -49f, 48f, 44f)
        kantoOval(skin, -5f, -29f, 39f, 24f)
        earlyEye(-11f, -33f, 5f, 0xFF428B91)
        earlyEye(13f, -33f, 5f, 0xFF428B91)
        kantoSmile(12f, -16f, 14f)
        kantoPolygon(EARLY_WHITE, 20f, -16f, 26f, -18f, 23f, -10f)
    } else {
        kantoPolygon(skin, -22f, -29f, -26f, -61f, -5f, -45f)
        kantoOval(skin, -24f, -48f, 51f, 43f)
        kantoOval(skin, 3f, -29f, 36f, 23f)
        earlyEye(-10f, -31f, 5f)
        earlyEye(15f, -31f, 5f)
        kantoLine(KANTO_INK, 2.5f, -16f, -38f, -5f, -34f)
        kantoLine(KANTO_INK, 2.5f, 10f, -34f, 20f, -38f)
        kantoSmile(14f, -16f, 14f)
        kantoPolygon(EARLY_WHITE, 23f, -16f, 29f, -18f, 25f, -10f)
    }
}

private fun DrawScope.earlyTurtle(cannons: Boolean, motion: Float) {
    val skin = if (cannons) 0xFF689BBC else 0xFF89BCE1
    if (!cannons) {
        earlyShape(0xFFDAE9FA) {
            moveTo(23f, 30f)
            cubicTo(72f, 49f, 83f, -6f, 57f, -11f + motion * 3f)
            cubicTo(64f, 9f, 31f, -7f, 35f, 17f)
            close()
        }
        earlyCurve(0xFF789ABB) {
            moveTo(44f, 29f); cubicTo(71f, 32f, 69f, 5f, 54f, 9f)
            quadraticTo(44f, 13f, 54f, 19f)
        }
    }
    kantoOval(0xFF9C755A, if (cannons) -42f else -27f, -22f, if (cannons) 85f else 66f, 63f)
    kantoOval(EARLY_CREAM, if (cannons) -37f else -27f, -16f, if (cannons) 74f else 59f, 59f)
    if (cannons) {
        for (side in listOf(-1f, 1f)) {
            kantoPolygon(
                0xFF91A5B8, side * 24f, -15f, side * 30f, -59f,
                side * 47f, -62f, side * 46f, -16f,
            )
            kantoOval(0xFFBDD0DF, side * 39f - 11f, -64f, 22f, 13f)
            kantoOval(KANTO_INK, side * 39f - 6f, -61f, 12f, 7f)
            kantoOval(skin, side * 39f - 15f, -4f + motion * side * 2f, 30f, 35f)
            earlyClaws(side * 45f, 23f + motion * side * 2f)
            kantoOval(skin, side * 26f - 15f, 34f, 31f, 15f)
            earlyClaws(side * 29f, 44f)
        }
    } else {
        for (side in listOf(-1f, 1f)) {
            kantoOval(skin, side * 30f - 10f, 3f + side * motion * 4f, 21f, 23f)
            kantoOval(skin, side * 18f - 11f, 34f, 25f, 14f)
            earlyClaws(side * 19f, 43f, 4f)
            earlyShape(0xFFD3E4F6) {
                moveTo(side * 18f, -31f)
                quadraticTo(side * 46f, -30f, side * 42f, -61f)
                quadraticTo(side * 31f, -47f, side * 15f, -48f)
                close()
            }
            kantoLine(0xFF7B9BC0, 2f, side * 22f, -35f, side * 35f, -48f)
        }
    }
    kantoOval(0xFFE4CC8A, -23f, -3f, 47f, 45f)
    for (y in listOf(8f, 21f, 32f)) kantoLine(0xFFA78E59, 2f, -19f, y, 19f, y)
    kantoLine(0xFFA78E59, 2f, 0f, -1f, 0f, 39f)
    kantoOval(skin, -27f, -44f, 54f, 45f)
    earlyEye(-14f, -25f, 6f, if (cannons) 0xFF835853 else 0xFF945868)
    earlyEye(14f, -25f, 6f, if (cannons) 0xFF835853 else 0xFF945868)
    kantoSmile(0f, -10f, 16f)
    if (!cannons) {
        kantoPolygon(EARLY_WHITE, -16f, -10f, -10f, -8f, -13f, -3f)
        kantoPolygon(EARLY_WHITE, 10f, -8f, 16f, -10f, 13f, -3f)
    }
}

private fun DrawScope.earlyCaterpie(motion: Float) {
    for (index in 3 downTo 0) {
        val x = -9f + index * 17f
        val y = 8f + index * 6f + motion * index
        kantoOval(0xFF88C86A, x - 13f, y - 14f, 29f, 31f)
        kantoOval(0xFFE7D982, x - 6f, y + 7f, 14f, 10f)
        if (index > 0) {
            kantoOval(0xFFCFDD88, x - 6f, y - 8f, 12f, 12f)
            drawCircle(Color(0xFF668F4D), 3f, Offset(x, y - 2f))
        }
    }
    kantoOval(0xFFF1D474, -40f, -3f, 44f, 18f)
    kantoOval(0xFF80C65C, -46f, -38f, 47f, 43f)
    kantoLine(KANTO_INK, 9f, -24f, -33f, -25f, -53f, -36f, -62f)
    kantoLine(0xFFDE626B, 6f, -24f, -33f, -25f, -53f, -36f, -62f)
    kantoLine(KANTO_INK, 9f, -25f, -53f, -12f, -61f)
    kantoLine(0xFFDE626B, 6f, -25f, -53f, -12f, -61f)
    earlyEye(-32f, -19f, 8f)
    earlyEye(-9f, -18f, 5f)
    kantoOval(0xFFF0BD74, -38f, -7f, 13f, 8f)
    kantoSmile(-17f, -2f, 8f)
}

private fun DrawScope.earlyWeedle(motion: Float) {
    kantoPolygon(EARLY_WHITE, 41f, 16f, 65f, 0f + motion * 3f, 54f, 30f)
    for (index in 4 downTo 0) {
        val x = -10f + index * 14f
        val y = 12f + index * 3f + motion * index * 0.6f
        kantoOval(0xFFD9A777, x - 11f, y - 12f, 25f, 27f)
        kantoOval(0xFFDF8C9C, x - 6f, y + 8f, 12f, 10f)
    }
    kantoOval(0xFFE5B578, -42f, -29f, 44f, 43f)
    kantoPolygon(EARLY_WHITE, -30f, -26f, -17f, -61f, -10f, -26f)
    kantoEyes(-20f, -9f, 10f, 3.7f)
    kantoOval(0xFFDF8C9C, -30f, -1f, 23f, 17f)
}

private fun DrawScope.earlyCocoon(golden: Boolean) {
    if (golden) {
        kantoPolygon(
            0xFFE6C65C, 0f, -62f, 26f, -41f, 27f, -12f, 19f, 32f,
            0f, 49f, -18f, 32f, -27f, -12f, -26f, -41f,
        )
        kantoPolygon(0xFFD0A53E, 0f, 5f, 19f, -5f, 13f, 34f, 0f, 49f)
        kantoPolygon(0xFFEDD97A, -24f, -22f, -3f, -14f, 0f, 5f, -20f, -1f)
        kantoPolygon(0xFFEDD97A, 24f, -22f, 3f, -14f, 0f, 5f, 20f, -1f)
        kantoPolygon(KANTO_INK, -21f, -34f, -5f, -28f, -7f, -18f, -18f, -22f)
        kantoPolygon(KANTO_INK, 21f, -34f, 5f, -28f, 7f, -18f, 18f, -22f)
        kantoLine(0xFF9C7C32, 2f, -17f, 14f, 0f, 21f, 17f, 14f)
        kantoLine(0xFF9C7C32, 2f, -14f, 28f, 0f, 34f, 14f, 28f)
    } else {
        kantoPolygon(
            0xFF8CC765, -13f, -62f, 22f, -41f, 29f, -6f, 12f, 47f,
            -10f, 35f, -40f, -4f, -25f, -20f, -30f, -46f,
        )
        kantoPolygon(0xFF6C9D4F, -40f, -4f, -6f, 4f, 12f, 47f, -10f, 35f)
        kantoPolygon(0xFFA7D77A, -13f, -62f, 3f, -30f, -6f, 4f, 29f, -6f, 22f, -41f)
        kantoLine(0xFF4F793E, 2.5f, -25f, -20f, -6f, -9f, 17f, -17f)
        kantoPolygon(EARLY_WHITE, -21f, -26f, -7f, -24f, -12f, -13f)
        kantoLine(KANTO_INK, 3f, -13f, -22f, -14f, -17f)
        kantoLine(0xFF4F793E, 2f, -24f, 11f, -9f, 16f, 5f, 13f)
    }
}

private fun DrawScope.earlyWingedInsect(moth: Boolean, motion: Float) {
    for (side in listOf(-1f, 1f)) {
        val lift = motion * 5f
        if (moth) {
            earlyShape(0xFFD4B9E5) {
                moveTo(side * 8f, -15f)
                lineTo(side * 78f, -61f + lift)
                quadraticTo(side * 87f, -17f, side * 59f, 7f)
                lineTo(side * 13f, 14f)
                close()
            }
            earlyShape(0xFFBA99D3) {
                moveTo(side * 11f, 5f)
                quadraticTo(side * 53f, -2f, side * 65f, 16f + lift)
                lineTo(side * 39f, 44f)
                lineTo(side * 10f, 27f)
                close()
            }
            kantoLine(0xFF9874B4, 2.5f, side * 16f, -9f, side * 68f, -43f + lift)
            kantoLine(0xFF9874B4, 2.5f, side * 24f, -7f, side * 59f, -7f + lift)
            kantoLine(0xFF9874B4, 2.5f, side * 17f, 16f, side * 41f, 33f)
        } else {
            earlyShape(0xFFF5F1F8) {
                moveTo(side * 9f, -15f)
                cubicTo(side * 39f, -62f + lift, side * 82f, -64f + lift, side * 82f, -32f)
                quadraticTo(side * 82f, -4f, side * 43f, 7f)
                quadraticTo(side * 87f, 17f, side * 56f, 43f)
                quadraticTo(side * 31f, 57f, side * 11f, 24f)
                close()
            }
            earlyCurve(0xFF57516A, 4f) {
                moveTo(side * 15f, -8f)
                quadraticTo(side * 42f, -48f + lift, side * 70f, -43f + lift)
                lineTo(side * 59f, -13f)
                lineTo(side * 20f, 0f)
                moveTo(side * 37f, -32f); lineTo(side * 43f, -8f)
                moveTo(side * 18f, 12f); lineTo(side * 49f, 17f)
                lineTo(side * 41f, 38f); lineTo(side * 23f, 23f)
            }
        }
    }
    if (moth) {
        for (i in 3 downTo 0) kantoOval(0xFFE5CBA0, -10f, 4f + i * 8f, 20f, 18f)
        kantoPolygon(0xFFB89ACE, -11f, -23f, -22f, -47f, -9f, -37f, 0f, -60f, 8f, -37f, 22f, -47f, 12f, -21f)
        kantoOval(0xFFC7A7DC, -22f, -34f, 44f, 38f)
        kantoPolygon(0xFFDD96BE, -20f, -24f, -5f, -16f, -6f, -1f, -20f, -10f)
        kantoPolygon(0xFFDD96BE, 20f, -24f, 5f, -16f, 6f, -1f, 20f, -10f)
        kantoLine(KANTO_INK, 2f, -14f, -17f, -13f, -9f)
        kantoLine(KANTO_INK, 2f, 14f, -17f, 13f, -9f)
        for (side in listOf(-1f, 1f)) {
            kantoLine(0xFF866AA3, 3f, side * 8f, 4f, side * 23f, 14f)
            kantoLine(0xFF866AA3, 3f, side * 9f, 14f, side * 20f, 27f)
        }
    } else {
        kantoOval(0xFF9B88C4, -12f, -5f, 24f, 45f)
        kantoOval(0xFFAA92CF, -20f, -31f, 40f, 37f)
        kantoOval(0xFFDE778E, -23f, -26f, 19f, 26f)
        kantoOval(0xFFDE778E, 4f, -26f, 19f, 26f)
        drawCircle(Color.White, 3.4f, Offset(-14f, -18f))
        drawCircle(Color.White, 3.4f, Offset(13f, -18f))
        kantoLine(KANTO_INK, 3f, -8f, -30f, -16f, -48f, -24f, -53f)
        kantoLine(KANTO_INK, 3f, 8f, -30f, 16f, -48f, 24f, -53f)
        kantoOval(0xFF91BEDC, -7f, -3f, 14f, 11f)
        for (side in listOf(-1f, 1f)) {
            kantoOval(0xFF91BEDC, side * 13f - 5f, 9f, 10f, 16f)
            kantoOval(0xFF91BEDC, side * 9f - 5f, 33f, 10f, 15f)
        }
    }
}

private fun DrawScope.earlyBeedrill(motion: Float) {
    for (side in listOf(-1f, 1f)) {
        earlyShape(0xFFDCEBF1) {
            moveTo(side * 7f, -14f)
            cubicTo(side * 27f, -65f + motion * 3f, side * 74f, -59f, side * 48f, -19f)
            quadraticTo(side * 29f, -6f, side * 7f, -5f)
            close()
        }
        kantoLine(0xFF9BB6C8, 2f, side * 15f, -14f, side * 44f, -43f + motion * 3f)
        kantoLine(KANTO_INK, 3f, side * 10f, 19f, side * 30f, 30f, side * 25f, 42f)
    }
    kantoPolygon(EARLY_WHITE, -8f, 34f, 9f, 34f, 2f, 51f)
    kantoOval(0xFFF3CC5D, -18f, -11f, 36f, 48f)
    earlyShape(KANTO_INK, outline = false) {
        moveTo(-17f, 5f); quadraticTo(0f, 12f, 17f, 5f)
        lineTo(17f, 14f); quadraticTo(0f, 22f, -17f, 14f); close()
    }
    earlyShape(KANTO_INK, outline = false) {
        moveTo(-14f, 25f); quadraticTo(0f, 31f, 14f, 25f)
        quadraticTo(4f, 44f, -10f, 32f); close()
    }
    kantoOval(0xFFF3CC5D, -20f, -42f, 40f, 35f)
    kantoOval(0xFFCC5671, -20f, -36f, 16f, 23f)
    kantoOval(0xFFCC5671, 4f, -36f, 16f, 23f)
    kantoLine(KANTO_INK, 3f, -9f, -41f, -19f, -57f, -26f, -59f)
    kantoLine(KANTO_INK, 3f, 9f, -41f, 19f, -57f, 26f, -59f)
    for (side in listOf(-1f, 1f)) {
        val dy = side * motion * 4f
        kantoLine(KANTO_INK, 7f, side * 16f, -8f, side * 35f, -1f + dy, side * 39f, 11f + dy)
        kantoOval(0xFFF3CC5D, side * 38f - 9f, 1f + dy, 18f, 19f)
        kantoPolygon(EARLY_WHITE, side * 29f, 14f + dy, side * 45f, 6f + dy, side * 69f, 40f + dy)
        kantoLine(0xFFA8B0BD, 1.7f, side * 38f, 14f + dy, side * 64f, 35f + dy)
    }
}

private fun DrawScope.earlyBirdFeet(y: Float = 44f) {
    for (side in listOf(-1f, 1f)) {
        kantoLine(0xFFBD7962, 4f, side * 14f, y - 10f, side * 16f, y + 3f)
        kantoLine(0xFFBD7962, 3f, side * 9f, y + 4f, side * 16f, y + 1f, side * 23f, y + 4f)
    }
}

private fun DrawScope.earlyCrestedBird(stage: Int, motion: Float) {
    val feathers = 0xFFC09865
    if (stage > 0) {
        kantoPolygon(0xFFBF6454, -6f, 27f, 30f, 48f, 42f, 44f, 22f, 26f)
        kantoPolygon(0xFFE4BC65, 5f, 26f, 43f, 42f, 51f, 36f, 22f, 20f)
        for (side in listOf(-1f, 1f)) {
            val reach = if (stage == 2) 93f else 70f
            val lift = motion * 5f
            kantoPolygon(
                feathers, side * 14f, -8f, side * (reach - 18f), -31f + lift,
                side * reach, -34f + lift, side * (reach - 6f), -20f + lift,
                side * (reach - 13f), -12f + lift, side * (reach - 9f), -4f + lift,
                side * (reach - 24f), -2f + lift, side * (reach - 22f), 8f + lift,
                side * (reach - 41f), 10f + lift, side * 21f, 24f,
            )
            kantoLine(0xFF7F634D, 2.5f, side * 28f, 1f, side * (reach - 14f), -23f + lift)
            kantoPolygon(EARLY_CREAM, side * 19f, -3f, side * (reach - 21f), -22f + lift, side * 32f, 11f)
        }
    } else {
        kantoPolygon(0xFF8A644A, 14f, 23f, 49f, 20f, 34f, 38f, 11f, 34f)
    }
    earlyBirdFeet()
    kantoOval(feathers, -28f, -12f, 56f, 54f)
    kantoOval(EARLY_CREAM, -20f, -5f, 40f, 44f)
    if (stage == 0) {
        for (side in listOf(-1f, 1f)) {
            earlyShape(feathers) {
                moveTo(side * 22f, -3f)
                quadraticTo(side * 45f, 1f + side * motion * 3f, side * 35f, 27f)
                lineTo(side * 23f, 20f); close()
            }
            kantoLine(0xFF846246, 2f, side * 29f, 8f, side * 32f, 20f)
        }
        kantoPolygon(feathers, -26f, -22f, -25f, -47f, -12f, -39f, 1f, -50f, 16f, -38f, 24f, -41f, 26f, -20f)
    } else if (stage == 1) {
        kantoPolygon(0xFFC86155, -24f, -27f, -27f, -61f, -15f, -51f, -2f, -64f, 11f, -49f, 25f, -53f, 20f, -29f)
    } else {
        earlyShape(0xFFEBC565) {
            moveTo(-24f, -32f)
            cubicTo(-9f, -67f, 37f, -67f, 76f, -49f + motion * 3f)
            quadraticTo(42f, -44f, 16f, -28f)
            close()
        }
        earlyShape(0xFFC56356) {
            moveTo(-13f, -38f)
            quadraticTo(21f, -60f, 62f, -51f + motion * 3f)
            quadraticTo(32f, -43f, 12f, -29f); close()
        }
    }
    kantoOval(feathers, -27f, -39f, 54f, 39f)
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(EARLY_CREAM, side * 2f, -25f, side * 23f, -31f, side * 21f, -6f, side * 3f, -3f)
        kantoLine(0xFF644C41, 4f, side * 5f, -22f, side * 25f, -29f)
        earlyEye(side * 13f, -19f, 4.7f)
    }
    kantoPolygon(0xFFD99683, -8f, -9f, 8f, -9f, 0f, 4f)
}

private fun DrawScope.earlyRodent(large: Boolean, motion: Float) {
    val fur = if (large) 0xFFC89957 else 0xFFA48CC9
    earlyCurve(KANTO_INK, if (large) 9f else 8f) {
        moveTo(34f, 27f)
        if (large) {
            cubicTo(77f, 38f, 74f, -2f, 87f, -18f + motion * 5f)
        } else {
            cubicTo(85f, 41f, 91f, -19f, 67f, -18f + motion * 3f)
            cubicTo(45f, -18f, 53f, 8f, 70f, 0f)
        }
    }
    earlyCurve(if (large) 0xFFD7B183 else 0xFFB69BD7, if (large) 6f else 5f) {
        moveTo(34f, 27f)
        if (large) {
            cubicTo(77f, 38f, 74f, -2f, 87f, -18f + motion * 5f)
        } else {
            cubicTo(85f, 41f, 91f, -19f, 67f, -18f + motion * 3f)
            cubicTo(45f, -18f, 53f, 8f, 70f, 0f)
        }
    }
    kantoOval(fur, if (large) -32f else -16f, if (large) -19f else -4f, if (large) 91f else 72f, if (large) 65f else 43f)
    for (x in listOf(-26f, 30f)) {
        kantoOval(if (large) 0xFFD9B282 else 0xFFD4B4CC, x - 13f, 35f, 30f, 14f)
        earlyClaws(x, 43f, 5f)
    }
    if (large) {
        kantoOval(fur, -49f, -43f, 23f, 26f)
        kantoOval(0xFFD18F86, -43f, -38f, 12f, 17f)
        kantoOval(fur, -13f, -43f, 24f, 26f)
        kantoOval(0xFFD18F86, -7f, -38f, 12f, 17f)
        kantoOval(fur, -52f, -29f, 62f, 51f)
        kantoOval(EARLY_CREAM, -50f, -1f, 70f, 38f)
        earlyEye(-36f, -12f, 6f)
        earlyEye(-9f, -12f, 6f)
        kantoPolygon(EARLY_WHITE, -30f, 12f, -7f, 12f, -7f, 31f, -27f, 30f)
        kantoLine(KANTO_INK, 2f, -18f, 14f, -18f, 29f)
        kantoOval(0xFFBC7D70, -31f, 1f, 18f, 12f)
        for (side in listOf(-1f, 1f)) {
            val root = -20f + side * 25f
            kantoLine(EARLY_CREAM, 3f, root, 9f, root + side * 25f, -1f)
            kantoLine(EARLY_CREAM, 3f, root, 14f, root + side * 27f, 17f)
        }
    } else {
        kantoOval(fur, -49f, -48f, 27f, 31f)
        kantoOval(0xFFE0ACC5, -43f, -42f, 15f, 20f)
        kantoOval(fur, -13f, -43f, 26f, 29f)
        kantoOval(0xFFE0ACC5, -7f, -38f, 14f, 18f)
        kantoOval(fur, -49f, -29f, 60f, 48f)
        kantoOval(EARLY_CREAM, -47f, -1f, 48f, 29f)
        earlyEye(-35f, -12f, 6f, 0xFFC65F7B)
        earlyEye(-8f, -12f, 6f, 0xFFC65F7B)
        kantoPolygon(EARLY_WHITE, -32f, 13f, -12f, 13f, -15f, 27f, -29f, 27f)
        kantoLine(KANTO_INK, 2f, -22f, 14f, -22f, 25f)
        kantoOval(0xFFB87696, -33f, 2f, 17f, 10f)
        kantoLine(KANTO_INK, 1.8f, -41f, 6f, -61f, -1f)
        kantoLine(KANTO_INK, 1.8f, -40f, 11f, -61f, 14f)
        kantoLine(KANTO_INK, 1.8f, -6f, 7f, 13f, 1f)
    }
}

private fun DrawScope.earlyLongBeakBird(longNeck: Boolean, motion: Float) {
    val brown = 0xFFA77956
    earlyBirdFeet()
    if (longNeck) {
        kantoPolygon(0xFF745747, -5f, 24f, -63f, 47f, -45f, 26f, -18f, 12f)
        for (side in listOf(-1f, 1f)) {
            val lift = motion * 5f
            kantoPolygon(
                brown, side * 12f, 9f, side * 58f, -28f + lift, side * 90f, -39f + lift,
                side * 82f, -17f + lift, side * 69f, -12f + lift,
                side * 73f, -3f + lift, side * 57f, 4f, side * 61f, 12f,
                side * 33f, 27f, side * 14f, 31f,
            )
            kantoLine(0xFF624A3B, 2.5f, side * 24f, 14f, side * 77f, -21f + lift)
        }
        kantoOval(brown, -21f, 3f, 54f, 38f)
        earlyShape(0xFFD5B67C) {
            moveTo(-9f, 27f)
            cubicTo(-47f, 18f, -15f, -9f, -34f, -25f)
            lineTo(-19f, -33f)
            cubicTo(6f, -11f, -27f, 6f, 14f, 15f)
            close()
        }
        kantoPolygon(0xFFC55E53, -44f, -36f, -46f, -56f, -35f, -51f, -28f, -63f, -18f, -48f, -11f, -50f, -11f, -34f)
        kantoOval(brown, -45f, -44f, 38f, 28f)
        kantoPolygon(0xFFD8BA82, -12f, -35f, 41f, -24f, -13f, -21f)
        kantoLine(0xFF8A6E4C, 1.8f, -9f, -28f, 30f, -25f)
        earlyEye(-25f, -33f, 5.5f)
        kantoLine(KANTO_INK, 2f, -33f, -40f, -20f, -36f)
    } else {
        kantoPolygon(0xFF74584C, -8f, 27f, 39f, 42f, 35f, 24f, 11f, 14f)
        for (side in listOf(-1f, 1f)) {
            val lift = motion * 4f
            kantoPolygon(
                0xFFB9755D, side * 18f, 0f, side * 51f, -22f + lift,
                side * 65f, -17f + lift, side * 54f, -5f,
                side * 63f, 0f, side * 49f, 10f, side * 52f, 19f, side * 24f, 26f,
            )
        }
        kantoOval(brown, -26f, -10f, 52f, 50f)
        kantoOval(0xFFE5C58B, -17f, 0f, 36f, 37f)
        kantoPolygon(
            0xFF795247, -26f, -8f, -31f, -30f, -25f, -43f, -17f, -36f,
            -9f, -50f, 0f, -41f, 10f, -47f, 21f, -32f, 26f, -11f,
            14f, -3f, 6f, 1f, -3f, -4f, -12f, 0f,
        )
        kantoOval(0xFFD6A371, -20f, -30f, 44f, 26f)
        earlyEye(-8f, -19f, 5f)
        earlyEye(13f, -19f, 5f)
        kantoPolygon(0xFFD89080, 17f, -16f, 44f, -11f, 19f, -5f)
        kantoLine(0xFF745343, 2f, -10f, 6f, -6f, 13f)
        kantoLine(0xFF745343, 2f, 2f, 9f, 5f, 16f)
        kantoLine(0xFF745343, 2f, 12f, 6f, 13f, 13f)
    }
}

private fun DrawScope.earlySnake(hood: Boolean, motion: Float) {
    val violet = if (hood) 0xFFA28CC5 else 0xFFB19AD0
    kantoOval(violet, -49f, 22f, 101f, 26f)
    kantoOval(0xFF8F77AD, -31f, 25f, 64f, 15f)
    earlyShape(violet) {
        moveTo(0f, 33f)
        cubicTo(18f, 45f, 49f, 34f, 61f, 22f + motion * 4f)
        quadraticTo(50f, 52f, -6f, 44f)
        close()
    }
    if (hood) {
        earlyShape(violet) {
            moveTo(-12f, 31f)
            cubicTo(-16f, 11f, -50f, 6f, -49f, -22f)
            cubicTo(-48f, -60f, -20f, -58f, 0f, -48f)
            cubicTo(20f, -58f, 48f, -60f, 49f, -22f)
            cubicTo(50f, 6f, 16f, 11f, 12f, 31f)
            close()
        }
        for (side in listOf(-1f, 1f)) {
            kantoPolygon(0xFFF3CC73, side * 14f, -24f, side * 37f, -28f, side * 31f, -8f, side * 15f, -8f)
            kantoOval(0xFFCB6973, side * 26f - 6f, -23f, 12f, 12f)
            drawCircle(Color(KANTO_INK), 3f, Offset(side * 26f, -17f))
        }
        kantoPolygon(0xFFDA7B83, -26f, 1f, 0f, 8f, 26f, 1f, 14f, 16f, -14f, 16f)
        kantoLine(KANTO_INK, 3f, -20f, 3f, 0f, 11f, 20f, 3f)
        kantoOval(violet, -23f, -59f, 46f, 32f)
        kantoPolygon(EARLY_WHITE, -19f, -49f, -5f, -44f, -11f, -39f, -19f, -40f)
        kantoPolygon(EARLY_WHITE, 19f, -49f, 5f, -44f, 11f, -39f, 19f, -40f)
        kantoLine(KANTO_INK, 2f, -12f, -45f, -12f, -41f)
        kantoLine(KANTO_INK, 2f, 12f, -45f, 12f, -41f)
        kantoSmile(0f, -34f, 12f)
        kantoPolygon(EARLY_CREAM, -10f, 20f, 11f, 20f, 12f, 32f, -5f, 37f)
        kantoLine(0xFFA88964, 2f, -8f, 27f, 11f, 27f)
    } else {
        earlyShape(violet) {
            moveTo(-13f, 36f)
            cubicTo(27f, 35f, -24f, -2f, -15f, -28f)
            lineTo(9f, -28f)
            cubicTo(-4f, -1f, 40f, 28f, 14f, 39f)
            close()
        }
        kantoPolygon(0xFFE7CB82, -15f, -15f, 8f, -15f, 11f, -3f, -11f, -3f)
        kantoOval(violet, -32f, -49f, 58f, 36f)
        kantoOval(0xFFE8CE87, -24f, -39f, 15f, 15f)
        kantoOval(0xFFE8CE87, 4f, -39f, 15f, 15f)
        kantoLine(KANTO_INK, 2.5f, -16f, -36f, -16f, -28f)
        kantoLine(KANTO_INK, 2.5f, 11f, -36f, 11f, -28f)
        kantoSmile(-2f, -22f, 15f)
        kantoLine(0xFFC96F81, 2.5f, -2f, -18f, -2f, -8f, -8f, -3f)
        kantoLine(0xFFC96F81, 2.5f, -2f, -8f, 4f, -3f)
    }
}

private fun DrawScope.earlyRaichu(motion: Float) {
    kantoLine(KANTO_INK, 4f, 17f, 27f, 45f, 34f, 51f, 6f, 69f, -2f + motion * 3f)
    kantoPolygon(
        0xFFF0CB68, 64f, 1f + motion * 3f, 53f, -16f, 68f, -16f,
        75f, -58f + motion * 3f, 91f, -38f, 80f, -36f,
        83f, -8f, 70f, -12f,
    )
    kantoOval(0xFFE8A657, -28f, -9f, 59f, 53f)
    kantoOval(EARLY_CREAM, -19f, 3f, 39f, 37f)
    for (side in listOf(-1f, 1f)) {
        kantoOval(0xFF80604B, side * 21f - 13f, 36f, 26f, 13f)
        kantoOval(0xFFE8A657, side * 29f - 9f, 1f + motion * side * 4f, 18f, 23f)
        earlyShape(0xFF80604B) {
            moveTo(side * 14f, -26f)
            quadraticTo(side * 11f, -59f, side * 39f, -64f)
            quadraticTo(side * 27f, -50f, side * 38f, -34f)
            quadraticTo(side * 32f, -22f, side * 14f, -26f)
            close()
        }
        earlyShape(0xFFE3BD65, outline = false) {
            moveTo(side * 20f, -29f)
            quadraticTo(side * 18f, -47f, side * 31f, -54f)
            quadraticTo(side * 26f, -40f, side * 32f, -34f)
            close()
        }
    }
    kantoOval(0xFFE8A657, -32f, -38f, 64f, 43f)
    kantoEyes(0f, -20f, 15f, 4f)
    kantoOval(0xFFF8DA77, -30f, -12f, 14f, 14f)
    kantoOval(0xFFF8DA77, 16f, -12f, 14f, 14f)
    kantoPolygon(KANTO_INK, -3f, -13f, 3f, -13f, 0f, -9f)
    kantoSmile(0f, -6f, 10f)
}

private fun DrawScope.earlySandArmor(spiky: Boolean, motion: Float) {
    val gold = 0xFFE5C56C
    if (spiky) {
        kantoPolygon(
            0xFFAD8552, -25f, 8f, -49f, -8f, -35f, -13f, -44f, -37f,
            -24f, -29f, -25f, -59f, -5f, -45f, 9f, -65f, 19f, -43f,
            40f, -61f, 37f, -35f, 61f, -43f, 51f, -17f,
            67f, -16f, 50f, 10f, 59f, 28f, 35f, 30f,
        )
        for (x in listOf(-15f, 6f, 28f)) {
            kantoLine(0xFF80623E, 2f, x, -34f, x + 8f, -12f, x + 2f, 12f)
        }
        kantoPolygon(gold, 27f, 23f, 67f, 16f + motion * 3f, 48f, 40f, 20f, 36f)
        kantoOval(gold, -28f, -11f, 64f, 55f)
        kantoOval(EARLY_CREAM, -15f, 2f, 34f, 37f)
        for (side in listOf(-1f, 1f)) {
            kantoOval(gold, side * 24f - 14f, 35f, 28f, 14f)
            earlyClaws(side * 25f, 43f)
            kantoOval(gold, side * 31f - 12f, 1f + side * motion * 2f, 24f, 23f)
            for (i in 0..1) {
                val x = side * (32f + i * 8f)
                kantoPolygon(EARLY_WHITE, x - 5f, 16f, x + 5f, 15f, x + side * 6f, 35f)
            }
        }
        kantoPolygon(gold, -25f, -19f, -24f, -47f, -9f, -31f)
        kantoPolygon(gold, 6f, -29f, 23f, -45f, 21f, -11f)
        kantoOval(gold, -27f, -31f, 51f, 35f)
        kantoPolygon(gold, -14f, -7f, 8f, -7f, -3f, 10f)
        earlyEye(-15f, -17f, 5f)
        earlyEye(11f, -17f, 5f)
        kantoLine(KANTO_INK, 2.5f, -21f, -23f, -10f, -20f)
        kantoLine(KANTO_INK, 2.5f, 6f, -20f, 16f, -24f)
        drawCircle(Color(KANTO_INK), 2.5f, Offset(-3f, 2f))
    } else {
        kantoPolygon(gold, 21f, 22f, 56f, 26f + motion * 3f, 44f, 43f, 19f, 38f)
        kantoOval(gold, -28f, -12f, 64f, 56f)
        for (y in listOf(-2f, 12f, 26f)) {
            kantoLine(0xFFAF8A45, 2f, -17f, y, 27f, y)
        }
        for ((x, y) in listOf(-7f to -9f, 14f to -9f, 3f to -2f, 23f to -2f, -9f to 12f, 14f to 12f, 3f to 26f)) {
            kantoLine(0xFFAF8A45, 2f, x, y, x, y + 13f)
        }
        for (side in listOf(-1f, 1f)) {
            kantoOval(gold, side * 23f - 11f, 34f, 23f, 14f)
            earlyClaws(side * 24f, 42f, 5f)
            kantoOval(gold, side * 28f - 9f, 9f, 18f, 20f)
            earlyClaws(side * 29f, 23f, 4f)
        }
        kantoPolygon(gold, -32f, -25f, -36f, -51f, -15f, -34f)
        kantoPolygon(gold, 1f, -34f, 15f, -50f, 19f, -22f)
        kantoOval(gold, -35f, -39f, 55f, 41f)
        kantoOval(EARLY_CREAM, -28f, -18f, 41f, 26f)
        kantoLine(0xFFAF8A45, 2f, -8f, -38f, -8f, -21f)
        kantoLine(0xFFAF8A45, 2f, -30f, -29f, 14f, -29f)
        earlyEye(-23f, -19f, 5.5f)
        earlyEye(7f, -19f, 5.5f)
        kantoPolygon(KANTO_INK, -12f, -10f, -5f, -10f, -9f, -5f)
        kantoSmile(-9f, 0f, 8f)
    }
}

private fun DrawScope.earlyNido(stage: Int, male: Boolean, motion: Float) {
    val skin = if (male) 0xFFB397C6 else 0xFF88BACA
    val dark = if (male) 0xFF7C6699 else 0xFF568EAD
    val ear = if (male) 0xFF76A7A7 else 0xFFAD82AC
    if (stage == 2) {
        earlyShape(skin) {
            moveTo(17f, 32f)
            quadraticTo(48f, 38f, 69f, 10f + motion * 3f)
            quadraticTo(66f, 45f, 25f, 47f)
            close()
        }
        kantoPolygon(dark, 41f, 30f, 43f, 14f, 53f, 25f)
        if (male) {
            kantoPolygon(dark, 24f, -20f, 40f, -31f, 39f, -11f, 53f, -13f, 45f, 7f, 55f, 16f, 36f, 23f)
        } else {
            kantoPolygon(dark, 24f, -12f, 37f, -19f, 36f, -2f, 46f, 5f, 34f, 14f)
        }
        kantoOval(skin, -32f, -15f, 65f, 61f)
        if (male) {
            kantoPolygon(EARLY_CREAM, -16f, -7f, 15f, -7f, 23f, 24f, 12f, 39f, -15f, 39f, -24f, 24f)
            kantoLine(0xFFBAA887, 2f, -18f, 16f, 19f, 16f)
        } else {
            kantoOval(EARLY_CREAM, -24f, -6f, 47f, 20f)
            kantoOval(EARLY_CREAM, -20f, 10f, 41f, 33f)
            kantoLine(0xFFB5A884, 2f, -14f, 30f, 15f, 30f)
        }
        for (side in listOf(-1f, 1f)) {
            kantoOval(skin, side * 27f - 16f, 31f, 34f, 19f)
            earlyClaws(side * 30f, 44f, 7f)
            kantoPolygon(
                skin, side * 25f, -6f, side * 45f, 1f, side * 53f, 22f + motion * side * 3f,
                side * 37f, 27f, side * 28f, 12f,
            )
            earlyClaws(side * 45f, 20f + motion * side * 3f, 5f)
            kantoPolygon(skin, side * 13f, -29f, side * 31f, -60f, side * 35f, -33f, side * 26f, -20f)
            kantoPolygon(ear, side * 22f, -32f, side * 29f, -49f, side * 29f, -29f)
        }
        kantoOval(skin, -27f, -45f, 55f, 36f)
        if (male) {
            kantoPolygon(skin, -9f, -38f, 3f, -66f, 14f, -39f)
            kantoPolygon(skin, -28f, -22f, -15f, -10f, -20f, -2f, 0f, -6f, 20f, -2f, 15f, -10f, 29f, -22f)
            kantoPolygon(EARLY_WHITE, -20f, -13f, -13f, -12f, -16f, -5f)
            kantoPolygon(EARLY_WHITE, 13f, -12f, 20f, -13f, 16f, -5f)
        } else {
            kantoPolygon(skin, -7f, -40f, 0f, -55f, 8f, -40f)
            kantoOval(skin, -23f, -23f, 47f, 22f)
        }
        earlyEye(-15f, -28f, 5.5f, 0xFFAC565F)
        earlyEye(16f, -28f, 5.5f, 0xFFAC565F)
        kantoLine(KANTO_INK, 2f, -22f, -34f, -11f, -30f)
        kantoLine(KANTO_INK, 2f, 11f, -30f, 23f, -34f)
        kantoSmile(0f, -13f, 10f)
        return
    }
    val grown = stage == 1
    val bodyY = if (grown) -5f else 8f
    kantoPolygon(skin, 27f, 24f, 54f, 13f + motion * 3f, 45f, 37f, 24f, 35f)
    if (grown || male) {
        kantoPolygon(
            dark, -4f, bodyY + 6f, 3f, bodyY - 13f, 15f, bodyY - 2f,
            21f, bodyY - 18f, 29f, bodyY - 2f, 40f, bodyY - 12f, 44f, bodyY + 12f,
        )
    }
    kantoOval(skin, -17f, bodyY, if (grown) 66f else 58f, if (grown) 46f else 32f)
    for (x in listOf(-25f, -5f, 24f, 41f)) {
        kantoOval(skin, x - 8f, if (grown) 29f else 32f, 18f, if (grown) 20f else 15f)
        earlyClaws(x, 43f, 4f)
    }
    for ((x, y) in listOf(15f to 12f, 33f to 21f, 5f to 29f)) {
        kantoOval(dark, x - 4f, y - 4f, if (grown) 11f else 8f, 8f)
    }
    if (male) {
        kantoPolygon(
            skin, -32f, -5f, -49f, -47f, -36f, -41f, -28f, -60f,
            -17f, -48f, -22f, -16f,
        )
        kantoPolygon(skin, -6f, -10f, 4f, -52f, 16f, -39f, 11f, -28f, 15f, -22f, 3f, -5f)
        kantoPolygon(ear, -34f, -19f, -39f, -40f, -29f, -46f, -28f, -21f)
        kantoPolygon(ear, 0f, -14f, 6f, -40f, 8f, -24f)
    } else {
        earlyShape(skin) {
            moveTo(-34f, -7f)
            cubicTo(-65f, -27f, -58f, -58f, -40f, -52f)
            quadraticTo(-22f, -43f, -20f, -12f); close()
        }
        earlyShape(ear) {
            moveTo(-36f, -18f)
            quadraticTo(-55f, -37f, -44f, -44f)
            quadraticTo(-33f, -41f, -29f, -18f); close()
        }
        earlyShape(skin) {
            moveTo(-10f, -10f)
            quadraticTo(-7f, -49f, 12f, -47f)
            quadraticTo(20f, -28f, 3f, -5f); close()
        }
        kantoPolygon(ear, -2f, -16f, 8f, -39f, 8f, -23f)
    }
    kantoOval(skin, -45f, -23f, if (grown) 56f else 51f, if (grown) 44f else 39f)
    if (male) {
        kantoPolygon(skin, -39f, -19f, if (grown) -37f else -32f, if (grown) -62f else -41f, -21f, -21f)
    } else {
        kantoPolygon(skin, -32f, -20f, -27f, if (grown) -38f else -31f, -21f, -21f)
    }
    earlyEye(-31f, -4f, 5.5f, 0xFFB86375)
    earlyEye(-4f, -5f, 5f, 0xFFB86375)
    kantoOval(skin, -43f, 6f, 43f, 22f)
    kantoSmile(-21f, 16f, 12f)
    kantoPolygon(EARLY_WHITE, -33f, 17f, -26f, 18f, -28f, 24f)
    drawCircle(Color(dark), 3f, Offset(-37f, 4f))
    drawCircle(Color(dark), 3f, Offset(0f, 6f))
}

private fun DrawScope.earlyMoonFairy(evolved: Boolean, motion: Float) {
    val pink = if (evolved) 0xFFE7AAC0 else 0xFFF0B6C9
    if (!evolved) {
        earlyShape(pink) {
            moveTo(22f, 27f)
            cubicTo(56f, 42f, 77f, 10f, 57f, -3f)
            cubicTo(38f, -17f, 28f, 7f, 45f, 13f)
            lineTo(27f, 20f); close()
        }
        earlyCurve(0xFFA96989, 2f) {
            moveTo(37f, 27f); cubicTo(64f, 29f, 66f, 3f, 49f, 3f)
            quadraticTo(40f, 4f, 47f, 12f)
        }
    } else {
        earlyShape(pink) {
            moveTo(22f, 30f)
            quadraticTo(57f, 48f, 58f, 17f)
            quadraticTo(51f, 5f, 43f, 14f)
            quadraticTo(57f, 34f, 25f, 22f); close()
        }
    }
    for (side in listOf(-1f, 1f)) {
        val reach = if (evolved) 62f else 46f
        kantoPolygon(
            0xFFD5A4C4, side * 23f, 24f, side * reach, -21f + motion * 3f,
            side * (reach - 4f), -2f, side * (reach - 16f), 3f,
            side * (reach - 11f), 15f, side * 30f, 24f,
        )
        if (evolved) {
            kantoPolygon(pink, side * 10f, -23f, side * 40f, -63f, side * 34f, -27f, side * 22f, -12f)
            kantoPolygon(0xFF745364, side * 28f, -45f, side * 40f, -63f, side * 36f, -42f)
        } else {
            kantoPolygon(pink, side * 14f, -21f, side * 33f, -53f, side * 36f, -18f)
            kantoPolygon(0xFF745364, side * 26f, -40f, side * 33f, -53f, side * 35f, -36f)
        }
        kantoOval(pink, side * 20f - 11f, 36f, 25f, 13f)
    }
    kantoOval(pink, if (evolved) -30f else -35f, if (evolved) -27f else -24f, if (evolved) 61f else 70f, if (evolved) 70f else 66f)
    for (side in listOf(-1f, 1f)) {
        kantoOval(pink, side * 32f - 8f, 5f + side * motion * 3f, 17f, 22f)
    }
    earlyShape(pink) {
        moveTo(-17f, -20f)
        cubicTo(-26f, -40f, 9f, -49f, 15f, -28f)
        quadraticTo(18f, -10f, -3f, -13f)
        quadraticTo(-13f, -18f, -5f, -27f)
        quadraticTo(3f, -33f, 6f, -23f)
        close()
    }
    kantoEyes(0f, -3f, 15f, 3.6f)
    kantoSmile(0f, 11f, 9f)
    drawCircle(Color(0xFFE896B2), 5f, Offset(-24f, 8f))
    drawCircle(Color(0xFFE896B2), 5f, Offset(24f, 8f))
}

private fun DrawScope.earlyFox(manyTails: Boolean, motion: Float) {
    val fur = if (manyTails) 0xFFEBDCAB else 0xFFC78861
    if (manyTails) {
        val tips = listOf(
            -5f to -58f, 15f to -64f, 36f to -60f,
            55f to -52f, 72f to -34f, 83f to -11f,
            80f to 10f, 65f to 31f, 43f to 45f,
        )
        for ((index, tip) in tips.withIndex().reversed()) {
            val tx = tip.first
            val ty = tip.second + motion * 2f
            val dx = tx - 22f
            val dy = ty - 26f
            val length = kotlin.math.sqrt(dx * dx + dy * dy)
            val nx = -dy / length * 9f
            val ny = dx / length * 9f
            earlyShape(if (index % 2 == 0) 0xFFF0E3B5 else 0xFFE1CF98) {
                moveTo(22f, 26f)
                cubicTo(22f + dx * 0.3f + nx, 26f + dy * 0.3f + ny, tx + nx, ty + ny, tx, ty)
                cubicTo(tx - nx, ty - ny, 22f + dx * 0.6f - nx, 26f + dy * 0.6f - ny, 22f, 26f)
                close()
            }
            earlyCurve(0xFFBFAD76, 1.5f) {
                moveTo(28f, 24f)
                quadraticTo(22f + dx * 0.6f, 26f + dy * 0.6f, tx, ty + 6f)
            }
        }
    } else {
        val curls = listOf(23f to -31f, 43f to -39f, 60f to -29f, 67f to -7f, 63f to 13f, 42f to 25f)
        for ((tx, baseY) in curls.asReversed()) {
            val ty = baseY + motion * 2f
            earlyShape(0xFFCE8059) {
                moveTo(15f, 25f)
                cubicTo(tx + 18f, ty + 22f, tx + 19f, ty - 14f, tx + 5f, ty - 15f)
                cubicTo(tx - 12f, ty - 16f, tx - 15f, ty + 3f, tx - 6f, ty + 8f)
                quadraticTo(tx - 9f, ty + 15f, 15f, 34f)
                close()
            }
            earlyCurve(0xFF945B4D, 2f) {
                moveTo(tx + 5f, ty + 8f)
                cubicTo(tx + 16f, ty - 5f, tx - 3f, ty - 13f, tx - 5f, ty - 1f)
                quadraticTo(tx - 4f, ty + 5f, tx + 3f, ty + 1f)
            }
        }
    }
    kantoOval(fur, -27f, 4f, if (manyTails) 70f else 64f, 31f)
    for (x in listOf(-28f, -11f, 18f, 32f)) {
        kantoPolygon(fur, x - 6f, 22f, x + 6f, 22f, x + 3f, 46f, x - 9f, 47f)
        if (!manyTails) kantoOval(0xFF865C51, x - 10f, 40f, 17f, 9f)
    }
    if (manyTails) {
        kantoPolygon(
            EARLY_CREAM, -40f, -16f, -14f, -12f, -4f, 5f, -12f, 2f,
            -5f, 20f, -21f, 13f, -27f, 24f, -33f, 9f, -46f, 11f,
        )
        kantoPolygon(fur, -51f, -27f, -54f, -61f, -32f, -38f)
        kantoPolygon(fur, -24f, -35f, -10f, -61f, -9f, -23f)
        kantoPolygon(0xFFB99876, -48f, -36f, -50f, -52f, -38f, -37f)
        kantoPolygon(0xFFB99876, -21f, -36f, -13f, -51f, -13f, -31f)
        kantoOval(fur, -54f, -39f, 46f, 36f)
        kantoPolygon(fur, -52f, -17f, -66f, -8f, -49f, -2f, -30f, -9f)
        earlyEye(-41f, -22f, 5f, 0xFFBD5F62)
        earlyEye(-19f, -22f, 4.5f, 0xFFBD5F62)
        kantoPolygon(KANTO_INK, -65f, -10f, -58f, -11f, -62f, -5f)
        kantoLine(0xFF94785D, 2f, -53f, -3f, -42f, -5f)
        kantoPolygon(fur, -44f, -34f, -38f, -50f, -31f, -37f, -23f, -45f, -18f, -32f)
    } else {
        kantoOval(EARLY_CREAM, -29f, 3f, 23f, 27f)
        kantoPolygon(fur, -48f, -16f, -54f, -47f, -30f, -30f)
        kantoPolygon(fur, -22f, -29f, -3f, -46f, -3f, -12f)
        kantoPolygon(0xFF785654, -44f, -24f, -49f, -38f, -34f, -27f)
        kantoPolygon(0xFF785654, -18f, -25f, -7f, -37f, -7f, -19f)
        kantoOval(fur, -52f, -29f, 51f, 41f)
        kantoOval(0xFFECC5A1, -45f, -2f, 35f, 18f)
        earlyEye(-39f, -13f, 6f, 0xFF79628E)
        earlyEye(-14f, -13f, 6f, 0xFF79628E)
        drawCircle(Color(KANTO_INK), 3f, Offset(-27f, 2f))
        kantoSmile(-27f, 9f, 8f)
        for (x in listOf(-39f, -26f, -13f)) {
            kantoOval(0xFFCE8059, x - 10f, -38f, 21f, 18f)
            earlyCurve(0xFF945B4D, 1.7f) {
                moveTo(x + 4f, -25f)
                quadraticTo(x - 8f, -25f, x - 3f, -32f)
            }
        }
    }
}

private fun DrawScope.earlyWigglytuff(motion: Float) {
    val pink = 0xFFEBAEC9
    for (side in listOf(-1f, 1f)) {
        earlyShape(pink) {
            moveTo(side * 8f, -21f)
            cubicTo(side * 6f, -69f, side * 31f, -70f, side * 32f, -50f)
            quadraticTo(side * 31f, -29f, side * 21f, -13f)
            close()
        }
        earlyShape(0xFF765A72) {
            moveTo(side * 15f, -29f)
            quadraticTo(side * 14f, -59f, side * 23f, -59f)
            quadraticTo(side * 32f, -54f, side * 20f, -29f)
            close()
        }
        kantoOval(pink, side * 22f - 12f, 37f, 27f, 12f)
        kantoOval(pink, side * 32f - 9f, 1f + motion * side * 4f, 18f, 26f)
    }
    kantoOval(pink, -33f, -29f, 66f, 73f)
    kantoOval(EARLY_WHITE, -25f, 0f, 50f, 40f)
    earlyEye(-15f, -9f, 9f, 0xFF489EAC)
    earlyEye(15f, -9f, 9f, 0xFF489EAC)
    earlyShape(pink) {
        moveTo(-12f, -24f)
        cubicTo(-26f, -42f, 11f, -47f, 14f, -31f)
        quadraticTo(19f, -16f, 2f, -16f)
        quadraticTo(-11f, -15f, -8f, -27f)
        quadraticTo(-3f, -35f, 5f, -28f)
        close()
    }
    kantoSmile(0f, 13f, 9f)
}

private fun DrawScope.earlyBat(largeMouth: Boolean, motion: Float) {
    val blue = 0xFF739BCE
    for (side in listOf(-1f, 1f)) {
        val reach = if (largeMouth) 92f else 85f
        val lift = motion * 5f
        earlyShape(0xFFAB8AC7) {
            moveTo(side * 12f, -15f)
            quadraticTo(side * 47f, -38f, side * reach, -55f + lift)
            lineTo(side * (reach - 9f), 5f + lift)
            quadraticTo(side * 64f, -15f, side * 53f, 15f)
            quadraticTo(side * 35f, -3f, side * 27f, 30f)
            lineTo(side * 12f, 15f)
            close()
        }
        earlyCurve(blue, 5f) {
            moveTo(side * 14f, -13f)
            quadraticTo(side * 50f, -40f, side * reach, -55f + lift)
        }
        kantoLine(0xFF6B679B, 2f, side * 30f, -22f, side * 52f, 10f)
        kantoLine(0xFF6B679B, 2f, side * 49f, -34f, side * (reach - 10f), 1f + lift)
        kantoLine(blue, 4f, side * 12f, 27f, side * 18f, 47f, side * 25f, 44f)
    }
    if (largeMouth) {
        kantoPolygon(blue, -24f, -25f, -27f, -47f, -8f, -33f)
        kantoPolygon(blue, 8f, -33f, 27f, -47f, 24f, -25f)
        kantoOval(blue, -31f, -39f, 62f, 82f)
        kantoOval(KANTO_INK, -24f, -25f, 48f, 63f)
        kantoOval(0xFFC76C92, -15f, 18f, 31f, 17f)
        kantoPolygon(EARLY_WHITE, -23f, -23f, -10f, -23f, -12f, 1f)
        kantoPolygon(EARLY_WHITE, 10f, -23f, 23f, -23f, 12f, 1f)
        kantoPolygon(EARLY_WHITE, -21f, 29f, -11f, 32f, -13f, 12f)
        kantoPolygon(EARLY_WHITE, 11f, 32f, 21f, 29f, 13f, 12f)
        kantoPolygon(EARLY_WHITE, -25f, -33f, -7f, -29f, -13f, -23f, -25f, -25f)
        kantoPolygon(EARLY_WHITE, 25f, -33f, 7f, -29f, 13f, -23f, 25f, -25f)
        drawCircle(Color(KANTO_INK), 2.8f, Offset(-17f, -27f))
        drawCircle(Color(KANTO_INK), 2.8f, Offset(17f, -27f))
    } else {
        kantoPolygon(blue, -14f, -17f, -27f, -62f, -7f, -51f, -2f, -21f)
        kantoPolygon(blue, 2f, -21f, 7f, -51f, 27f, -62f, 14f, -17f)
        kantoPolygon(0xFFB28FCC, -15f, -30f, -22f, -52f, -11f, -46f)
        kantoPolygon(0xFFB28FCC, 15f, -30f, 22f, -52f, 11f, -46f)
        kantoOval(blue, -17f, -28f, 34f, 60f)
        kantoOval(KANTO_INK, -13f, -7f, 26f, 24f)
        kantoPolygon(EARLY_WHITE, -11f, -5f, -3f, -5f, -6f, 8f)
        kantoPolygon(EARLY_WHITE, 3f, -5f, 11f, -5f, 6f, 8f)
        kantoPolygon(EARLY_WHITE, -9f, 13f, -3f, 15f, -5f, 7f)
        kantoPolygon(EARLY_WHITE, 3f, 15f, 9f, 13f, 5f, 7f)
    }
}

private fun DrawScope.earlyFlowerWalker(stage: Int, motion: Float) {
    val blue = 0xFF789FBF
    for (side in listOf(-1f, 1f)) {
        kantoOval(blue, side * 20f - 13f, 34f, 27f, 15f)
        if (stage > 0) {
            kantoOval(blue, side * 28f - 9f, 8f + side * motion * 3f, 19f, 21f)
        }
    }
    kantoOval(blue, -27f, if (stage == 0) -9f else -2f, 54f, if (stage == 0) 46f else 44f)
    if (stage == 0) {
        val tips = listOf(-43f to -45f, -23f to -61f, 0f to -65f, 23f to -59f, 43f to -42f)
        for ((tx, ty) in tips) {
            earlyShape(0xFF70AE68) {
                moveTo(-3f, -5f)
                quadraticTo(tx - 15f, ty + 5f, tx + motion * 2f, ty)
                quadraticTo(tx + 15f, ty + 5f, 5f, -5f)
                close()
            }
            kantoLine(0xFF4E8050, 2f, 0f, -7f, tx + motion, ty + 10f)
        }
        kantoEyes(0f, 13f, 12f, 3.4f)
        kantoSmile(0f, 24f, 7f)
    } else if (stage == 1) {
        kantoPolygon(0xFFAB664E, -27f, -8f, -35f, -24f, -18f, -23f, -8f, -44f, 3f, -27f, 20f, -43f, 25f, -20f, 40f, -20f, 25f, 0f)
        for ((x, y) in listOf(-28f to -22f, -10f to -35f, 12f to -35f, 29f to -20f)) {
            kantoOval(0xFFD68B66, x - 15f, y - 9f, 31f, 30f)
            kantoOval(0xFF9C6157, x - 8f, y - 4f, 15f, 12f)
            drawCircle(Color(0xFFE4AB7E), 2.5f, Offset(x + 8f, y + 8f))
        }
        kantoLine(KANTO_INK, 2.6f, -20f, 12f, -15f, 15f, -8f, 12f)
        kantoLine(KANTO_INK, 2.6f, 8f, 12f, 15f, 15f, 21f, 12f)
        kantoOval(0xFFC8798B, -8f, 20f, 20f, 10f)
        earlyShape(EARLY_WHITE) {
            moveTo(3f, 25f)
            quadraticTo(11f, 21f, 14f, 27f)
            lineTo(13f, 36f + motion * 2f)
            quadraticTo(8f, 43f, 5f, 35f)
            close()
        }
    } else {
        val petals = listOf(-39f to -38f, 0f to -48f, 39f to -38f, -24f to -14f, 24f to -14f)
        for ((x, y) in petals) {
            kantoOval(0xFFE27A82, x - 26f, y - 17f, 52f, 35f)
            drawCircle(Color(EARLY_CREAM), 5f, Offset(x - 10f, y))
            drawCircle(Color(EARLY_CREAM), 3.5f, Offset(x + 10f, y - 5f))
            drawCircle(Color(EARLY_CREAM), 3f, Offset(x + 5f, y + 9f))
        }
        kantoOval(0xFFB65D72, -18f, -40f, 36f, 25f)
        kantoOval(0xFFE5B17B, -10f, -34f, 20f, 14f)
        kantoOval(0xFF9B596B, -5f, -30f, 10f, 7f)
        kantoEyes(0f, 17f, 13f, 3.7f)
        kantoSmile(0f, 29f, 9f)
    }
}

private fun DrawScope.earlyMushroom(x: Float, y: Float, radius: Float) {
    kantoPolygon(EARLY_CREAM, x - 4f, y, x + 4f, y, x + 5f, y + 22f, x - 5f, y + 22f)
    kantoOval(EARLY_CREAM, x - radius, y + 2f, radius * 2f, 10f)
    earlyShape(0xFFDC7871) {
        moveTo(x - radius, y + 6f)
        cubicTo(x - radius, y - radius, x + radius, y - radius, x + radius, y + 6f)
        quadraticTo(x, y + 1f, x - radius, y + 6f)
        close()
    }
    drawCircle(Color(EARLY_CREAM), radius * 0.2f, Offset(x - radius * 0.4f, y - radius * 0.22f))
    drawCircle(Color(EARLY_CREAM), radius * 0.15f, Offset(x + radius * 0.4f, y - radius * 0.12f))
}

private fun DrawScope.earlyMushroomCrab(large: Boolean, motion: Float) {
    val orange = 0xFFD89567
    for (side in listOf(-1f, 1f)) {
        for (i in 0..2) {
            val x = side * (31f + i * 10f)
            val y = 23f + i * 5f
            kantoLine(KANTO_INK, 7f, side * 15f, 21f, x, y - 5f, x + side * 4f, y + 12f)
            kantoLine(orange, 4f, side * 15f, 21f, x, y - 5f, x + side * 4f, y + 12f)
        }
        val reach = if (large) 69f else 58f
        val dy = side * motion * 3f
        earlyShape(orange) {
            moveTo(side * 23f, 22f)
            quadraticTo(side * reach, 33f + dy, side * reach, -1f + dy)
            lineTo(side * (reach - 13f), 9f + dy)
            lineTo(side * (reach - 15f), -8f + dy)
            quadraticTo(side * 34f, -2f, side * 32f, 12f)
            lineTo(side * 21f, 10f)
            close()
        }
    }
    if (large) {
        kantoPolygon(EARLY_CREAM, -17f, -17f, 17f, -17f, 24f, 20f, -22f, 20f)
        kantoOval(EARLY_CREAM, -64f, -18f, 128f, 26f)
        for (x in listOf(-48f, -30f, -12f, 12f, 30f, 48f)) {
            kantoLine(0xFFB2A27F, 1.7f, x, -9f, x * 0.7f, 2f)
        }
        earlyShape(0xFFD87368) {
            moveTo(-66f, -7f)
            cubicTo(-64f, -76f, 64f, -76f, 66f, -7f)
            quadraticTo(0f, -18f, -66f, -7f)
            close()
        }
        for ((x, y, r) in listOf(Triple(-34f, -34f, 10f), Triple(0f, -47f, 12f), Triple(34f, -34f, 11f), Triple(7f, -20f, 7f))) {
            kantoOval(EARLY_CREAM, x - r, y - r * 0.65f, r * 2f, r * 1.3f)
        }
        kantoOval(orange, -32f, 4f, 64f, 34f)
        kantoOval(EARLY_WHITE, -27f, 9f, 19f, 17f)
        kantoOval(EARLY_WHITE, 8f, 9f, 19f, 17f)
        kantoSmile(0f, 30f, 11f)
    } else {
        kantoOval(orange, -30f, -3f, 60f, 34f)
        earlyMushroom(-24f, -29f + motion * 2f, 20f)
        earlyMushroom(23f, -34f - motion * 2f, 21f)
        kantoOval(orange, -24f, 3f, 48f, 28f)
        kantoLine(orange, 8f, -14f, 8f, -17f, -3f)
        kantoLine(orange, 8f, 14f, 8f, 17f, -3f)
        earlyEye(-17f, -1f, 8f)
        earlyEye(17f, -1f, 8f)
        kantoSmile(0f, 20f, 11f)
        kantoPolygon(EARLY_WHITE, -9f, 21f, -3f, 22f, -6f, 26f)
        kantoPolygon(EARLY_WHITE, 3f, 22f, 9f, 21f, 6f, 26f)
    }
}

private fun DrawScope.earlyVenonat(motion: Float) {
    val violet = 0xFF9B84B7
    for (side in listOf(-1f, 1f)) {
        kantoLine(KANTO_INK, 7f, side * 9f, -30f, side * 13f, -59f)
        kantoLine(EARLY_WHITE, 4f, side * 9f, -30f, side * 13f, -59f)
        kantoOval(EARLY_WHITE, side * 13f - 4f, -63f, 8f, 11f)
        kantoOval(0xFFD899A8, side * 19f - 9f, 33f, 21f, 15f)
        kantoOval(violet, side * 36f - 9f, 7f + side * motion * 3f, 18f, 19f)
    }
    earlyShape(violet) {
        repeat(48) { index ->
            val angle = (index * PI / 24.0).toFloat()
            val fringe = if (index % 2 == 0) 1f else 0.91f
            val x = cos(angle) * 36f * fringe
            val y = sin(angle) * 38f * fringe - 2f
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    for (side in listOf(-1f, 1f)) {
        val x = side * 17f
        kantoOval(0xFFD583AB, x - 13f, -24f, 26f, 31f)
        for (y in listOf(-17f, -9f, -1f)) {
            kantoLine(0xFFAF608A, 1.4f, x - 9f, y, x + 9f, y)
        }
        for (dx in listOf(-5f, 3f)) {
            kantoLine(0xFFAF608A, 1.4f, x + dx, -21f, x + dx, 4f)
        }
        drawCircle(Color(0xFFF4C6DC), 3f, Offset(x - 4f, -16f))
    }
    kantoOval(0xFFE8B2B8, -8f, 7f, 16f, 11f)
    kantoSmile(0f, 21f, 10f)
    kantoPolygon(EARLY_WHITE, -10f, 21f, -3f, 23f, -5f, 29f)
    kantoPolygon(EARLY_WHITE, 3f, 23f, 10f, 21f, 5f, 29f)
}

private fun DrawScope.earlyDiglett(motion: Float) {
    kantoOval(0xFFC3AF8C, -47f, 29f, 94f, 21f)
    for ((x, y) in listOf(-37f to 33f, -27f to 29f, 27f to 30f, 38f to 35f)) {
        kantoPolygon(0xFFA7977B, x - 8f, y + 9f, x - 6f, y, x + 4f, y - 3f, x + 9f, y + 8f)
    }
    val lift = motion * 2f
    earlyShape(0xFFB58A70) {
        moveTo(-25f, 40f)
        lineTo(-25f, -13f + lift)
        cubicTo(-25f, -50f + lift, 25f, -50f + lift, 25f, -13f + lift)
        lineTo(25f, 40f)
        quadraticTo(0f, 48f, -25f, 40f)
        close()
    }
    earlyCurve(0xFFD1AC8C, 5f) {
        moveTo(-16f, -22f + lift)
        quadraticTo(-12f, -32f + lift, -4f, -33f + lift)
    }
    kantoOval(KANTO_INK, -12f, -10f + lift, 5f, 11f)
    kantoOval(KANTO_INK, 7f, -10f + lift, 5f, 11f)
    kantoOval(0xFFE4A0A6, -15f, 6f + lift, 30f, 17f)
    drawCircle(Color(0xFFF5C6C6), 3f, Offset(-6f, 11f + lift))
    for ((x, y) in listOf(-30f to 43f, -11f to 46f, 15f to 45f, 34f to 42f)) {
        kantoPolygon(0xFFC9B695, x - 8f, y + 3f, x - 3f, y - 6f, x + 5f, y - 4f, x + 9f, y + 3f)
    }
}
