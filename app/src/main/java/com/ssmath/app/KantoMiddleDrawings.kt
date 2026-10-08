package com.ssmath.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawKantoMiddle(ndex: Int, motion: Float) {
    val movement = motion.coerceIn(-1f, 1f)
    when (ndex) {
        51 -> drawDugtrio(movement)
        52, 53 -> drawCoinCats(ndex == 53, movement)
        54, 55 -> drawDucks(ndex == 55, movement)
        56, 57 -> drawPigMonkeys(ndex == 57, movement)
        58, 59 -> drawFlameDogs(ndex == 59, movement)
        60, 61, 62 -> drawTadpoles(ndex, movement)
        63, 64, 65 -> drawPsychicFoxes(ndex, movement)
        66, 67, 68 -> drawMuscleLizards(ndex, movement)
        69, 70, 71 -> drawPitcherPlants(ndex, movement)
        72, 73 -> drawJellyfish(ndex == 73, movement)
        76 -> drawGolem(movement)
        77, 78 -> drawFireHorses(ndex == 78, movement)
        79, 80 -> drawSlowCreatures(ndex == 80, movement)
        81, 82 -> drawMagneticCreatures(ndex == 82, movement)
        83 -> drawLeekDuck(movement)
        84, 85 -> drawRunningBirds(ndex == 85, movement)
        86, 87 -> drawSeaLions(ndex == 87, movement)
        88, 89 -> drawSludge(ndex == 89, movement)
        90, 91 -> drawShells(ndex == 91, movement)
        92, 93, 94 -> drawGhosts(ndex, movement)
        95 -> drawOnix(movement)
        96, 97 -> drawDreamEaters(ndex == 97, movement)
        98, 99 -> drawCrabs(ndex == 99, movement)
        100 -> drawVoltorb()
        else -> error("Unsupported middle Kanto drawing: $ndex")
    }
}

private fun DrawScope.middleOval(color: Long, x: Float, y: Float, width: Float, height: Float) {
    kantoOval(color, x - width / 2f, y - height / 2f, width, height)
}

private fun DrawScope.middleCurve(
    color: Long,
    width: Float,
    startX: Float,
    startY: Float,
    controlX: Float,
    controlY: Float,
    endX: Float,
    endY: Float,
) {
    val curve = Path().apply {
        moveTo(startX, startY)
        quadraticTo(controlX, controlY, endX, endY)
    }
    drawPath(curve, Color(KANTO_INK), style = Stroke(width + 3f, cap = StrokeCap.Round))
    drawPath(curve, Color(color), style = Stroke(width, cap = StrokeCap.Round))
}

private fun DrawScope.middleSpiral(x: Float, y: Float, radius: Float) {
    val spiral = Path()
    for (step in 0..90) {
        val t = step / 90f
        val angle = t * PI.toFloat() * 5f
        val px = x + cos(angle) * radius * t
        val py = y + sin(angle) * radius * t
        if (step == 0) spiral.moveTo(px, py) else spiral.lineTo(px, py)
    }
    drawPath(spiral, Color(KANTO_INK), style = Stroke(3f))
}

private fun DrawScope.drawDugtrio(motion: Float) {
    middleOval(0xFFAD8A62, 0f, 35f, 154f, 31f)
    for ((x, y) in listOf(-44f to 0f, 40f to -8f, 0f to -24f)) {
        val bob = if (x == 0f) motion * 3f else -motion * 2f
        middleOval(0xFFAA7958, x, y + bob, 49f, 85f)
        middleOval(0xFFC79671, x - 8f, y - 19f + bob, 13f, 27f)
        kantoEyes(x, y - 19f + bob, 10f, 3.3f)
        middleOval(0xFFEA999A, x, y - 6f + bob, 24f, 14f)
    }
    kantoPolygon(
        0xFFAD8A62, -77f, 37f, -61f, 24f, -45f, 38f, -23f, 27f,
        -9f, 40f, 14f, 29f, 34f, 39f, 55f, 25f, 76f, 38f, 66f, 49f, -61f, 49f,
    )
    kantoLine(0xFF765D49, 3f, -56f, 41f, -42f, 43f)
    kantoLine(0xFF765D49, 3f, 30f, 43f, 46f, 41f)
}

private fun DrawScope.drawCoinCats(persian: Boolean, motion: Float) {
    val fur = if (persian) 0xFFE9D4A7 else 0xFFF2DCAD
    val bodyX = if (persian) 9f else 0f
    middleCurve(fur, 11f, 41f, 15f, 90f + motion * 4f, -48f, 76f, 5f)
    middleCurve(0xFFB58B58, 10f, 76f, 5f, 60f, 21f, 67f, 29f)
    if (persian) {
        for (x in listOf(-20f, 10f, 35f)) {
            kantoLine(fur, 13f, x, 15f, x - 5f, 43f)
            middleOval(fur, x - 9f, 44f, 23f, 13f)
        }
        middleOval(fur, bodyX, 6f, 87f, 45f)
        middleOval(fur, -33f, -3f, 39f, 60f)
    } else {
        middleOval(fur, -18f, 43f, 27f, 16f)
        middleOval(fur, 17f, 43f, 27f, 16f)
        middleOval(fur, 0f, 16f, 45f, 53f)
        kantoLine(fur, 12f, -18f, 6f, -37f, 19f, -48f, 8f + motion * 4f)
        kantoLine(fur, 12f, 18f, 6f, 35f, 21f, 45f, 10f - motion * 4f)
    }
    val hx = if (persian) -38f else 0f
    val hy = if (persian) -31f else -27f
    kantoPolygon(KANTO_INK, hx - 26f, hy - 8f, hx - 29f, hy - 34f, hx - 8f, hy - 18f)
    kantoPolygon(KANTO_INK, hx + 26f, hy - 8f, hx + 29f, hy - 34f, hx + 8f, hy - 18f)
    kantoPolygon(0xFFE5AAA9, hx - 23f, hy - 13f, hx - 24f, hy - 27f, hx - 14f, hy - 18f)
    kantoPolygon(0xFFE5AAA9, hx + 23f, hy - 13f, hx + 24f, hy - 27f, hx + 14f, hy - 18f)
    middleOval(fur, hx, hy, 64f, 49f)
    middleOval(0xFFFFFFFF, hx - 14f, hy, 19f, 22f)
    middleOval(0xFFFFFFFF, hx + 14f, hy, 19f, 22f)
    kantoEyes(hx, hy, 14f, 3f)
    if (persian) {
        middleOval(0xFFE77782, hx, hy - 16f, 12f, 14f)
        kantoLine(KANTO_INK, 2f, hx - 24f, hy - 8f, hx - 6f, hy - 4f)
        kantoLine(KANTO_INK, 2f, hx + 24f, hy - 8f, hx + 6f, hy - 4f)
    } else {
        middleOval(0xFFF2C84F, hx, hy - 23f, 15f, 28f)
        kantoLine(0xFFA77835, 2f, hx, hy - 33f, hx, hy - 13f)
    }
    kantoPolygon(0xFF9A7164, hx - 4f, hy + 8f, hx + 4f, hy + 8f, hx, hy + 12f)
    kantoSmile(hx, hy + 13f, 11f)
    for (side in listOf(-1f, 1f)) {
        kantoLine(KANTO_INK, 2f, hx + side * 24f, hy + 6f, hx + side * 42f, hy)
        kantoLine(KANTO_INK, 2f, hx + side * 24f, hy + 12f, hx + side * 42f, hy + 15f)
    }
}

private fun DrawScope.drawDucks(golduck: Boolean, motion: Float) {
    val skin = if (golduck) 0xFF559ECE else 0xFFF0C94F
    val bill = 0xFFE4C795
    kantoPolygon(skin, 16f, 27f, 64f, 14f + motion * 4f, 42f, 36f, 18f, 39f)
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(
            bill, side * 13f, 34f, side * 17f, 49f, side * 41f, 48f,
            side * 34f, 42f, side * 28f, 35f,
        )
    }
    middleOval(skin, 0f, 9f, if (golduck) 57f else 68f, 68f)
    if (golduck) {
        middleOval(0xFF94C3DB, 0f, 14f, 35f, 43f)
        for (side in listOf(-1f, 1f)) {
            kantoLine(skin, 12f, side * 24f, -2f, side * 45f, 7f, side * 56f, -11f + motion * 3f)
            kantoPolygon(
                skin, side * 51f, -6f, side * 55f, -22f, side * 61f, -15f,
                side * 68f, -20f, side * 68f, -6f, side * 57f, 0f,
            )
        }
        kantoPolygon(
            skin, -25f, -30f, -27f, -56f, -13f, -45f, 0f, -66f,
            10f, -46f, 27f, -57f, 25f, -28f,
        )
    } else {
        kantoLine(KANTO_INK, 3f, -5f, -46f, -14f, -62f)
        kantoLine(KANTO_INK, 3f, 0f, -47f, 0f, -65f)
        kantoLine(KANTO_INK, 3f, 5f, -46f, 14f, -60f)
    }
    middleOval(skin, 0f, -29f, 64f, 51f)
    middleOval(0xFFFFFFFF, -15f, -31f, 21f, 21f)
    middleOval(0xFFFFFFFF, 15f, -31f, 21f, 21f)
    kantoEyes(0f, -31f, 15f, 2.6f)
    middleOval(bill, 0f, -13f, 49f, 24f)
    kantoEyes(0f, -18f, 8f, 1.6f)
    if (golduck) {
        middleOval(0xFFEE6477, 0f, -42f, 12f, 13f)
        kantoLine(KANTO_INK, 3f, -25f, -39f, -7f, -32f)
        kantoLine(KANTO_INK, 3f, 25f, -39f, 7f, -32f)
    } else {
        kantoLine(skin, 14f, -25f, 5f, -42f, -11f, -29f, -29f + motion * 2f)
        kantoLine(skin, 14f, 25f, 5f, 42f, -11f, 29f, -29f - motion * 2f)
        middleOval(skin, -30f, -27f + motion * 2f, 19f, 25f)
        middleOval(skin, 30f, -27f - motion * 2f, 19f, 25f)
    }
}

private fun DrawScope.drawPigMonkeys(primeape: Boolean, motion: Float) {
    val fur = if (primeape) 0xFFEAD7B5 else 0xFFEBCF9C
    val limb = if (primeape) 0xFFAA7A54 else fur
    if (!primeape) {
        middleCurve(fur, 8f, 25f, 14f, 77f, -19f, 71f, 17f + motion * 4f)
        middleCurve(fur, 8f, 71f, 17f + motion * 4f, 59f, 35f, 48f, 20f)
    }
    for (side in listOf(-1f, 1f)) {
        kantoLine(limb, 13f, side * 18f, 24f, side * 30f, 40f)
        middleOval(limb, side * 35f, 43f, 27f, 17f)
        kantoLine(limb, 13f, side * 28f, -8f, side * 50f, -9f, side * 61f, -28f + side * motion * 5f)
        middleOval(limb, side * 61f, -30f + side * motion * 5f, 24f, 27f)
        if (primeape) {
            kantoLine(KANTO_INK, 6f, side * 53f, -17f, side * 66f, -17f)
            kantoLine(KANTO_INK, 5f, side * 24f, 32f, side * 35f, 30f)
        }
    }
    kantoPolygon(
        fur, -36f, -19f, -42f, -33f, -25f, -32f, -31f, -48f,
        -12f, -43f, 0f, -56f, 11f, -44f, 31f, -49f, 28f, -32f,
        43f, -29f, 35f, -15f, 45f, -3f, 36f, 9f, 40f, 25f,
        23f, 26f, 12f, 39f, 0f, 31f, -17f, 38f, -25f, 25f,
        -40f, 24f, -36f, 9f, -45f, -5f,
    )
    kantoPolygon(fur, -31f, -31f, -33f, -57f, -13f, -40f)
    kantoPolygon(fur, 31f, -31f, 33f, -57f, 13f, -40f)
    kantoPolygon(0xFFCF9E98, -29f, -36f, -29f, -48f, -20f, -40f)
    kantoPolygon(0xFFCF9E98, 29f, -36f, 29f, -48f, 20f, -40f)
    middleOval(0xFFFFFFFF, -16f, -20f, 23f, 18f)
    middleOval(0xFFFFFFFF, 16f, -20f, 23f, 18f)
    kantoEyes(0f, -19f, 13f, 3.2f)
    kantoLine(KANTO_INK, 3f, -29f, -30f, -7f, -22f)
    kantoLine(KANTO_INK, 3f, 29f, -30f, 7f, -22f)
    middleOval(0xFFE5A19B, 0f, -3f, 36f, 25f)
    kantoEyes(0f, -3f, 8f, 3f)
    if (primeape) {
        kantoLine(0xFF9C5F55, 3f, 25f, -36f, 21f, -41f, 24f, -46f)
        kantoLine(0xFF9C5F55, 3f, 27f, -42f, 33f, -43f)
    }
}

private fun DrawScope.drawFlameDogs(arcanine: Boolean, motion: Float) {
    val fur = 0xFFF1A15B
    val cream = 0xFFF4DFAD
    kantoPolygon(
        cream, 29f, 7f, 52f, -7f, 47f, -23f, 68f, -18f,
        82f, -40f + motion * 4f, 86f, -11f, 74f, 13f, 49f, 22f,
    )
    for (x in listOf(-32f, -9f, 26f, 45f)) {
        kantoLine(fur, if (arcanine) 15f else 12f, x, 18f, x - 3f, 40f)
        middleOval(cream, x - 6f, 44f, 25f, 15f)
    }
    middleOval(fur, 10f, 7f, if (arcanine) 98f else 84f, 54f)
    for (x in listOf(0f, 21f, 41f)) {
        kantoPolygon(0xFF51494A, x - 8f, -16f, x + 4f, -14f, x - 2f, 8f, x - 10f, 1f)
    }
    kantoPolygon(
        cream, -44f, -33f, -21f, -34f, -10f, -22f, -15f, -9f,
        -3f, 1f, -19f, 4f, -12f, 19f, -30f, 15f, -35f, 30f,
        -45f, 16f, -59f, 20f, -53f, 4f, -65f, -4f, -53f, -13f,
    )
    if (arcanine) {
        kantoPolygon(
            cream, -64f, -37f, -72f, -47f, -60f, -51f, -57f, -65f,
            -44f, -57f, -34f, -68f, -24f, -57f, -8f, -61f,
            -13f, -43f, 0f, -30f, -17f, -19f, -55f, -17f,
        )
    }
    kantoPolygon(fur, -65f, -32f, -66f, -59f, -44f, -44f)
    kantoPolygon(fur, -27f, -35f, -15f, -59f, -12f, -27f)
    kantoPolygon(0xFF66545B, -61f, -38f, -61f, -51f, -51f, -43f)
    kantoPolygon(0xFF66545B, -24f, -38f, -18f, -49f, -17f, -33f)
    middleOval(fur, -39f, -27f, 57f, 51f)
    kantoPolygon(0xFF51494A, -44f, -51f, -31f, -51f, -36f, -32f)
    kantoEyes(-39f, -29f, 13f, 3.7f)
    middleOval(cream, -43f, -11f, 39f, 23f)
    middleOval(KANTO_INK, -47f, -17f, 14f, 9f)
    kantoSmile(-43f, -8f, 10f)
    if (!arcanine) {
        kantoPolygon(cream, -48f, -46f, -50f, -60f, -40f, -55f, -33f, -64f, -24f, -49f)
    }
}

private fun DrawScope.drawTadpoles(ndex: Int, motion: Float) {
    val blue = if (ndex == 62) 0xFF467CA9 else 0xFF649CCF
    if (ndex == 60) {
        middleCurve(0xFF8CBAD8, 10f, 23f, 14f, 46f, 28f, 59f, 5f)
        middleOval(0xFFE4EDF2, 67f, -7f + motion * 4f, 42f, 57f)
        middleCurve(0xFF91BBD3, 3f, 55f, 10f, 69f, 1f, 72f, -23f + motion * 4f)
    }
    for (side in listOf(-1f, 1f)) {
        kantoLine(blue, 11f, side * 18f, 24f, side * 24f, 41f)
        middleOval(blue, side * 29f, 44f, 30f, 14f)
        if (ndex != 60) {
            val handY = if (ndex == 62) -17f else 8f
            kantoLine(blue, if (ndex == 62) 17f else 12f, side * 31f, -7f, side * 50f, 9f, side * 62f, handY + motion * side * 4f)
            middleOval(0xFFF8F6E9, side * 64f, handY + motion * side * 4f, 26f, 27f)
            kantoLine(0xFF99A3B6, 2f, side * 62f, handY - 7f, side * 70f, handY - 4f)
        }
    }
    middleOval(blue, 0f, -4f, if (ndex == 62) 92f else 77f, 83f)
    middleOval(0xFFF5F3E7, 0f, 8f, if (ndex == 62) 68f else 60f, 57f)
    middleSpiral(0f, 8f, 23f)
    if (ndex == 60) {
        middleOval(0xFFFFFFFF, -19f, -31f, 21f, 26f)
        middleOval(0xFFFFFFFF, 19f, -31f, 21f, 26f)
        kantoEyes(0f, -31f, 19f, 4f)
        middleOval(0xFFE29AAD, 0f, -23f, 19f, 12f)
    } else {
        middleOval(blue, -23f, -39f, 27f, 23f)
        middleOval(blue, 23f, -39f, 27f, 23f)
        middleOval(0xFFFFFFFF, -23f, -40f, 18f, 17f)
        middleOval(0xFFFFFFFF, 23f, -40f, 18f, 17f)
        kantoEyes(0f, -39f, 23f, 3.5f)
        if (ndex == 62) {
            kantoLine(KANTO_INK, 4f, -35f, -48f, -13f, -40f)
            kantoLine(KANTO_INK, 4f, 35f, -48f, 13f, -40f)
        }
        kantoLine(KANTO_INK, 2f, -14f, -20f, 14f, -20f)
    }
}

private fun DrawScope.middleSpoon(x: Float, y: Float, motion: Float) {
    kantoLine(0xFFDCE4EA, 4f, x, y + 12f, x + motion, y - 13f)
    middleOval(0xFFDCE4EA, x + motion, y - 22f, 15f, 22f)
    kantoLine(0xFF94A5B5, 2f, x + motion, y - 28f, x + motion - 2f, y - 19f)
}

private fun DrawScope.drawPsychicFoxes(ndex: Int, motion: Float) {
    val yellow = 0xFFE8C75C
    val armor = 0xFF9D744F
    if (ndex != 65) {
        middleCurve(yellow, 14f, 21f, 22f, 64f, 48f, 74f, 6f + motion * 4f)
        kantoLine(armor, 8f, 58f, 32f, 69f, 23f)
    }
    for (side in listOf(-1f, 1f)) {
        kantoLine(yellow, 14f, side * 13f, 22f, side * 28f, 35f, side * 43f, 37f)
        kantoPolygon(yellow, side * 31f, 34f, side * 53f, 38f, side * 53f, 48f, side * 25f, 46f)
    }
    middleOval(yellow, 0f, 8f, 45f, 51f)
    kantoPolygon(armor, -26f, -15f, 26f, -15f, 20f, 10f, 10f, 24f, -10f, 24f, -20f, 10f)
    for (side in listOf(-1f, 1f)) {
        middleOval(armor, side * 25f, -7f, 24f, 24f)
        val elbowY = if (ndex == 63) 19f else 12f
        kantoLine(yellow, 10f, side * 28f, -2f, side * 43f, elbowY, side * 60f, 6f + side * motion * 3f)
        middleOval(yellow, side * 61f, 5f + side * motion * 3f, 20f, 16f)
        if (ndex == 65 || (ndex == 64 && side > 0f)) {
            middleSpoon(side * 66f, -3f + side * motion * 3f, motion * 2f)
        }
    }
    kantoPolygon(
        yellow, -27f, -33f, -31f, -65f, -11f, -48f, 0f, -51f,
        12f, -48f, 32f, -65f, 28f, -31f, 14f, -14f, 0f, -10f, -15f, -17f,
    )
    if (ndex == 63) {
        kantoLine(KANTO_INK, 3f, -22f, -34f, -9f, -31f)
        kantoLine(KANTO_INK, 3f, 22f, -34f, 9f, -31f)
    } else {
        kantoPolygon(0xFFFFFFFF, -25f, -38f, -7f, -31f, -19f, -28f)
        kantoPolygon(0xFFFFFFFF, 25f, -38f, 7f, -31f, 19f, -28f)
        kantoEyes(0f, -32f, 16f, 2.5f)
        val reach = if (ndex == 65) 43f else 34f
        kantoPolygon(0xFFF7DF91, -3f, -23f, -reach, -13f, -reach - 5f, -23f, -31f, -4f, -8f, -13f)
        kantoPolygon(0xFFF7DF91, 3f, -23f, reach, -13f, reach + 5f, -23f, 31f, -4f, 8f, -13f)
    }
    kantoPolygon(yellow, -9f, -27f, 9f, -27f, 0f, -15f)
    if (ndex == 64) {
        kantoPolygon(0xFFC66860, 0f, -47f, 3f, -40f, 10f, -40f, 5f, -35f, 7f, -28f, 0f, -32f, -7f, -28f, -5f, -35f, -10f, -40f, -3f, -40f)
        kantoLine(0xFFE1C889, 3f, -9f, 2f, 0f, 9f, 9f, 2f)
    }
}

private fun DrawScope.drawMuscleLizards(ndex: Int, motion: Float) {
    val skin = if (ndex == 66) 0xFF93B9B3 else if (ndex == 67) 0xFFACA6C3 else 0xFFA2B4C7
    if (ndex == 66) {
        middleCurve(skin, 12f, 17f, 25f, 63f, 44f, 65f, 12f + motion * 4f)
    }
    for (side in listOf(-1f, 1f)) {
        kantoLine(skin, 18f, side * 14f, 21f, side * 26f, 40f)
        middleOval(skin, side * 32f, 44f, 30f, 15f)
        if (ndex == 68) {
            kantoLine(skin, 16f, side * 24f, 1f, side * 56f, 21f, side * 74f, 10f + motion * side * 4f)
            middleOval(skin, side * 76f, 8f + motion * side * 4f, 25f, 23f)
        }
        kantoLine(skin, if (ndex == 66) 14f else 19f, side * 23f, -15f, side * 49f, -7f, side * 58f, -29f + motion * side * 4f)
        middleOval(skin, side * 58f, -33f + motion * side * 4f, 26f, 25f)
        kantoLine(KANTO_INK, 2f, side * 52f, -38f + motion * side * 4f, side * 61f, -35f + motion * side * 4f)
    }
    middleOval(skin, 0f, 2f, if (ndex == 66) 47f else 66f, 58f)
    if (ndex != 66) {
        kantoPolygon(0xFF464867, -29f, 17f, 29f, 17f, 20f, 34f, 0f, 29f, -20f, 34f)
        kantoLine(0xFFC8AE64, 6f, -27f, 17f, 27f, 17f)
        middleOval(0xFFF5D36B, 0f, 18f, 16f, 12f)
        kantoLine(KANTO_INK, 2f, -22f, -7f, -8f, -4f)
        kantoLine(KANTO_INK, 2f, 22f, -7f, 8f, -4f)
    } else {
        for (y in listOf(-2f, 6f, 14f)) kantoLine(0xFF658E89, 2f, -12f, y, 12f, y)
    }
    middleOval(skin, 0f, -34f, 45f, 40f)
    for (x in listOf(-12f, 0f, 12f)) {
        kantoPolygon(0xFF7C929C, x - 4f, -47f, x - 2f, -66f + kotlin.math.abs(x) / 3f, x + 6f, -59f, x + 5f, -45f)
    }
    middleOval(0xFFFFFFFF, -12f, -35f, 15f, 13f)
    middleOval(0xFFFFFFFF, 12f, -35f, 15f, 13f)
    kantoEyes(0f, -35f, 11f, 2.5f)
    if (ndex == 68) {
        middleOval(0xFFCCAD91, 0f, -22f, 32f, 15f)
        kantoLine(KANTO_INK, 2f, -11f, -22f, 11f, -22f)
    } else {
        kantoLine(KANTO_INK, 2f, -11f, -23f, 8f, -21f)
    }
}

private fun DrawScope.middleLeaf(x: Float, y: Float, side: Float, motion: Float) {
    kantoPolygon(
        0xFF6CA660, x, y, x + side * 18f, y - 23f + motion,
        x + side * 42f, y - 24f + motion, x + side * 34f, y - 2f,
        x + side * 15f, y + 7f,
    )
    kantoLine(0xFF426C4D, 2f, x + side * 5f, y - 1f, x + side * 31f, y - 17f + motion)
}

private fun DrawScope.drawPitcherPlants(ndex: Int, motion: Float) {
    val yellow = 0xFFDDD371
    if (ndex == 69) {
        kantoLine(0xFF92754D, 7f, 0f, -5f, 0f, 22f, -13f, 45f, -29f, 47f)
        kantoLine(0xFF92754D, 6f, 0f, 22f, 16f, 42f, 32f, 43f)
        middleLeaf(-2f, 9f, -1f, motion * 4f)
        middleLeaf(2f, 12f, 1f, -motion * 4f)
        middleOval(yellow, -2f, -27f, 48f, 48f)
        kantoPolygon(yellow, 10f, -46f, 43f, -39f, 44f, -19f, 9f, -8f)
        middleOval(0xFFE89DAD, 43f, -29f, 17f, 31f)
        middleOval(0xFF6A4358, 45f, -29f, 7f, 17f)
        kantoEyes(-7f, -33f, 11f, 3.2f)
    } else if (ndex == 70) {
        middleCurve(0xFF92754D, 6f, 0f, -34f, 30f, -68f, 36f, -45f + motion * 3f)
        middleLeaf(-22f, 2f, -1f, motion * 4f)
        middleLeaf(22f, 2f, 1f, -motion * 4f)
        middleOval(yellow, 0f, -1f, 72f, 85f)
        middleOval(0xFF7EA065, -20f, -18f, 13f, 18f)
        middleOval(0xFF7EA065, 21f, 2f, 12f, 16f)
        middleOval(0xFFFFFFFF, -14f, -15f, 19f, 23f)
        middleOval(0xFFFFFFFF, 14f, -15f, 19f, 23f)
        kantoEyes(0f, -12f, 14f, 3.3f)
        middleOval(0xFFECA5AF, 0f, 22f, 52f, 29f)
        middleOval(0xFF65516A, 0f, 23f, 36f, 17f)
    } else {
        middleCurve(0xFF648952, 7f, 17f, -30f, 86f, -57f, 72f, 5f + motion * 5f)
        middleCurve(0xFF648952, 6f, 72f, 5f + motion * 5f, 57f, 20f, 58f, 2f)
        middleLeaf(-23f, 13f, -1f, motion * 4f)
        middleLeaf(22f, 17f, 1f, -motion * 4f)
        middleOval(yellow, 0f, 2f, 69f, 95f)
        kantoPolygon(yellow, -18f, 32f, 1f, 49f, 19f, 29f)
        middleOval(0xFF8D9C55, -21f, 5f, 14f, 21f)
        middleOval(0xFF8D9C55, 19f, 25f, 12f, 17f)
        middleOval(0xFFE697A0, 0f, -29f, 66f, 34f)
        middleOval(0xFF574557, 0f, -28f, 49f, 23f)
        kantoPolygon(0xFFFFF6DD, -21f, -26f, -12f, -24f, -17f, -11f)
        kantoPolygon(0xFFFFF6DD, 21f, -26f, 12f, -24f, 17f, -11f)
        kantoPolygon(0xFF6EAD67, -31f, -39f, -24f, -62f, 2f, -67f, 34f, -47f, 18f, -33f, -5f, -37f)
        kantoLine(0xFF416F4B, 2f, -19f, -48f, 4f, -51f, 27f, -45f)
        middleOval(0xFFFFFFFF, -14f, 4f, 18f, 15f)
        middleOval(0xFFFFFFFF, 14f, 4f, 18f, 15f)
        kantoEyes(0f, 4f, 13f, 3f)
    }
}

private fun DrawScope.drawJellyfish(tentacruel: Boolean, motion: Float) {
    val tentacles = if (tentacruel) 8 else 2
    for (i in 0 until tentacles) {
        val x = if (tentacruel) (i - 3.5f) * 11f else (i * 2f - 1f) * 17f
        val endX = x * 1.45f + motion * (if (i % 2 == 0) 5f else -5f)
        middleCurve(0xFFB7B9C6, if (tentacruel) 7f else 12f, x, -1f, x * 1.8f - motion * 5f, 29f, endX, 46f - i % 3 * 7f)
    }
    if (tentacruel) {
        kantoPolygon(0xFF73AFC4, -45f, -23f, -66f, -42f, -54f, -8f, -31f, 9f)
        kantoPolygon(0xFF73AFC4, 45f, -23f, 66f, -42f, 54f, -8f, 31f, 9f)
        middleOval(0xFF57607A, 0f, -1f, 48f, 45f)
        kantoLine(KANTO_INK, 5f, -18f, -3f, -32f, 11f, -20f, 28f)
        kantoLine(KANTO_INK, 5f, 18f, -3f, 32f, 11f, 20f, 28f)
    }
    middleOval(0xFF75BCD3, 0f, -29f, if (tentacruel) 105f else 77f, 62f)
    middleOval(0xFFDC5D7B, if (tentacruel) -29f else -20f, -40f, if (tentacruel) 35f else 23f, 34f)
    middleOval(0xFFDC5D7B, if (tentacruel) 29f else 20f, -40f, if (tentacruel) 35f else 23f, 34f)
    middleOval(0xFFF4A6AF, if (tentacruel) -33f else -23f, -47f, 9f, 12f)
    middleOval(0xFFF4A6AF, if (tentacruel) 25f else 17f, -47f, 9f, 12f)
    middleOval(0xFFE06787, 0f, -16f, 17f, 14f)
    kantoPolygon(0xFF78BED2, -33f, -12f, -15f, 7f, 0f, -6f, 16f, 7f, 34f, -12f)
    middleOval(0xFFFFFFFF, -16f, -1f, 17f, 13f)
    middleOval(0xFFFFFFFF, 16f, -1f, 17f, 13f)
    kantoEyes(0f, 0f, 15f, 2.8f)
}

private fun DrawScope.drawGolem(motion: Float) {
    val rock = 0xFF9C9789
    val hide = 0xFFB5A57F
    for (side in listOf(-1f, 1f)) {
        kantoLine(hide, 19f, side * 29f, 25f, side * 40f, 41f)
        middleOval(hide, side * 43f, 42f, 35f, 18f)
        kantoLine(hide, 18f, side * 37f, -10f, side * 61f, 5f + motion * side * 4f)
        middleOval(hide, side * 66f, 10f + motion * side * 4f, 27f, 25f)
        for (n in 0..2) {
            val x = side * (35f + n * 9f)
            kantoPolygon(0xFFF5EDCF, x - 3f, 45f, x, 50f, x + 4f, 44f)
        }
    }
    kantoPolygon(
        rock, -48f, -24f, -28f, -52f, 8f, -58f, 40f, -39f,
        53f, -8f, 43f, 26f, 15f, 42f, -22f, 40f, -49f, 16f,
    )
    kantoLine(0xFF615F59, 3f, -28f, -52f, -16f, -25f, -40f, -5f, -49f, 16f)
    kantoLine(0xFF615F59, 3f, -16f, -25f, 9f, -34f, 8f, -58f)
    kantoLine(0xFF615F59, 3f, 9f, -34f, 28f, -17f, 50f, -15f)
    kantoLine(0xFF615F59, 3f, -40f, -5f, -21f, 16f, -22f, 40f)
    kantoLine(0xFF615F59, 3f, -21f, 16f, 3f, 23f, 15f, 42f)
    kantoLine(0xFF615F59, 3f, 3f, 23f, 30f, 8f, 43f, 26f)
    kantoPolygon(hide, -15f, -28f, 15f, -29f, 31f, -10f, 22f, 14f, -3f, 17f, -22f, -2f)
    kantoPolygon(0xFFFFFFFF, -16f, -15f, -1f, -11f, -11f, -5f)
    kantoPolygon(0xFFFFFFFF, 5f, -12f, 22f, -18f, 20f, -6f)
    kantoEyes(3f, -10f, 12f, 2.5f)
    kantoLine(KANTO_INK, 3f, -8f, 5f, 19f, 3f)
    kantoPolygon(0xFFF5EDCF, 10f, 4f, 15f, 4f, 13f, 10f)
}

private fun DrawScope.middleFlame(x: Float, y: Float, scale: Float, motion: Float) {
    kantoPolygon(
        0xFFED7750, x - 16f * scale, y + 15f * scale,
        x - 24f * scale, y - 6f * scale, x - 10f * scale, y - 2f * scale,
        x - 7f * scale, y - 29f * scale + motion, x + 4f * scale, y - 13f * scale,
        x + 18f * scale, y - 33f * scale - motion, x + 21f * scale, y - 8f * scale,
        x + 12f * scale, y + 17f * scale,
    )
    kantoPolygon(
        0xFFFFD763, x - 9f * scale, y + 12f * scale,
        x - 11f * scale, y - 2f * scale, x + 1f * scale, y + 1f * scale,
        x + 10f * scale, y - 14f * scale, x + 9f * scale, y + 12f * scale,
    )
}

private fun DrawScope.drawFireHorses(rapidash: Boolean, motion: Float) {
    val cream = 0xFFF2DFC1
    middleFlame(62f, 10f, if (rapidash) 1.15f else 0.9f, motion * 3f)
    kantoLine(0xFFED7750, 13f, 32f, 1f, 62f, 16f)
    for ((index, x) in listOf(-28f, -6f, 19f, 35f).withIndex()) {
        val shift = if (index % 2 == 0) motion * 3f else -motion * 3f
        kantoLine(cream, 9f, x, 14f, x - 5f + shift, 39f)
        kantoLine(0xFF877777, 10f, x - 5f + shift, 39f, x - 6f + shift, 46f)
        if (rapidash || index == 0 || index == 3) {
            middleFlame(x - 5f + shift, 35f, 0.35f, motion)
        }
    }
    middleOval(cream, 4f, 6f, 83f, 41f)
    middleFlame(-21f, -20f, if (rapidash) 1.1f else 0.85f, motion * 3f)
    kantoPolygon(cream, -35f, 12f, -48f, -27f, -31f, -43f, -13f, -6f, -13f, 10f)
    kantoPolygon(cream, -44f, -43f, -47f, -64f, -35f, -51f, -25f, -63f, -25f, -39f)
    middleOval(cream, -42f, -35f, 35f, 40f)
    middleOval(cream, -56f, -25f, 33f, 20f)
    if (rapidash) {
        kantoPolygon(0xFFE9D6AB, -54f, -45f, -68f, -66f, -60f, -36f)
        middleFlame(-21f, -39f, 0.75f, motion * 2f)
    } else {
        middleFlame(-30f, -41f, 0.55f, motion * 2f)
    }
    middleOval(KANTO_INK, -48f, -38f, 6f, 9f)
    middleOval(0xFFFFFFFF, -49f, -40f, 2f, 3f)
    middleOval(KANTO_INK, -67f, -25f, 4f, 3f)
    kantoLine(KANTO_INK, 2f, -65f, -19f, -54f, -18f)
}

private fun DrawScope.drawSlowCreatures(slowbro: Boolean, motion: Float) {
    val pink = 0xFFE7A8AF
    middleCurve(pink, if (slowbro) 17f else 21f, 25f, 24f, 76f, 38f, 70f, -10f + motion * 4f)
    if (slowbro) {
        kantoPolygon(
            0xFFA9A4B9, 51f, 23f, 42f, 5f, 49f, -10f, 42f, -22f,
            57f, -23f, 60f, -44f, 73f, -32f, 88f, -43f, 84f, -20f,
            93f, -8f, 85f, 12f, 72f, 28f,
        )
        middleOval(0xFF595568, 65f, 14f, 28f, 30f)
        kantoPolygon(0xFFF4EDDB, 54f, 4f, 60f, 3f, 58f, 13f)
        kantoPolygon(0xFFF4EDDB, 71f, 1f, 77f, 5f, 70f, 12f)
        middleOval(0xFFFFFFFF, 58f, -14f, 15f, 18f)
        middleOval(0xFFFFFFFF, 77f, -15f, 15f, 18f)
        kantoEyes(67f, -14f, 9f, 2.7f)
        middleOval(pink, -21f, 45f, 33f, 13f)
        middleOval(pink, 23f, 45f, 33f, 13f)
        middleOval(pink, 0f, 12f, 75f, 75f)
        middleOval(0xFFF0D8BE, 0f, 19f, 49f, 50f)
        middleOval(pink, -33f, 16f, 23f, 34f)
        middleOval(pink, 32f, 18f, 22f, 31f)
    } else {
        middleOval(pink, 0f, 18f, 105f, 49f)
        middleOval(pink, -28f, 42f, 30f, 17f)
        middleOval(pink, 29f, 41f, 31f, 17f)
    }
    val hx = if (slowbro) -7f else -35f
    val hy = if (slowbro) -28f else -10f
    middleOval(pink, hx - 25f, hy - 20f, 21f, 23f)
    middleOval(pink, hx + 25f, hy - 20f, 21f, 23f)
    middleOval(0xFFC9899A, hx - 25f, hy - 20f, 9f, 11f)
    middleOval(0xFFC9899A, hx + 25f, hy - 20f, 9f, 11f)
    middleOval(pink, hx, hy, 67f, 49f)
    middleOval(0xFFFFFFFF, hx - 15f, hy - 6f, 21f, 19f)
    middleOval(0xFFFFFFFF, hx + 15f, hy - 6f, 21f, 19f)
    kantoEyes(hx, hy - 6f, 15f, 2.2f)
    middleOval(0xFFECD4AF, hx - 2f, hy + 12f, 58f, 25f)
    kantoLine(KANTO_INK, 2f, hx - 23f, hy + 13f, hx, hy + 19f, hx + 21f, hy + 12f)
    kantoPolygon(0xFFFFFFFF, hx - 19f, hy + 15f, hx - 12f, hy + 17f, hx - 15f, hy + 23f)
    kantoPolygon(0xFFFFFFFF, hx + 12f, hy + 16f, hx + 18f, hy + 14f, hx + 15f, hy + 23f)
    kantoEyes(hx - 2f, hy + 7f, 9f, 1.5f)
}

private fun DrawScope.middleMagnet(x: Float, y: Float, side: Float, scale: Float) {
    kantoLine(
        0xFFA7B7C5, 10f * scale,
        x + side * 25f * scale, y - 16f * scale,
        x + side * 9f * scale, y - 16f * scale,
        x + side * 5f * scale, y + 16f * scale,
        x + side * 25f * scale, y + 16f * scale,
    )
    kantoLine(0xFFDB777D, 11f * scale, x + side * 19f * scale, y - 16f * scale, x + side * 28f * scale, y - 16f * scale)
    kantoLine(0xFF6394CF, 11f * scale, x + side * 19f * scale, y + 16f * scale, x + side * 28f * scale, y + 16f * scale)
}

private fun DrawScope.middleMagnetBody(x: Float, y: Float, scale: Float) {
    kantoLine(0xFF9CA7B0, 7f * scale, x, y - 21f * scale, x, y - 34f * scale)
    kantoPolygon(
        0xFFD7DFE2, x - 9f * scale, y - 39f * scale,
        x + 9f * scale, y - 39f * scale, x + 9f * scale, y - 30f * scale,
        x - 9f * scale, y - 30f * scale,
    )
    kantoLine(KANTO_INK, 2f, x - 5f * scale, y - 35f * scale, x + 5f * scale, y - 35f * scale)
    middleOval(0xFFB6CDD8, x, y, 55f * scale, 52f * scale)
    middleOval(0xFFFFFFFF, x, y - 2f * scale, 29f * scale, 28f * scale)
    middleOval(KANTO_INK, x, y - 2f * scale, 7f * scale, 10f * scale)
    for (side in listOf(-1f, 1f)) {
        middleOval(0xFF9DADB8, x + side * 16f * scale, y + 13f * scale, 10f * scale, 10f * scale)
        kantoLine(KANTO_INK, 1.6f, x + (side * 16f - 3f) * scale, y + 13f * scale, x + (side * 16f + 3f) * scale, y + 13f * scale)
    }
}

private fun DrawScope.drawMagneticCreatures(magneton: Boolean, motion: Float) {
    if (magneton) {
        middleMagnet(-55f, 14f + motion * 2f, -1f, 0.85f)
        middleMagnet(55f, 14f - motion * 2f, 1f, 0.85f)
        middleMagnet(-23f, -31f, -1f, 0.72f)
        middleMagnet(23f, -31f, 1f, 0.72f)
        middleMagnetBody(-29f, 20f + motion * 2f, 1f)
        middleMagnetBody(29f, 20f - motion * 2f, 1f)
        middleMagnetBody(0f, -28f, 0.97f)
    } else {
        middleMagnet(-34f, -4f + motion * 3f, -1f, 1.2f)
        middleMagnet(34f, -4f - motion * 3f, 1f, 1.2f)
        middleMagnetBody(0f, -2f, 1.35f)
    }
}

private fun DrawScope.drawLeekDuck(motion: Float) {
    val feather = 0xFFAA926F
    for (side in listOf(-1f, 1f)) {
        kantoLine(0xFFDBB268, 8f, side * 16f, 28f, side * 22f, 44f)
        kantoPolygon(0xFFDBB268, side * 17f, 40f, side * 37f, 47f, side * 12f, 49f)
    }
    kantoPolygon(feather, 20f, 20f, 52f, 5f, 46f, 30f, 24f, 34f)
    middleOval(feather, 0f, 8f, 67f, 68f)
    middleOval(0xFFE8D9B7, -2f, 15f, 44f, 45f)
    middleOval(feather, -28f, 8f, 25f, 39f)
    kantoLine(0xFFE7E4B6, 12f, 24f, 33f, 55f, -18f + motion * 3f)
    kantoPolygon(0xFF659B5B, 49f, -12f, 48f, -53f, 58f, -40f, 65f, -64f, 70f, -41f, 84f, -49f, 65f, -11f)
    kantoLine(0xFF426D45, 2f, 56f, -16f, 65f, -47f)
    middleOval(feather, 29f, 12f, 25f, 22f)
    middleOval(feather, -4f, -30f, 58f, 45f)
    kantoPolygon(feather, -25f, -45f, -23f, -59f, -13f, -54f, -5f, -66f, 1f, -51f, 14f, -57f, 17f, -43f)
    middleOval(0xFFFFFFFF, -19f, -30f, 19f, 18f)
    middleOval(0xFFFFFFFF, 8f, -30f, 19f, 18f)
    kantoEyes(-5f, -29f, 13f, 3f)
    kantoLine(KANTO_INK, 5f, -29f, -42f, -6f, -35f, 18f, -42f)
    kantoPolygon(0xFFDCB268, -23f, -17f, 18f, -17f, 24f, -7f, -24f, -8f)
    kantoLine(KANTO_INK, 2f, -20f, -11f, 18f, -11f)
}

private fun DrawScope.drawRunningBirds(dodrio: Boolean, motion: Float) {
    val feather = if (dodrio) 0xFFB99665 else 0xFFBD9A70
    val neck = if (dodrio) 0xFF6A5652 else feather
    for (side in listOf(-1f, 1f)) {
        kantoLine(0xFFB39A66, 7f, side * 15f, 24f, side * 22f, 36f, side * (30f + motion * 3f), 46f)
        kantoLine(0xFFB39A66, 5f, side * (30f + motion * 3f), 46f, side * 45f, 48f)
        kantoLine(0xFFB39A66, 4f, side * (30f + motion * 3f), 46f, side * 29f, 51f)
    }
    if (dodrio) {
        kantoPolygon(0xFFA26570, 20f, 9f, 61f, -8f, 47f, 13f, 70f, 11f, 45f, 27f, 19f, 23f)
    }
    middleOval(feather, 0f, 11f, 66f, 49f)
    kantoPolygon(feather, -30f, 0f, -38f, 9f, -28f, 15f, -33f, 24f, -17f, 28f, 4f, 20f, 21f, 27f, 32f, 15f, 29f, 0f)
    val heads = if (dodrio) listOf(-45f to -25f, 0f to -44f, 45f to -25f) else listOf(-34f to -34f, 34f to -34f)
    for ((index, head) in heads.withIndex()) {
        val (hx, baseY) = head
        val hy = baseY + if (index % 2 == 0) motion * 2f else -motion * 2f
        kantoLine(neck, 9f, hx * 0.35f, 2f, hx * 0.75f, -8f, hx, hy + 13f)
        if (dodrio) {
            kantoPolygon(KANTO_INK, hx - 9f, hy - 12f, hx - 13f, hy - 22f, hx - 3f, hy - 18f, hx + 4f, hy - 22f, hx + 8f, hy - 10f)
        }
        middleOval(feather, hx, hy, 36f, 34f)
        val side = if (hx < 0) -1f else 1f
        kantoPolygon(0xFFE3C684, hx + side * 12f, hy - 3f, hx + side * 38f, hy + 5f, hx + side * 12f, hy + 8f)
        middleOval(0xFFFFFFFF, hx + side * 5f, hy - 5f, 13f, 14f)
        middleOval(KANTO_INK, hx + side * 7f, hy - 5f, 4f, 6f)
        if (dodrio && index == 0) {
            kantoLine(KANTO_INK, 2f, hx - 14f, hy - 13f, hx, hy - 7f)
        }
        if (dodrio && index == 2) {
            kantoLine(KANTO_INK, 2f, hx, hy - 10f, hx + 13f, hy - 15f)
        }
    }
}

private fun DrawScope.drawSeaLions(dewgong: Boolean, motion: Float) {
    val white = if (dewgong) 0xFFF5F0E4 else 0xFFE6EBEF
    kantoPolygon(
        white, 30f, 25f, 62f, 3f, 66f, -17f + motion * 4f,
        79f, -28f + motion * 4f, 83f, -4f, 94f, -6f,
        87f, 15f, 68f, 22f, 49f, 41f,
    )
    middleOval(white, 0f, 21f, 112f, 48f)
    middleOval(white, -29f, -4f, if (dewgong) 56f else 61f, 57f)
    kantoPolygon(white, -43f, -25f, -32f, -50f, -24f, -23f)
    middleOval(white, -28f, 33f, if (dewgong) 60f else 48f, 20f)
    kantoPolygon(white, 7f, 18f, 30f, 23f, 43f, 41f, 17f, 37f)
    if (dewgong) {
        kantoLine(0xFFAEBEC6, 2f, -45f, 30f, -24f, 33f, -8f, 30f)
        middleOval(KANTO_INK, -43f, -8f, 6f, 9f)
        middleOval(KANTO_INK, -17f, -8f, 6f, 9f)
        middleOval(0xFFFFFFFF, -44f, -10f, 2f, 3f)
        middleOval(0xFFFFFFFF, -18f, -10f, 2f, 3f)
    } else {
        kantoEyes(-30f, -10f, 14f, 4f)
        middleOval(0xFFDDD8CC, -31f, 8f, 42f, 24f)
    }
    middleOval(0xFF58576B, -32f, 0f, 12f, 8f)
    kantoSmile(-31f, 9f, 12f)
    if (!dewgong) {
        middleOval(0xFFDD95A7, -30f, 17f, 13f, 15f)
        kantoPolygon(0xFFFFFFFF, -44f, 9f, -37f, 12f, -41f, 23f)
        kantoPolygon(0xFFFFFFFF, -24f, 12f, -17f, 8f, -20f, 23f)
    }
}

private fun DrawScope.drawSludge(muk: Boolean, motion: Float) {
    val purple = if (muk) 0xFF987CB2 else 0xFFA08BBB
    val dark = 0xFF715F91
    val reach = if (muk) 78f else 65f
    val armY = if (muk) -39f + motion * 4f else -19f + motion * 4f
    kantoLine(purple, if (muk) 26f else 20f, -25f, 8f, -48f, -1f, -reach, armY + 10f)
    kantoLine(purple, 22f, 25f, 7f, 49f, 10f, reach - 2f, 0f - motion * 3f)
    for (i in 0..2) {
        kantoLine(purple, 8f, -reach + i * 7f, armY + 12f, -reach - 7f + i * 8f, armY - 8f - i % 2 * 7f)
        kantoLine(purple, 7f, reach - 6f + i * 6f, -motion * 3f, reach - 9f + i * 7f, -13f - motion * 3f - i % 2 * 7f)
    }
    val sludge = Path().apply {
        moveTo(-66f, 43f)
        cubicTo(-87f, 28f, -39f, 27f, -38f, -4f)
        cubicTo(-46f, -55f, 32f, -63f, 37f, -15f)
        cubicTo(42f, 5f, 41f, 29f, 70f, 31f)
        cubicTo(90f, 51f, 27f, 49f, 9f, 46f)
        cubicTo(-12f, 53f, -46f, 43f, -66f, 43f)
        close()
    }
    drawPath(sludge, Color(purple))
    drawPath(sludge, Color(KANTO_INK), style = Stroke(3f))
    middleOval(0xFFFFFFFF, -16f, -24f, 24f, 20f)
    middleOval(0xFFFFFFFF, 15f, -25f, 24f, 20f)
    kantoEyes(0f, -24f, 14f, 3f)
    if (muk) {
        kantoLine(KANTO_INK, 4f, -30f, -37f, -4f, -28f)
        kantoLine(KANTO_INK, 4f, 28f, -38f, 4f, -29f)
        middleOval(0xFF403649, 0f, 0f, 50f, 30f)
        kantoPolygon(0xFFBC96BC, -19f, 10f, -6f, 2f, 10f, 4f, 21f, 10f, 4f, 15f)
    } else {
        middleOval(0xFF403649, 0f, -2f, 43f, 24f)
        middleOval(0xFFCB9BB8, 0f, 6f, 26f, 9f)
    }
    middleCurve(dark, 3f, -30f, 18f, 0f, 31f, 30f, 18f)
    middleCurve(dark, 3f, -45f, 33f, -18f, 39f, -4f, 34f)
    middleCurve(dark, 3f, 14f, 37f, 40f, 40f, 50f, 35f)
    middleOval(0xFFB29AC6, -22f, -39f, 12f, 8f)
}

private fun DrawScope.drawShells(cloyster: Boolean, motion: Float) {
    val shell = if (cloyster) 0xFF8D84B2 else 0xFF9C9ED0
    if (cloyster) {
        kantoPolygon(
            shell, 0f, -67f, 14f, -44f, 40f, -58f, 39f, -35f,
            67f, -40f, 54f, -16f, 78f, -1f, 55f, 8f, 66f, 34f,
            39f, 32f, 29f, 50f, 6f, 35f, -17f, 50f, -30f, 33f,
            -60f, 39f, -49f, 13f, -77f, 1f, -54f, -17f,
            -68f, -42f, -39f, -35f, -42f, -59f, -14f, -43f,
        )
        middleOval(0xFFB0A7CB, 0f, -7f, 91f, 92f)
        kantoPolygon(shell, -44f, -23f, -22f, -46f, -30f, -4f, -16f, 31f, -40f, 15f)
        kantoPolygon(shell, 44f, -23f, 22f, -46f, 30f, -4f, 16f, 31f, 40f, 15f)
        middleOval(0xFF49445F, 0f, -2f, 55f, 61f)
        kantoPolygon(shell, -8f, -33f, 0f, -63f, 9f, -32f)
        kantoPolygon(0xFFFFFFFF, -23f, -11f, -4f, -4f, -18f, 1f)
        kantoPolygon(0xFFFFFFFF, 23f, -11f, 4f, -4f, 18f, 1f)
        kantoEyes(0f, -3f, 14f, 2.5f)
        kantoPolygon(0xFFF6F3EC, -19f, 11f, 0f, 17f, 20f, 9f, 12f, 24f, -9f, 25f)
        kantoLine(KANTO_INK, 2f, 0f, 17f, 0f, 24f)
    } else {
        kantoPolygon(shell, -47f, -9f, -41f, -37f, -19f, -52f, 0f, -41f, 22f, -51f, 43f, -32f, 49f, -6f, 27f, 11f, -28f, 11f)
        kantoLine(0xFF7078A3, 3f, -30f, -34f, -22f, -10f)
        kantoLine(0xFF7078A3, 3f, 0f, -35f, 0f, -9f)
        kantoLine(0xFF7078A3, 3f, 30f, -33f, 23f, -10f)
        middleOval(0xFF494660, 0f, 7f, 83f, 48f)
        middleOval(0xFFFFFFFF, -19f, 2f, 24f, 23f)
        middleOval(0xFFFFFFFF, 19f, 2f, 24f, 23f)
        kantoEyes(0f, 3f, 18f, 3.5f)
        kantoPolygon(shell, -49f, 11f, -26f, 28f, 0f, 26f, 25f, 28f, 49f, 10f, 39f, 36f, 12f, 44f, -14f, 43f, -38f, 34f)
        middleOval(0xFFE9A4BD, 0f, 32f + motion * 2f, 28f, 35f)
        kantoLine(0xFFA46D96, 2f, 0f, 25f, 0f, 43f + motion * 2f)
    }
}

private fun DrawScope.drawGhosts(ndex: Int, motion: Float) {
    val purple = if (ndex == 94) 0xFF8A79AF else 0xFF9180BD
    if (ndex == 92) {
        kantoPolygon(
            0xFFB895CB, -56f, -41f, -33f, -45f, -28f, -64f, -9f, -55f,
            9f, -65f, 23f, -48f, 46f, -53f, 47f, -31f,
            69f, -18f + motion * 3f, 56f, 2f, 73f, 18f, 49f, 22f,
            45f, 43f, 23f, 36f, 6f, 49f, -10f, 38f, -36f, 45f,
            -39f, 24f, -64f, 19f, -55f, -1f, -72f, -17f - motion * 3f,
            -52f, -22f,
        )
        middleOval(0xFF51455F, 0f, -8f, 90f, 88f)
    } else if (ndex == 93) {
        kantoPolygon(
            purple, -47f, -46f, -22f, -39f, -15f, -60f, 0f, -43f,
            17f, -59f, 28f, -37f, 46f, -45f, 37f, -23f, 49f, -9f,
            36f, 3f, 44f, 19f, 22f, 22f, 11f, 37f, -2f, 28f,
            -23f, 42f, -21f, 23f, -43f, 27f, -34f, 5f, -49f, -7f, -36f, -23f,
        )
        for (side in listOf(-1f, 1f)) {
            val y = 13f + motion * side * 5f
            kantoPolygon(
                purple, side * 60f, y + 14f, side * 53f, y,
                side * 60f, y - 21f, side * 66f, y - 6f,
                side * 76f, y - 24f, side * 77f, y - 5f,
                side * 89f, y - 14f, side * 85f, y + 5f, side * 71f, y + 17f,
            )
        }
    } else {
        kantoPolygon(purple, 28f, 22f, 64f, 10f + motion * 3f, 54f, 35f, 29f, 38f)
        middleOval(purple, -24f, 43f, 33f, 16f)
        middleOval(purple, 24f, 43f, 33f, 16f)
        kantoPolygon(purple, -36f, -25f, -39f, -62f, -16f, -44f, -5f, -55f, 5f, -44f, 20f, -54f, 27f, -42f, 42f, -62f, 41f, -22f)
        middleOval(purple, 0f, -1f, 89f, 91f)
        for (side in listOf(-1f, 1f)) {
            kantoPolygon(purple, side * 37f, -10f, side * 60f, 4f, side * 65f, 20f, side * 52f, 15f, side * 53f, 26f, side * 35f, 20f)
        }
    }
    val eyeColor = if (ndex == 94) 0xFFE77689 else 0xFFFFFFFF
    kantoPolygon(eyeColor, -33f, -27f, -5f, -17f, -12f, -7f, -27f, -10f)
    kantoPolygon(eyeColor, 33f, -27f, 5f, -17f, 12f, -7f, 27f, -10f)
    kantoEyes(0f, -16f, 19f, 3.3f)
    kantoPolygon(0xFFFFF4DF, -31f, 0f, -10f, 7f, 12f, 6f, 32f, -2f, 23f, 19f, 2f, 25f, -20f, 19f)
    if (ndex == 92) {
        kantoPolygon(0xFF51455F, -18f, 5f, -12f, 7f, -15f, 16f)
        kantoPolygon(0xFF51455F, 14f, 5f, 21f, 2f, 17f, 15f)
    } else {
        for (x in listOf(-17f, -5f, 8f, 20f)) {
            kantoLine(KANTO_INK, 2f, x, 7f, x - 1f, if (x == -5f || x == 8f) 22f else 16f)
        }
    }
}

private fun DrawScope.middleRock(x: Float, y: Float, size: Float, color: Long) {
    kantoPolygon(
        color, x - size * 0.5f, y - size * 0.2f, x - size * 0.24f, y - size * 0.51f,
        x + size * 0.26f, y - size * 0.46f, x + size * 0.52f, y - size * 0.08f,
        x + size * 0.33f, y + size * 0.42f, x - size * 0.17f, y + size * 0.5f,
        x - size * 0.51f, y + size * 0.18f,
    )
    kantoLine(0xFF6F7984, 2f, x - size * 0.28f, y - size * 0.23f, x + size * 0.03f, y - size * 0.09f, x + size * 0.25f, y - size * 0.29f)
}

private fun DrawScope.drawOnix(motion: Float) {
    val gray = 0xFFABB4BA
    kantoPolygon(gray, 62f, 28f, 90f, 10f + motion * 4f, 78f, 34f)
    middleRock(63f, 34f, 19f, gray)
    middleRock(44f, 39f, 25f, gray)
    middleRock(20f, 36f, 30f, gray)
    middleRock(-6f, 32f, 34f, gray)
    middleRock(-31f, 20f, 37f, gray)
    middleRock(-47f, -4f, 41f, gray)
    kantoPolygon(
        gray, -70f, -35f, -61f, -54f, -40f, -58f, -34f, -67f,
        -25f, -47f, -13f, -34f, -15f, -15f, -37f, -8f, -61f, -13f,
    )
    kantoPolygon(0xFFFFFFFF, -64f, -39f, -48f, -34f, -60f, -27f)
    kantoPolygon(0xFFFFFFFF, -41f, -35f, -23f, -42f, -27f, -28f)
    middleOval(KANTO_INK, -56f, -33f, 4f, 6f)
    middleOval(KANTO_INK, -31f, -34f, 4f, 6f)
    kantoLine(KANTO_INK, 3f, -61f, -19f, -43f, -22f, -25f, -18f)
    kantoLine(0xFF6F7984, 2f, -49f, -51f, -44f, -42f, -48f, -29f)
}

private fun DrawScope.drawDreamEaters(hypno: Boolean, motion: Float) {
    val yellow = 0xFFE0C360
    for (side in listOf(-1f, 1f)) {
        kantoLine(if (hypno) yellow else 0xFF937A64, 17f, side * 17f, 27f, side * 23f, 43f)
        middleOval(if (hypno) yellow else 0xFF937A64, side * 28f, 45f, 30f, 13f)
        kantoLine(yellow, 13f, side * 26f, -2f, side * 44f, 12f, side * 58f, -3f + side * motion * 3f)
        middleOval(yellow, side * 59f, -4f + side * motion * 3f, 21f, 20f)
    }
    middleOval(yellow, 0f, 8f, if (hypno) 58f else 78f, 68f)
    if (!hypno) {
        kantoPolygon(0xFF937A64, -36f, 10f, -18f, 15f, -3f, 11f, 14f, 17f, 36f, 11f, 32f, 32f, 15f, 40f, -17f, 40f, -33f, 29f)
        middleOval(yellow, -24f, -38f, 19f, 23f)
        middleOval(yellow, 24f, -38f, 19f, 23f)
    } else {
        kantoPolygon(0xFFF1EADF, -32f, -17f, -41f, -9f, -31f, -1f, -35f, 9f, -18f, 7f, -12f, 18f, 0f, 10f, 14f, 17f, 20f, 6f, 35f, 8f, 31f, -4f, 40f, -12f, 29f, -20f)
        kantoPolygon(yellow, -25f, -34f, -30f, -65f, -10f, -47f)
        kantoPolygon(yellow, 25f, -34f, 30f, -65f, 10f, -47f)
    }
    middleOval(yellow, 0f, -29f, 56f, 45f)
    middleOval(0xFFFFFFFF, -14f, -32f, 17f, 14f)
    middleOval(0xFFFFFFFF, 14f, -32f, 17f, 14f)
    kantoEyes(0f, -31f, 13f, 2.7f)
    kantoLine(KANTO_INK, 2f, -23f, -38f, -6f, -34f)
    kantoLine(KANTO_INK, 2f, 23f, -38f, 6f, -34f)
    if (hypno) {
        kantoPolygon(yellow, -5f, -29f, 5f, -27f, 17f, -11f, 4f, -9f, -5f, -16f)
        val px = 67f + motion * 6f
        kantoLine(KANTO_INK, 2f, 62f, -5f + motion * 3f, px, 23f)
        middleOval(0xFFEFD887, px, 33f, 19f, 23f)
        middleOval(0xFFC0A25B, px, 33f, 10f, 13f)
    } else {
        middleCurve(yellow, 14f, 0f, -21f, 13f, 7f, -9f, 8f)
        kantoLine(KANTO_INK, 2f, -7f, 7f, -2f, 9f)
    }
}

private fun DrawScope.middleClaw(x: Float, y: Float, size: Float, side: Float) {
    kantoPolygon(
        0xFFE68C67, x - size * 0.4f, y + size * 0.32f,
        x - size * 0.5f, y - size * 0.09f, x - size * 0.27f, y - size * 0.5f,
        x - size * 0.06f, y - size * 0.16f, x + size * 0.25f, y - size * 0.48f,
        x + size * 0.48f, y - size * 0.05f, x + size * 0.3f, y + size * 0.36f,
    )
    kantoPolygon(
        0xFFF4D9B1, x - size * 0.27f, y - size * 0.5f,
        x - size * 0.37f, y - size * 0.22f, x - size * 0.06f, y - size * 0.16f,
    )
    kantoPolygon(
        0xFFF4D9B1, x + size * 0.25f, y - size * 0.48f,
        x + size * 0.37f, y - size * 0.22f, x + size * 0.06f, y - size * 0.17f,
    )
    kantoLine(0xFFAE624F, 2f, x, y, x + side * size * 0.17f, y + size * 0.23f)
}

private fun DrawScope.drawCrabs(kingler: Boolean, motion: Float) {
    val orange = 0xFFE4946D
    for (side in listOf(-1f, 1f)) {
        for (i in 0..2) {
            val y = 15f + i * 8f
            kantoLine(orange, 7f, side * 25f, y, side * (46f + i * 5f), y + 3f, side * (48f + i * 7f), 45f - i * 2f)
        }
        val big = kingler && side < 0f
        val cx = side * (if (big) 62f else 58f)
        val cy = if (big) -27f + motion * 3f else -15f - motion * side * 4f
        kantoLine(orange, if (big) 13f else 9f, side * 27f, 7f, side * 48f, 0f, cx, cy + 13f)
        middleClaw(cx, cy, if (big) 63f else if (kingler) 34f else 40f, side)
    }
    middleOval(0xFFF0D4AA, 0f, 22f, 63f, 31f)
    middleOval(orange, 0f, 8f, 76f, 44f)
    if (kingler) {
        kantoPolygon(orange, -31f, -5f, -31f, -27f, -16f, -15f, -9f, -37f, 2f, -19f, 15f, -34f, 20f, -12f, 33f, -20f, 32f, 2f)
    }
    for (side in listOf(-1f, 1f)) {
        kantoLine(orange, 7f, side * 15f, -2f, side * 16f, -22f)
        middleOval(0xFFF8F1DB, side * 16f, -21f, 14f, 21f)
        middleOval(KANTO_INK, side * 16f, -22f, 4f, 8f)
    }
    kantoLine(KANTO_INK, 2f, -21f, 15f, -10f, 20f, 11f, 20f, 22f, 14f)
    if (kingler) {
        kantoPolygon(0xFFFFF4DD, -9f, 20f, -3f, 20f, -6f, 27f)
        kantoPolygon(0xFFFFF4DD, 6f, 20f, 12f, 20f, 9f, 26f)
    }
}

private fun DrawScope.drawVoltorb() {
    val ball = Path().apply {
        moveTo(-48f, -2f)
        cubicTo(-48f, -28.51f, -26.51f, -50f, 0f, -50f)
        cubicTo(26.51f, -50f, 48f, -28.51f, 48f, -2f)
        close()
    }
    middleOval(0xFFF1EEE5, 0f, -2f, 96f, 96f)
    drawPath(ball, Color(0xFFE36D76))
    drawPath(ball, Color(KANTO_INK), style = Stroke(3f))
    middleOval(0xFFF2A1A4, -20f, -32f, 18f, 11f)
    kantoPolygon(0xFFFFFFFF, -33f, -25f, -7f, -15f, -14f, -5f, -31f, -10f)
    kantoPolygon(0xFFFFFFFF, 33f, -25f, 7f, -15f, 14f, -5f, 31f, -10f)
    kantoEyes(0f, -14f, 21f, 3.1f)
}
