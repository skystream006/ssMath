package com.ssmath.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawKantoLate(ndex: Int, motion: Float) {
    when (ndex) {
        101 -> lateElectrode()
        102 -> lateExeggcute()
        103 -> lateExeggutor(motion)
        104, 105 -> lateBoneKeeper(ndex == 105, motion)
        106 -> lateHitmonlee(motion)
        107 -> lateHitmonchan(motion)
        108 -> lateLickitung(motion)
        109, 110 -> lateGasCloud(ndex == 110, motion)
        111, 112 -> lateRockRhino(ndex == 112, motion)
        113 -> lateChansey(motion)
        114 -> lateTangela(motion)
        115 -> lateKangaskhan(motion)
        116, 117 -> lateSeahorse(ndex == 117, motion)
        118, 119 -> lateHornFish(ndex == 119, motion)
        120, 121 -> lateStarfish(ndex == 121)
        122 -> lateMime(motion)
        123 -> lateScyther(motion)
        124 -> lateJynx(motion)
        125 -> lateElectabuzz(motion)
        126 -> lateMagmar(motion)
        127 -> latePinsir(motion)
        128 -> lateTauros(motion)
        129 -> lateMagikarp(motion)
        130 -> lateGyarados(motion)
        131 -> lateLapras(motion)
        132 -> lateDitto(motion)
        133, 134, 135, 136 -> lateEeveeFamily(ndex, motion)
        137 -> latePorygon(motion)
        138, 139 -> lateAmmonite(ndex == 139, motion)
        140, 141 -> lateHorseshoeCrab(ndex == 141, motion)
        142 -> lateAerodactyl(motion)
        143 -> lateSnorlax(motion)
        144, 145, 146 -> lateLegendaryBird(ndex, motion)
        147, 148 -> lateDragonSerpent(ndex == 148, motion)
        149 -> lateDragonite(motion)
        150 -> lateMewtwo(motion)
        151 -> lateMew(motion)
        else -> error("Unsupported late Kanto species: $ndex")
    }
}

private fun DrawScope.lateShape(color: Long, block: Path.() -> Unit) {
    val path = Path().apply(block)
    drawPath(path, Color(color))
    drawPath(path, Color(KANTO_INK), style = Stroke(2.5f, join = StrokeJoin.Round))
}

private fun DrawScope.lateTube(color: Long, width: Float, block: Path.() -> Unit) {
    val path = Path().apply(block)
    drawPath(path, Color(KANTO_INK), style = Stroke(width + 4f, cap = StrokeCap.Round))
    drawPath(path, Color(color), style = Stroke(width, cap = StrokeCap.Round))
}

private fun DrawScope.lateEye(x: Float, y: Float, width: Float = 14f, height: Float = 17f) {
    kantoOval(0xFFFFFCF0, x - width / 2f, y - height / 2f, width, height)
    kantoOval(KANTO_INK, x - 2f, y - height / 4f, 4f, height / 2f)
}

private fun DrawScope.lateElectrode() {
    kantoOval(0xFFE65B65, -49f, -56f, 98f, 98f)
    lateShape(0xFFFFFCF1) {
        moveTo(-49f, -7f)
        cubicTo(-49f, -72f, 49f, -72f, 49f, -7f)
        close()
    }
    kantoEyes(0f, -23f, 20f, 4f)
    kantoLine(KANTO_INK, 3f, -32f, -36f, -12f, -29f)
    kantoLine(KANTO_INK, 3f, 12f, -29f, 31f, -37f)
    lateShape(0xFFFFFCF1) {
        moveTo(-30f, -2f)
        quadraticTo(0f, 11f, 30f, -2f)
        quadraticTo(24f, 24f, 0f, 25f)
        quadraticTo(-22f, 23f, -30f, -2f)
        close()
    }
    kantoLine(KANTO_INK, 2f, -20f, 10f, 21f, 10f)
    drawOval(Color.White.copy(alpha = 0.5f), Offset(-33f, -46f), Size(20f, 8f))
}

private fun DrawScope.lateExeggcute() {
    val eggs = arrayOf(
        floatArrayOf(-43f, -28f), floatArrayOf(0f, -34f), floatArrayOf(41f, -26f),
        floatArrayOf(-56f, 7f), floatArrayOf(-13f, 12f), floatArrayOf(36f, 12f),
    )
    eggs.forEachIndexed { index, position ->
        val x = position[0]
        val y = position[1]
        lateShape(if (index % 2 == 0) 0xFFF5A8AC else 0xFFF2BEC1) {
            moveTo(x - 21f, y + 16f)
            cubicTo(x - 31f, y - 23f, x + 12f, y - 38f, x + 22f, y - 2f)
            quadraticTo(x + 34f, y + 33f, x, y + 32f)
            quadraticTo(x - 18f, y + 31f, x - 21f, y + 16f)
            close()
        }
        if (index == 1) {
            kantoPolygon(0xFFEDDA93, x - 18f, y - 13f, x - 10f, y - 22f,
                x - 1f, y - 15f, x + 6f, y - 23f, x + 16f, y - 13f, x + 9f, y - 6f)
        } else if (index == 3 || index == 4) {
            kantoLine(KANTO_INK, 2f, x - 5f, y - 20f, x - 9f, y - 12f,
                x - 1f, y - 6f, x - 4f, y)
        }
        kantoEyes(x + 1f, y + 7f, 9f, 2.7f)
        kantoLine(KANTO_INK, 2f, x - 14f, y, x - 5f, y + 2f)
        kantoSmile(x + 2f, y + 19f, 6f)
    }
}

private fun DrawScope.lateExeggutor(motion: Float) {
    kantoPolygon(0xFFC6A36F, -21f, -15f, 20f, -15f, 27f, 39f,
        43f, 45f, 40f, 51f, 14f, 51f, 3f, 30f, -10f, 49f, -38f, 49f, -40f, 43f, -25f, 35f)
    kantoLine(0xFF806441, 3f, -21f, 9f, 21f, 8f)
    kantoLine(0xFF806441, 3f, -24f, 24f, 24f, 23f)
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(0xFF5AAA65, 0f, -34f, side * 28f, -64f,
            side * 72f, -65f + motion * 2f, side * 48f, -52f, side * 26f, -47f)
        kantoPolygon(0xFF76C56E, side * 7f, -35f, side * 44f, -51f,
            side * 82f, -29f + motion * 3f, side * 50f, -33f, side * 28f, -27f)
    }
    kantoPolygon(0xFF67B060, -6f, -32f, -14f, -66f, 8f, -55f, 15f, -33f)
    for ((x, y) in listOf(-32f to -19f, 30f to -19f, 0f to -31f)) {
        kantoOval(0xFFD8BC85, x - 22f, y - 19f, 44f, 44f)
        kantoEyes(x, y - 2f, 9f, 3f)
        kantoSmile(x, y + 11f, 8f)
    }
}

private fun DrawScope.lateBoneKeeper(evolved: Boolean, motion: Float) {
    val brown = if (evolved) 0xFFAE805A else 0xFFC8A277
    kantoPolygon(brown, -19f, 15f, -49f, 28f, -61f, 13f, -52f, 43f, -18f, 40f)
    kantoOval(brown, -29f, -5f, 62f, 52f)
    kantoOval(0xFFEED3A0, -17f, 9f, 36f, 34f)
    kantoOval(brown, -40f, 36f, 32f, 15f)
    kantoOval(brown, 10f, 35f, 33f, 16f)
    kantoOval(brown, -42f, 0f, 19f, 26f)
    kantoOval(brown, 21f, 0f, 24f, 20f)
    val boneX = if (evolved) 62f else 54f
    val lift = motion * 3f
    kantoLine(KANTO_INK, 13f, boneX - 11f, 33f, boneX + 6f, -23f + lift)
    kantoLine(0xFFFFF3D7, 8f, boneX - 11f, 33f, boneX + 6f, -23f + lift)
    for ((x, y) in listOf(boneX - 15f to 32f, boneX - 6f to 36f,
        boneX + 1f to -26f + lift, boneX + 11f to -23f + lift)) {
        kantoOval(0xFFFFF3D7, x - 6f, y - 6f, 12f, 12f)
    }
    if (evolved) {
        kantoPolygon(0xFFF4EBD2, -31f, -19f, -35f, -48f, -25f, -61f,
            -15f, -43f, 1f, -52f, 18f, -58f, 19f, -39f,
            42f, -23f, 36f, -7f, 12f, -5f, -6f, -13f)
    } else {
        lateShape(0xFFF4EBD2) {
            moveTo(-33f, -16f)
            lineTo(-39f, -55f); lineTo(-21f, -44f)
            quadraticTo(-2f, -64f, 18f, -46f)
            lineTo(29f, -56f); lineTo(28f, -32f)
            lineTo(44f, -19f); lineTo(35f, -3f)
            quadraticTo(6f, -3f, -4f, -14f)
            quadraticTo(-25f, -5f, -33f, -16f)
            close()
        }
        kantoLine(0xFFBEB8A4, 2f, -12f, -49f, -7f, -34f, -14f, -27f)
    }
    kantoOval(KANTO_INK, 1f, -32f, 20f, 16f)
    kantoOval(0xFFFFFEF2, 12f, -28f, 5f, 7f)
    kantoOval(KANTO_INK, 32f, -18f, 4f, 5f)
}

private fun DrawScope.lateHitmonlee(motion: Float) {
    for (side in listOf(-1f, 1f)) {
        val x = side * (40f + motion * 3f)
        kantoLine(KANTO_INK, 17f, side * 18f, 12f, side * 26f, 26f, x, 39f)
        kantoLine(0xFFE7C897, 12f, side * 18f, 12f, side * 26f, 26f, x, 39f)
        repeat(4) { n ->
            val y = 18f + n * 5f
            val center = side * (20f + n * 4.5f + motion * n)
            kantoLine(0xFF9C7754, 2f, center - 5f, y + 2f, center + 5f, y - 2f)
        }
        kantoPolygon(0xFFA9855F, x - 11f, 33f, x + 12f, 38f,
            x + 22f, 48f, x - 12f, 49f)
        lateTube(0xFFC09B70, 9f) {
            moveTo(side * 22f, -26f)
            quadraticTo(side * 51f, -37f, side * 60f, -7f - motion * 3f)
        }
        kantoOval(0xFFC09B70, side * 60f - 9f, -11f - motion * 3f, 18f, 15f)
    }
    lateShape(0xFFC09B70) {
        moveTo(-31f, -29f)
        quadraticTo(-30f, -58f, 0f, -58f)
        quadraticTo(29f, -58f, 31f, -29f)
        lineTo(24f, 14f)
        quadraticTo(0f, 30f, -24f, 14f)
        close()
    }
    kantoPolygon(0xFFFFFCDF, -24f, -33f, -6f, -28f, -14f, -15f)
    kantoPolygon(0xFFFFFCDF, 24f, -33f, 6f, -28f, 14f, -15f)
    kantoLine(KANTO_INK, 3f, -15f, -28f, -14f, -22f)
    kantoLine(KANTO_INK, 3f, 15f, -28f, 14f, -22f)
}

private fun DrawScope.lateHitmonchan(motion: Float) {
    kantoLine(0xFFAF8D6D, 13f, -14f, 24f, -24f, 43f)
    kantoLine(0xFFAF8D6D, 13f, 15f, 24f, 24f, 43f)
    kantoOval(0xFFAD93D2, -39f, 37f, 30f, 15f)
    kantoOval(0xFFAD93D2, 12f, 37f, 30f, 15f)
    kantoPolygon(0xFF8D78AE, -19f, -6f, 20f, -6f, 31f, 28f,
        5f, 33f, 0f, 22f, -6f, 33f, -30f, 28f)
    kantoOval(0xFFB49A75, -23f, -21f, 46f, 35f)
    kantoPolygon(0xFFBDA17C, -24f, -28f, -29f, -43f, -19f, -61f,
        -10f, -50f, 0f, -65f, 9f, -50f, 21f, -59f, 27f, -41f, 23f, -25f,
        12f, -17f, -12f, -17f)
    kantoEyes(0f, -36f, 12f, 3f)
    kantoLine(KANTO_INK, 2f, -7f, -25f, 7f, -25f)
    kantoLine(0xFFB49A75, 12f, -20f, -8f, -42f, 2f, -48f, -21f + motion * 5f)
    kantoLine(0xFFB49A75, 12f, 20f, -8f, 43f, -3f, 51f, -32f - motion * 5f)
    kantoOval(0xFFD85061, -65f, -43f + motion * 5f, 31f, 31f)
    kantoOval(0xFFD85061, 36f, -53f - motion * 5f, 32f, 32f)
    kantoLine(0xFFAD344D, 3f, -61f, -21f + motion * 5f, -39f, -21f + motion * 5f)
    kantoLine(0xFFAD344D, 3f, 39f, -29f - motion * 5f, 62f, -29f - motion * 5f)
}

private fun DrawScope.lateLickitung(motion: Float) {
    lateShape(0xFFE8A4B2) {
        moveTo(28f, 17f)
        quadraticTo(61f, 26f, 67f, -11f)
        quadraticTo(88f, 32f, 43f, 39f)
        close()
    }
    kantoOval(0xFFE8A4B2, -42f, -15f, 87f, 62f)
    kantoOval(0xFFF7D8BF, -30f, 1f, 64f, 42f)
    kantoLine(0xFFCA9C96, 2f, -23f, 17f, 27f, 17f)
    kantoLine(0xFFCA9C96, 2f, -20f, 29f, 23f, 29f)
    kantoOval(0xFFE8A4B2, -48f, 35f, 30f, 15f)
    kantoOval(0xFFE8A4B2, 16f, 35f, 30f, 15f)
    kantoOval(0xFFE8A4B2, -57f, -1f, 24f, 25f)
    kantoOval(0xFFE8A4B2, 32f, -1f, 24f, 25f)
    kantoOval(0xFFE8A4B2, -37f, -53f, 72f, 56f)
    kantoEyes(-1f, -32f, 19f, 3.5f)
    kantoOval(0xFF864862, -26f, -19f, 48f, 26f)
    lateShape(0xFFF383A2) {
        moveTo(-20f, -8f)
        cubicTo(-39f, -5f, -82f, 12f + motion * 3f, -75f, 31f + motion * 3f)
        cubicTo(-66f, 49f, -31f, 23f, 12f, -5f)
        close()
    }
    kantoLine(0xFFC4567F, 2f, -12f, -3f, -58f, 26f + motion * 3f)
}

private fun DrawScope.lateGasCloud(evolved: Boolean, motion: Float) {
    if (evolved) {
        kantoOval(0xFFCEC5A0, -47f, -64f + motion * 2f, 28f, 20f)
        kantoOval(0xFFCEC5A0, 64f, -53f - motion * 2f, 28f, 22f)
        kantoLine(0xFF9481B8, 20f, -4f, -7f, 46f, -23f)
        lateGasOrb(-26f, 3f, 0.88f, false)
        lateGasOrb(49f, -22f, 0.57f, true)
    } else {
        kantoOval(0xFFCBCBA3, -71f, -39f + motion * 2f, 26f, 18f)
        kantoOval(0xFFCBCBA3, 48f, 13f - motion * 2f, 29f, 23f)
        lateGasOrb(0f, -6f, 1f, false)
    }
}

private fun DrawScope.lateGasOrb(x: Float, y: Float, scale: Float, small: Boolean) {
    withTransform({
        translate(x, y)
        scale(scale, scale, pivot = Offset.Zero)
    }) {
        for ((px, py) in listOf(-38f to -24f, -23f to -42f, 20f to -41f,
            39f to -21f, 42f to 17f, 21f to 40f, -26f to 38f, -42f to 10f)) {
            kantoOval(0xFF9E86BA, px - 9f, py - 8f, 18f, 16f)
        }
        kantoOval(0xFFAA91C8, -45f, -45f, 90f, 90f)
        kantoOval(0xFF8F77AE, -34f, -28f, 13f, 10f)
        kantoOval(0xFF8F77AE, 26f, 3f, 12f, 10f)
        kantoEyes(0f, -15f, 19f, 4f)
        if (small) {
            kantoLine(KANTO_INK, 3f, -18f, 8f, -6f, 2f, 11f, 7f)
        } else {
            kantoSmile(0f, -1f, 24f)
            kantoPolygon(0xFFFFFAE4, -17f, 3f, -7f, 6f, -10f, 13f)
            kantoPolygon(0xFFFFFAE4, 8f, 6f, 18f, 3f, 13f, 13f)
        }
        kantoLine(0xFFEFE5C8, 5f, -13f, 29f, 13f, 41f)
        kantoLine(0xFFEFE5C8, 5f, -13f, 41f, 13f, 29f)
        kantoOval(0xFFEFE5C8, -11f, 17f, 22f, 20f)
        kantoEyes(0f, 25f, 4f, 2f)
    }
}

private fun DrawScope.lateRockRhino(evolved: Boolean, motion: Float) {
    val stone = 0xFFAAB4BE
    if (!evolved) {
        kantoPolygon(stone, 42f, -10f, 67f, 5f, 59f, 25f, 39f, 24f)
        for (x in listOf(-35f, 27f)) {
            kantoPolygon(stone, x - 10f, 12f, x + 18f, 12f,
                x + 20f, 43f, x - 14f, 43f, x - 17f, 34f)
            kantoLine(0xFFF2ECDC, 4f, x - 8f, 40f, x - 8f, 35f)
            kantoLine(0xFFF2ECDC, 4f, x + 2f, 40f, x + 2f, 35f)
        }
        kantoPolygon(stone, -48f, -18f, -24f, -36f, 28f, -34f,
            51f, -9f, 48f, 23f, -37f, 25f, -58f, 6f)
        kantoPolygon(0xFFCBD1D6, -26f, -34f, -11f, -49f, 0f, -33f,
            17f, -46f, 29f, -30f, 41f, -25f, 13f, -11f, -18f, -12f)
        kantoLine(0xFF6F7D8A, 3f, 13f, -11f, 14f, 21f)
        kantoPolygon(stone, -55f, -13f, -70f, -34f, -45f, -29f,
            -25f, -12f, -22f, 12f, -40f, 29f, -71f, 22f, -79f, 4f)
        kantoPolygon(0xFFE7E4D8, -70f, -2f, -84f, -28f, -56f, -9f)
        lateEye(-48f, -3f, 13f, 10f)
        kantoLine(KANTO_INK, 2f, -67f, 16f, -51f, 16f)
    } else {
        kantoPolygon(stone, 28f, 16f, 61f, 22f, 75f, 7f + motion * 3f,
            69f, 36f, 39f, 40f, 17f, 34f)
        kantoOval(stone, -35f, -9f, 69f, 57f)
        kantoOval(0xFFDAD8CE, -21f, 2f, 41f, 41f)
        repeat(3) { n -> kantoLine(0xFF9DA4A5, 2f, -18f, 12f + n * 10f, 17f, 12f + n * 10f) }
        kantoPolygon(stone, -30f, 28f, -13f, 34f, -13f, 49f, -47f, 49f, -42f, 40f)
        kantoPolygon(stone, 12f, 33f, 29f, 28f, 43f, 44f, 43f, 49f, 12f, 49f)
        kantoPolygon(stone, -29f, -14f, -47f, -4f, -57f, 17f, -43f, 25f, -29f, 8f)
        kantoPolygon(stone, 26f, -15f, 47f, -7f, 51f, 18f, 36f, 21f, 25f, 5f)
        kantoPolygon(stone, -29f, -31f, -33f, -61f, -16f, -50f, -2f, -54f,
            20f, -62f, 22f, -42f, 37f, -28f, 31f, -10f, -17f, -9f)
        kantoPolygon(0xFFE7E4D8, 15f, -30f, 33f, -50f, 31f, -24f)
        kantoLine(0xFFA9A99E, 2f, 24f, -36f, 30f, -33f)
        lateEye(-6f, -33f, 15f, 11f)
        kantoLine(KANTO_INK, 2f, 8f, -18f, 28f, -18f)
        for (x in listOf(-38f, -28f, 23f, 33f)) {
            kantoPolygon(0xFFF7F0DB, x - 4f, 49f, x, 42f, x + 4f, 49f)
        }
    }
}

private fun DrawScope.lateChansey(motion: Float) {
    kantoOval(0xFFE795AE, -44f, 36f, 33f, 14f)
    kantoOval(0xFFE795AE, 12f, 36f, 33f, 14f)
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(0xFFF3B4C6, side * 33f, -26f, side * 61f, -35f,
            side * 51f, -21f, side * 62f, -14f, side * 47f, -9f,
            side * 57f, -1f, side * 35f, 2f)
    }
    kantoOval(0xFFF3B4C6, -46f, -49f, 92f, 93f)
    kantoEyes(0f, -27f, 14f, 3f)
    kantoSmile(0f, -17f, 9f)
    kantoOval(0xFFFFF1D1, -16f, 3f, 32f, 37f)
    lateShape(0xFFEAA5BA) {
        moveTo(-30f, 16f)
        quadraticTo(0f, 32f, 30f, 16f)
        quadraticTo(28f, 42f, 0f, 43f)
        quadraticTo(-28f, 42f, -30f, 16f)
        close()
    }
    kantoOval(0xFFF3B4C6, -52f, -2f + motion * 2f, 29f, 15f)
    kantoOval(0xFFF3B4C6, 24f, -2f - motion * 2f, 29f, 15f)
}

private fun DrawScope.lateTangela(motion: Float) {
    kantoOval(0xFFCE6972, -39f, 32f, 34f, 19f)
    kantoOval(0xFFCE6972, 9f, 32f, 34f, 19f)
    kantoOval(0xFF263E59, -40f, -42f, 81f, 80f)
    repeat(10) { n ->
        val x = -39f + n * 8.5f
        lateTube(if (n % 2 == 0) 0xFF65A7CA else 0xFF4585B6, 7f) {
            moveTo(x, 31f)
            cubicTo(x - 28f, 11f, x + 24f, -7f, x - 4f, -21f)
            cubicTo(x - 26f, -51f, x + 17f, -58f + motion * 2f, x + 6f, -28f)
            quadraticTo(x - 9f, 2f, x + 8f, 29f)
        }
    }
    lateTube(0xFF76B2D1, 7f) {
        moveTo(-39f, 10f); cubicTo(-65f, -3f, -57f, -32f, -33f, -28f)
    }
    lateTube(0xFF76B2D1, 7f) {
        moveTo(37f, 4f); cubicTo(65f, -6f, 60f, 28f + motion * 3f, 42f, 27f)
    }
    kantoOval(0xFF263E59, -23f, -16f, 46f, 24f)
    lateEye(-12f, -5f, 18f, 21f)
    lateEye(12f, -5f, 18f, 21f)
}

private fun DrawScope.lateKangaskhan(motion: Float) {
    kantoPolygon(0xFFB99477, 25f, 21f, 63f, 22f, 72f, -1f + motion * 3f,
        77f, 30f, 49f, 44f, 22f, 39f)
    kantoOval(0xFFB99477, -37f, -13f, 76f, 59f)
    kantoOval(0xFFDFCBA0, -23f, 1f, 48f, 42f)
    kantoOval(0xFFB99477, -46f, 34f, 33f, 17f)
    kantoOval(0xFFB99477, 18f, 34f, 33f, 17f)
    kantoOval(0xFFB99477, -51f, -6f, 23f, 29f)
    kantoOval(0xFFB99477, 29f, -6f, 23f, 29f)
    kantoPolygon(0xFFB99477, -30f, -28f, -43f, -56f, -18f, -48f,
        -5f, -56f, 16f, -50f, 32f, -60f, 33f, -32f, 24f, -12f,
        -17f, -11f)
    kantoOval(0xFFE1CAAA, -23f, -30f, 48f, 26f)
    kantoEyes(0f, -36f, 16f, 3.5f)
    kantoLine(KANTO_INK, 2f, -10f, -16f, 10f, -16f)
    kantoPolygon(0xFFFFF3DB, -14f, -21f, -8f, -21f, -10f, -14f)
    kantoPolygon(0xFFFFF3DB, 8f, -21f, 14f, -21f, 10f, -14f)
    kantoPolygon(0xFFC5AEC8, -17f, 17f, -21f, 1f, -8f, 7f, 6f, 7f,
        18f, 1f, 18f, 22f, 10f, 31f, -10f, 31f)
    kantoEyes(0f, 17f, 8f, 2.6f)
    kantoSmile(0f, 24f, 5f)
    lateShape(0xFFD1B485) {
        moveTo(-25f, 25f)
        quadraticTo(0f, 37f, 26f, 25f)
        quadraticTo(18f, 47f, -15f, 42f)
        close()
    }
}

private fun DrawScope.lateSeahorse(evolved: Boolean, motion: Float) {
    val blue = if (evolved) 0xFF5E9FC9 else 0xFF77BBDC
    lateTube(blue, 12f) {
        moveTo(1f, 20f)
        cubicTo(42f, 44f, -18f, 54f, -20f, 35f)
        cubicTo(-20f, 22f, -1f, 26f, -7f, 38f)
    }
    if (evolved) {
        kantoPolygon(blue, -17f, -13f, -51f, -31f - motion * 3f,
            -38f, -4f, -55f, 8f, -29f, 11f, -35f, 30f, -12f, 22f)
        kantoPolygon(blue, 9f, -33f, 11f, -66f, -2f, -51f,
            -20f, -65f, -20f, -44f, -38f, -46f, -25f, -28f)
    } else {
        kantoPolygon(0xFFE5D7AB, -13f, -9f, -43f, -17f - motion * 3f,
            -36f, 1f, -42f, 19f, -14f, 15f)
        kantoPolygon(blue, -20f, -34f, -21f, -55f, -10f, -47f, -1f, -63f,
            8f, -46f, 20f, -54f, 19f, -32f)
    }
    kantoOval(blue, -23f, -15f, 49f, 48f)
    kantoOval(0xFFF0D9A8, -7f, -5f, 29f, 33f)
    repeat(3) { n -> kantoLine(0xFFB7A883, 2f, -3f, 4f + n * 8f, 18f, 4f + n * 8f) }
    kantoOval(blue, -26f, -51f, 56f, 43f)
    kantoPolygon(blue, 21f, -35f, 51f, -33f, 51f, -18f, 23f, -20f)
    kantoOval(0xFF39739D, 46f, -34f, 10f, 17f)
    lateEye(9f, -34f, 15f, 18f)
    if (evolved) kantoLine(KANTO_INK, 3f, -1f, -45f, 18f, -41f)
}

private fun DrawScope.lateHornFish(evolved: Boolean, motion: Float) {
    val orange = if (evolved) 0xFFE98658 else 0xFFF19E75
    lateShape(0xFFFFF2E4) {
        moveTo(29f, -11f)
        quadraticTo(57f, -13f, 79f, -43f + motion * 4f)
        quadraticTo(90f, -3f, 68f, 9f)
        quadraticTo(89f, 25f, 78f, 45f - motion * 3f)
        quadraticTo(55f, 28f, 28f, 17f)
        close()
    }
    kantoPolygon(orange, 47f, -9f, 77f, -34f + motion * 4f, 64f, 7f,
        77f, 35f - motion * 3f, 44f, 16f)
    kantoPolygon(orange, -17f, -23f, 0f, -54f, 10f, -38f, 28f, -40f, 39f, -15f)
    kantoOval(if (evolved) orange else 0xFFFFF3E6, -55f, -30f, 102f, 67f)
    kantoPolygon(0xFFFFEBCB, -46f, -17f, -36f, -59f, -26f, -25f)
    if (evolved) {
        kantoOval(0xFFFFEDCE, -55f, -16f, 35f, 45f)
        for ((x, y) in listOf(4f to -17f, 20f to 5f, -1f to 15f)) {
            kantoOval(0xFF5B4A50, x, y, 15f, 13f)
        }
    } else {
        kantoOval(orange, -31f, -28f, 28f, 26f)
        kantoOval(orange, 10f, 5f, 27f, 23f)
    }
    lateShape(0xFFFFF4DD) {
        moveTo(-1f, 6f)
        quadraticTo(-17f, 16f, -7f, 44f)
        quadraticTo(11f, 37f, 24f, 9f)
        close()
    }
    kantoLine(orange, 3f, 5f, 14f, -3f, 32f)
    lateEye(-34f, -4f, 17f, 21f)
    kantoOval(0xFFEDA7A1, -63f, 9f, 15f, 13f)
}

private fun DrawScope.lateStarPoints(color: Long, radius: Float, inner: Float, rotation: Float) {
    val points = FloatArray(20)
    repeat(10) { n ->
        val angle = (rotation + n * 36f) * PI.toFloat() / 180f
        val r = if (n % 2 == 0) radius else inner
        points[n * 2] = cos(angle) * r
        points[n * 2 + 1] = sin(angle) * r - 5f
    }
    kantoPolygon(color, *points)
}

private fun DrawScope.lateStarfish(evolved: Boolean) {
    if (evolved) lateStarPoints(0xFF7876B6, 57f, 25f, -54f)
    lateStarPoints(if (evolved) 0xFFA292C9 else 0xFFC9A774, 58f, 25f, -90f)
    kantoPolygon(0xFFE8C45E, 0f, -27f, 21f, -13f, 21f, 7f,
        0f, 20f, -21f, 7f, -21f, -13f)
    kantoOval(0xFFE17880, -15f, -21f, 30f, 30f)
    kantoPolygon(0xFFFFC1B8, -8f, -15f, 2f, -16f, -3f, -5f, -10f, -4f)
    if (!evolved) {
        kantoLine(0xFF9F794C, 3f, -21f, 8f, -31f, 32f)
        kantoLine(0xFFEBC776, 4f, -31f, 16f, -19f, 21f)
    }
}

private fun DrawScope.lateMime(motion: Float) {
    val lift = motion * 3f
    kantoLine(0xFFECAFC1, 11f, -16f, 18f, -26f, 38f)
    kantoLine(0xFFECAFC1, 11f, 16f, 18f, 26f, 38f)
    kantoOval(0xFF54749F, -47f, 36f, 32f, 15f)
    kantoOval(0xFF54749F, 15f, 36f, 32f, 15f)
    kantoOval(0xFFFFE4D8, -28f, -15f, 56f, 51f)
    kantoOval(0xFFE47187, -14f, -3f, 28f, 28f)
    for (side in listOf(-1f, 1f)) {
        kantoLine(0xFFF4CFCB, 10f, side * 26f, -9f, side * 48f, 3f,
            side * 62f, -19f + lift)
        kantoOval(0xFFE47187, side * 29f - 9f, -18f, 18f, 18f)
        kantoOval(0xFFE47187, side * 49f - 6f, -3f, 12f, 12f)
        kantoPolygon(0xFF54749F, side * 15f, -39f, side * 31f, -58f,
            side * 38f, -34f, side * 28f, -21f)
        val hx = side * 67f
        for ((dx, dy) in listOf(-13f to -15f, -5f to -24f, 6f to -24f, 15f to -14f)) {
            lateTube(0xFFFFF2E7, 6f) {
                moveTo(hx + side * dx * 0.45f, -24f + lift)
                lineTo(hx + side * dx, -24f + dy + lift)
            }
        }
        kantoOval(0xFFFFF2E7, hx - 14f, -34f + lift, 28f, 26f)
        kantoOval(0xFFE47187, hx - 5f, -28f + lift, 10f, 10f)
    }
    kantoOval(0xFFFFE4D8, -27f, -55f, 54f, 46f)
    kantoEyes(0f, -37f, 12f, 3f)
    kantoOval(0xFFE47187, -24f, -32f, 13f, 13f)
    kantoOval(0xFFE47187, 11f, -32f, 13f, 13f)
    kantoOval(0xFFE47187, -5f, -31f, 10f, 9f)
    kantoSmile(0f, -21f, 8f)
}

private fun DrawScope.lateScyther(motion: Float) {
    for (side in listOf(-1f, 1f)) {
        lateShape(0xFFF3F2CD) {
            moveTo(side * 8f, -19f)
            quadraticTo(side * 36f, -61f, side * 55f, -49f)
            quadraticTo(side * 67f, -22f, side * 23f, 9f)
            close()
        }
        kantoPolygon(0xFF79B579, side * 11f, 16f, side * 30f, 20f,
            side * 38f, 44f, side * 25f, 49f, side * 16f, 32f, side * 6f, 29f)
        kantoPolygon(0xFFFFECCE, side * 26f, 42f, side * 44f, 48f,
            side * 18f, 49f)
        kantoPolygon(0xFF79B579, side * 13f, -23f, side * 36f, -22f,
            side * 51f, -4f, side * 43f, 8f, side * 25f, -3f)
        lateShape(0xFFF7F5DB) {
            moveTo(side * 44f, -5f)
            quadraticTo(side * 82f, -22f + motion * 3f, side * 88f, 24f + motion * 3f)
            quadraticTo(side * 72f, 6f, side * 49f, 12f)
            close()
        }
    }
    kantoOval(0xFF83BC78, -20f, -1f, 40f, 39f)
    kantoLine(0xFF4D805B, 2f, -17f, 16f, 17f, 16f)
    kantoPolygon(0xFF669D6C, -21f, -27f, 19f, -27f, 27f, -9f,
        14f, 7f, -14f, 7f, -28f, -11f)
    kantoPolygon(0xFF8DC483, -23f, -34f, -28f, -60f, -12f, -48f,
        0f, -57f, 13f, -48f, 27f, -60f, 23f, -31f, 0f, -14f)
    kantoPolygon(0xFFFFF8E5, -20f, -39f, -4f, -32f, -14f, -28f)
    kantoPolygon(0xFFFFF8E5, 20f, -39f, 4f, -32f, 14f, -28f)
    kantoLine(KANTO_INK, 3f, -12f, -35f, -12f, -30f)
    kantoLine(KANTO_INK, 3f, 12f, -35f, 12f, -30f)
}

private fun DrawScope.lateJynx(motion: Float) {
    kantoPolygon(0xFFE1747D, -24f, -12f, 24f, -12f, 49f, 47f,
        27f, 50f, 0f, 45f, -28f, 50f, -50f, 46f)
    kantoPolygon(0xFFBF536C, -8f, 3f, -18f, 46f, 0f, 45f, 16f, 47f, 7f, 2f)
    for (side in listOf(-1f, 1f)) {
        kantoLine(0xFFDABD6C, 13f, side * 20f, -6f, side * 39f, 5f,
            side * 55f, -8f - motion * 3f)
        kantoOval(0xFFAC8AC2, side * 64f - 12f, -20f - motion * 3f, 24f, 22f)
        kantoLine(0xFFAC8AC2, 5f, side * 64f, -15f - motion * 3f,
            side * 73f, -28f - motion * 3f)
    }
    lateShape(0xFFF2D77B) {
        moveTo(-34f, 7f)
        lineTo(-39f, -27f)
        quadraticTo(-37f, -63f, 0f, -62f)
        quadraticTo(36f, -63f, 39f, -27f)
        lineTo(35f, 9f); lineTo(24f, 3f); lineTo(15f, 10f)
        lineTo(-17f, 8f); lineTo(-25f, 1f)
        close()
    }
    kantoOval(0xFFA58AC4, -25f, -44f, 50f, 42f)
    kantoPolygon(0xFFF2D77B, -28f, -42f, -19f, -58f, 15f, -58f,
        29f, -41f, 6f, -45f, -3f, -38f, -10f, -46f)
    lateEye(-11f, -30f, 13f, 14f)
    lateEye(11f, -30f, 13f, 14f)
    kantoOval(0xFFF0ADB9, -16f, -17f, 32f, 13f)
    kantoLine(0xFFC27495, 2f, -11f, -10f, 11f, -10f)
    kantoOval(0xFFE8D08A, -23f, 3f, 20f, 13f)
    kantoOval(0xFFE8D08A, 3f, 3f, 20f, 13f)
}

private fun DrawScope.lateElectabuzz(motion: Float) {
    kantoPolygon(0xFFF0C453, 24f, 17f, 61f, 16f, 52f, -2f,
        77f, 2f, 66f, -19f + motion * 3f, 86f, -10f, 85f, 14f, 68f, 10f,
        70f, 30f, 27f, 37f)
    kantoPolygon(KANTO_INK, 53f, 16f, 62f, 16f, 66f, 29f, 56f, 31f)
    kantoOval(0xFFF0C453, -30f, -16f, 61f, 60f)
    kantoPolygon(KANTO_INK, -20f, -7f, 5f, -3f, -5f, 7f,
        21f, 9f, -8f, 17f, 0f, 6f, -21f, 3f)
    for (side in listOf(-1f, 1f)) {
        kantoOval(0xFFF0C453, side * 25f - 13f, 30f, 30f, 21f)
        kantoLine(KANTO_INK, 5f, side * 17f, 37f, side * 29f, 32f)
        lateTube(0xFFF0C453, 16f) {
            moveTo(side * 24f, -8f)
            lineTo(side * 48f, 5f)
            lineTo(side * 59f, -12f + motion * 4f)
        }
        kantoLine(KANTO_INK, 6f, side * 39f, -5f, side * 35f, 6f)
        kantoOval(0xFFF0C453, side * 61f - 12f, -24f + motion * 4f, 24f, 24f)
        kantoPolygon(0xFFF0C453, side * 12f, -40f, side * 17f, -64f,
            side * 28f, -63f, side * 26f, -37f)
        kantoLine(KANTO_INK, 5f, side * 19f, -60f, side * 27f, -58f)
    }
    kantoOval(0xFFF0C453, -31f, -49f, 62f, 42f)
    kantoPolygon(KANTO_INK, -8f, -48f, 10f, -48f, 2f, -32f, -6f, -39f)
    lateEye(-15f, -31f, 16f, 12f)
    lateEye(15f, -31f, 16f, 12f)
    kantoLine(KANTO_INK, 3f, -25f, -39f, -7f, -34f)
    kantoLine(KANTO_INK, 3f, 25f, -39f, 7f, -34f)
    kantoLine(KANTO_INK, 2f, -11f, -18f, 11f, -18f)
}

private fun DrawScope.lateFlame(x: Float, y: Float, scale: Float, motion: Float) {
    withTransform({
        translate(x, y)
        scale(scale, scale, pivot = Offset.Zero)
    }) {
        lateShape(0xFFEE7852) {
            moveTo(-14f, 11f)
            quadraticTo(-28f, -4f, -11f, -21f)
            lineTo(-8f, -7f)
            lineTo(4f + motion * 3f, -35f)
            quadraticTo(19f, -22f, 13f, -9f)
            lineTo(24f, -20f)
            quadraticTo(29f, 10f, 12f, 18f)
            quadraticTo(-5f, 21f, -14f, 11f)
            close()
        }
        kantoPolygon(0xFFFFD468, -8f, 9f, -2f, -4f, 4f, 1f, 11f, -14f,
            14f, 10f, 4f, 16f)
    }
}

private fun DrawScope.lateMagmar(motion: Float) {
    lateTube(0xFFE9A056, 11f) {
        moveTo(26f, 23f)
        quadraticTo(52f, 37f, 62f, 9f)
    }
    lateFlame(66f, -2f, 0.67f, motion)
    kantoOval(0xFFE58D52, -31f, -15f, 63f, 61f)
    kantoPolygon(0xFFF8CD62, -24f, 24f, -14f, -3f, -4f, 14f, 7f, -8f,
        14f, 16f, 24f, 0f, 24f, 33f, 0f, 41f)
    for (side in listOf(-1f, 1f)) {
        kantoOval(0xFFAE6C65, side * 26f - 15f, 35f, 31f, 15f)
        kantoLine(0xFFE8A353, 15f, side * 25f, -10f, side * 47f, -2f,
            side * 55f, -18f + motion * 3f)
        kantoLine(0xFFCE644E, 5f, side * 39f, -8f, side * 34f, 4f)
        kantoOval(0xFFE8A353, side * 57f - 11f, -30f + motion * 3f, 22f, 23f)
    }
    kantoOval(0xFFE89457, -28f, -47f, 55f, 40f)
    lateFlame(-17f, -47f, 0.48f, motion)
    lateFlame(14f, -47f, 0.48f, -motion)
    kantoOval(0xFFF6CA63, -25f, -29f, 51f, 20f)
    kantoLine(KANTO_INK, 2f, -21f, -18f, 21f, -18f)
    kantoEyes(0f, -36f, 13f, 3.5f)
    kantoLine(KANTO_INK, 3f, -23f, -42f, -6f, -38f)
    kantoLine(KANTO_INK, 3f, 23f, -42f, 6f, -38f)
}

private fun DrawScope.latePinsir(motion: Float) {
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(0xFFAA8C71, side * 16f, 20f, side * 35f, 30f,
            side * 36f, 43f, side * 52f, 48f, side * 19f, 49f, side * 15f, 37f)
        kantoLine(0xFFAA8C71, 11f, side * 28f, -13f, side * 52f, -4f,
            side * 61f, -23f + motion * 3f)
        kantoOval(0xFFAA8C71, side * 62f - 10f, -32f + motion * 3f, 20f, 19f)
        kantoPolygon(0xFFFFF0D4, side * 58f, -28f + motion * 3f,
            side * 57f, -41f + motion * 3f, side * 65f, -31f + motion * 3f)
    }
    kantoOval(0xFFB49B80, -35f, -37f, 70f, 78f)
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(0xFFE2D5B7, side * 15f, -29f, side * 31f, -47f,
            side * 29f, -66f, side * 43f, -55f, side * 43f, -39f,
            side * 24f, -20f)
        kantoPolygon(0xFFE2D5B7, side * 30f, -52f, side * 21f, -57f,
            side * 26f, -45f)
        kantoPolygon(0xFFE2D5B7, side * 35f, -39f, side * 24f, -42f,
            side * 27f, -32f)
    }
    kantoPolygon(0xFFFFF5DE, -28f, -12f, -8f, -8f, -20f, -1f)
    kantoPolygon(0xFFFFF5DE, 28f, -12f, 8f, -8f, 20f, -1f)
    kantoLine(KANTO_INK, 3f, -18f, -9f, -18f, -4f)
    kantoLine(KANTO_INK, 3f, 18f, -9f, 18f, -4f)
    kantoOval(0xFF5A4B49, -15f, 0f, 30f, 29f)
    repeat(4) { n ->
        kantoPolygon(0xFFF2E4C8, -13f, 3f + n * 6f, -3f, 6f + n * 6f,
            -13f, 9f + n * 6f)
        kantoPolygon(0xFFF2E4C8, 13f, 3f + n * 6f, 3f, 6f + n * 6f,
            13f, 9f + n * 6f)
    }
}

private fun DrawScope.lateTauros(motion: Float) {
    repeat(3) { n ->
        val y = -26f + n * 16f + motion * 4f
        lateTube(0xFFB99464, 4f) {
            moveTo(39f, 1f)
            quadraticTo(64f, 5f + n * 7f, 74f, y)
            lineTo(86f, y - 6f)
        }
        kantoOval(0xFF675345, 81f, y - 13f, 12f, 14f)
    }
    for (x in listOf(-25f, 26f)) {
        kantoPolygon(0xFFC5A071, x - 7f, 14f, x + 9f, 15f,
            x + 7f, 44f, x - 9f, 44f)
        kantoPolygon(0xFF6A5D55, x - 9f, 39f, x + 8f, 39f, x + 10f, 49f, x - 12f, 49f)
    }
    kantoOval(0xFFC5A071, -43f, -28f, 94f, 61f)
    kantoOval(0xFF947455, -55f, -29f, 41f, 58f)
    kantoPolygon(0xFFD3B181, -47f, -30f, -66f, -20f, -64f, 9f,
        -52f, 25f, -30f, 24f, -21f, 6f, -25f, -23f)
    lateShape(0xFFE5DEBF) {
        moveTo(-58f, -20f)
        quadraticTo(-80f, -21f, -77f, -49f)
        quadraticTo(-68f, -32f, -51f, -32f)
        close()
    }
    lateShape(0xFFE5DEBF) {
        moveTo(-29f, -26f)
        quadraticTo(-5f, -30f, -7f, -52f)
        quadraticTo(5f, -21f, -25f, -15f)
        close()
    }
    kantoOval(0xFFBDA982, -62f, 6f, 38f, 24f)
    kantoEyes(-43f, -8f, 12f, 3f)
    kantoEyes(-43f, 17f, 9f, 2f)
    repeat(3) { n -> kantoOval(0xFF6A635D, -46f, -29f + n * 7f, 6f, 6f) }
}

private fun DrawScope.lateMagikarp(motion: Float) {
    kantoPolygon(0xFFF9E4BD, 34f, -10f, 79f, -35f + motion * 5f,
        71f, 1f, 80f, 37f - motion * 5f, 35f, 18f)
    kantoPolygon(0xFFF2C56A, -14f, -27f, -13f, -52f, 0f, -40f,
        14f, -56f, 19f, -34f, 32f, -44f, 35f, -16f)
    kantoOval(0xFFE88E64, -55f, -29f, 106f, 66f)
    kantoLine(0xFFC6684A, 3f, 18f, -23f, 4f, -6f, 22f, 8f, 7f, 28f)
    kantoLine(0xFFC6684A, 3f, 35f, -13f, 23f, 1f, 39f, 15f)
    kantoPolygon(0xFFF9E4BD, -4f, -2f, 10f, 6f, -9f, 30f, -15f, 16f)
    kantoPolygon(0xFFF2C56A, -9f, 33f, 0f, 46f, 13f, 35f)
    lateEye(-30f, -8f, 25f, 28f)
    kantoOval(0xFFF6DAB1, -67f, 3f, 24f, 30f)
    kantoOval(0xFF996C64, -62f, 10f, 12f, 16f)
    lateTube(0xFFF4D586, 3f) {
        moveTo(-53f, 20f); quadraticTo(-85f, 9f, -88f, 34f)
    }
    lateTube(0xFFF4D586, 3f) {
        moveTo(-45f, 25f); quadraticTo(-25f, 44f, -10f, 40f)
    }
}

private fun DrawScope.lateGyarados(motion: Float) {
    kantoPolygon(0xFFD3E4E7, 59f, -9f, 68f, -41f + motion * 3f,
        77f, -29f, 89f, -39f + motion * 3f, 84f, -8f, 68f, 10f)
    lateTube(0xFF509BCA, 27f) {
        moveTo(-39f, -9f)
        cubicTo(-45f, 41f, -8f, 45f, 9f, 23f)
        cubicTo(37f, -8f, 49f, 48f, 69f, 26f)
        quadraticTo(80f, 10f, 68f, -7f)
    }
    lateTube(0xFFF0DCAD, 12f) {
        moveTo(-43f, 4f)
        cubicTo(-41f, 34f, -10f, 41f, 8f, 24f)
        cubicTo(36f, 3f, 44f, 41f, 65f, 32f)
    }
    for ((x, y) in listOf(-28f to 29f, -13f to 31f, 25f to 24f, 42f to 32f, 59f to 31f)) {
        kantoLine(0xFFB3A98F, 2f, x, y - 5f, x - 3f, y + 7f)
    }
    for ((x, y) in listOf(-25f to 8f, 17f to 1f, 50f to 6f)) {
        kantoPolygon(0xFFDFEAE0, x - 7f, y + 7f, x - 2f, y - 13f, x + 10f, y + 3f)
    }
    kantoPolygon(0xFF4E97C6, -67f, -35f, -70f, -57f, -48f, -47f,
        -30f, -57f, -17f, -35f, -22f, 0f, -51f, 10f, -66f, -5f)
    kantoPolygon(0xFFF2E9CE, -62f, -40f, -72f, -64f, -50f, -48f,
        -40f, -66f, -34f, -42f, -19f, -55f, -22f, -30f)
    kantoPolygon(0xFFF8F2D8, -67f, -16f, -57f, -20f, -38f, -18f,
        -27f, -4f, -39f, 14f, -58f, 13f, -70f, 0f)
    kantoOval(0xFF8D5160, -61f, -14f, 28f, 26f)
    kantoPolygon(0xFFFFFCEB, -61f, -13f, -52f, -14f, -56f, -4f)
    kantoPolygon(0xFFFFFCEB, -42f, -13f, -33f, -9f, -42f, -2f)
    lateEye(-52f, -30f, 16f, 11f)
    kantoLine(KANTO_INK, 3f, -63f, -37f, -42f, -34f)
    lateTube(0xFF478DC2, 4f) {
        moveTo(-64f, -8f); quadraticTo(-88f, -10f, -88f, 18f + motion * 3f)
    }
    lateTube(0xFF478DC2, 4f) {
        moveTo(-27f, -5f); quadraticTo(-11f, -12f, -11f, -31f - motion * 3f)
    }
}

private fun DrawScope.lateLapras(motion: Float) {
    kantoPolygon(0xFF76B8D4, 37f, 17f, 77f, 12f, 61f, 30f, 33f, 35f)
    kantoOval(0xFF76B8D4, -42f, -4f, 103f, 46f)
    for (side in listOf(-1f, 1f)) {
        lateShape(0xFF76B8D4) {
            moveTo(side * 22f, 22f)
            quadraticTo(side * 63f, 25f, side * 75f, 47f + motion * 2f)
            quadraticTo(side * 40f, 49f, side * 17f, 32f)
            close()
        }
    }
    kantoOval(0xFF959CAD, -9f, -17f, 66f, 47f)
    for ((x, y) in listOf(4f to -15f, 25f to -20f, 44f to -10f, 17f to 5f, 42f to 9f)) {
        kantoPolygon(0xFFB8BECC, x - 7f, y + 8f, x, y - 10f, x + 8f, y + 8f)
    }
    lateShape(0xFF76B8D4) {
        moveTo(-46f, 27f)
        quadraticTo(-61f, 4f, -55f, -31f)
        lineTo(-31f, -35f)
        quadraticTo(-36f, 1f, -13f, 27f)
        quadraticTo(-26f, 38f, -46f, 27f)
        close()
    }
    lateShape(0xFFF1DFC0) {
        moveTo(-46f, -26f)
        quadraticTo(-47f, 13f, -27f, 29f)
        lineTo(-41f, 29f)
        quadraticTo(-55f, 2f, -53f, -24f)
        close()
    }
    kantoOval(0xFF76B8D4, -67f, -56f, 50f, 36f)
    kantoOval(0xFF76B8D4, -79f, -37f, 34f, 19f)
    kantoPolygon(0xFFF2EACC, -51f, -52f, -46f, -67f, -38f, -51f)
    lateShape(0xFF76B8D4) {
        moveTo(-24f, -43f)
        quadraticTo(2f, -57f, -1f, -35f)
        quadraticTo(-7f, -22f, -23f, -31f)
        close()
    }
    kantoLine(0xFF417FAD, 2f, -18f, -35f, -7f, -39f, -7f, -34f)
    lateEye(-53f, -41f, 13f, 16f)
    kantoSmile(-64f, -29f, 8f)
}

private fun DrawScope.lateDitto(motion: Float) {
    lateShape(0xFFBC9AD1) {
        moveTo(-64f, 35f)
        cubicTo(-75f, 24f, -42f, 6f, -48f, -13f)
        cubicTo(-58f, -47f, -27f, -46f, -13f, -35f)
        cubicTo(4f, -24f, 17f, -57f, 35f, -38f)
        cubicTo(44f, -24f, 38f, -5f, 58f, 9f + motion * 3f)
        cubicTo(83f, 31f, 57f, 44f, 37f, 40f)
        quadraticTo(18f, 50f, -5f, 42f)
        quadraticTo(-28f, 50f, -38f, 40f)
        quadraticTo(-52f, 47f, -64f, 35f)
        close()
    }
    kantoEyes(0f, -10f, 21f, 2.7f)
    kantoSmile(0f, 5f, 21f)
    drawOval(Color.White.copy(alpha = 0.2f), Offset(-37f, -28f), Size(16f, 9f))
}

private fun DrawScope.lateEeveeFamily(ndex: Int, motion: Float) {
    val body = when (ndex) {
        134 -> 0xFF75BDD0
        135 -> 0xFFF2CE69
        136 -> 0xFFEAA36B
        else -> 0xFFBD9670
    }
    when (ndex) {
        134 -> {
            lateTube(body, 13f) {
                moveTo(36f, 21f)
                quadraticTo(81f, 39f, 68f, -5f + motion * 3f)
            }
            kantoPolygon(0xFF709CC5, 66f, 2f, 50f, -15f, 58f, -37f + motion * 3f,
                69f, -22f, 87f, -27f + motion * 3f, 82f, -4f)
        }
        135 -> kantoPolygon(body, 32f, 17f, 58f, 9f, 64f, -18f + motion * 3f,
            71f, 3f, 88f, -2f, 78f, 18f, 86f, 31f, 62f, 27f, 42f, 36f)
        136 -> lateShape(0xFFF5D998) {
            moveTo(32f, 27f)
            cubicTo(40f, -11f, 84f, -8f, 72f, -39f + motion * 3f)
            cubicTo(103f, -15f, 85f, 25f, 65f, 37f)
            quadraticTo(45f, 48f, 32f, 27f)
            close()
        }
        else -> {
            lateShape(body) {
                moveTo(32f, 23f)
                quadraticTo(48f, -18f, 83f, -26f + motion * 3f)
                quadraticTo(88f, 12f, 69f, 30f)
                quadraticTo(50f, 41f, 32f, 23f)
                close()
            }
            kantoPolygon(0xFFF1DCA7, 61f, -16f, 83f, -26f + motion * 3f,
                81f, 1f, 71f, -2f, 67f, 9f, 61f, 0f, 50f, 2f)
        }
    }
    kantoOval(body, -26f, -5f, 79f, 43f)
    for (x in listOf(-20f, 24f)) {
        kantoPolygon(body, x - 8f, 20f, x + 10f, 20f, x + 9f, 41f,
            x + 15f, 46f, x - 11f, 47f)
        kantoLine(KANTO_INK, 1.5f, x + 3f, 42f, x + 3f, 46f)
    }
    when (ndex) {
        134 -> kantoPolygon(0xFFF3ECD4, -30f, -15f, -9f, -15f, 10f, -3f,
            5f, 6f, 13f, 12f, 0f, 16f, -6f, 28f, -19f, 17f,
            -34f, 22f, -33f, 8f, -47f, 2f, -34f, -4f)
        135 -> kantoPolygon(0xFFFFF2CF, -32f, -13f, -9f, -11f, 11f, -7f,
            2f, 3f, 19f, 7f, 4f, 14f, 10f, 25f, -7f, 19f,
            -17f, 33f, -25f, 19f, -41f, 24f, -38f, 10f, -51f, 2f, -38f, -3f)
        else -> {
            lateShape(0xFFF2D7A0) {
                moveTo(-38f, -11f)
                quadraticTo(-19f, -20f, 2f, -7f)
                quadraticTo(24f, 11f, 5f, 21f)
                lineTo(-5f, 17f); lineTo(-16f, 30f); lineTo(-25f, 21f)
                quadraticTo(-52f, 23f, -46f, 1f)
                close()
            }
        }
    }
    if (ndex == 134) {
        kantoPolygon(0xFF699AC3, -42f, -32f, -63f, -48f, -65f, -24f,
            -55f, -10f, -38f, -14f)
        kantoPolygon(0xFFF4DFAD, -43f, -28f, -59f, -40f, -59f, -23f, -43f, -17f)
        kantoPolygon(0xFF699AC3, -1f, -34f, 21f, -51f, 28f, -29f,
            15f, -10f, -1f, -14f)
        kantoPolygon(0xFFF4DFAD, 2f, -29f, 19f, -43f, 22f, -27f, 5f, -16f)
    } else {
        kantoPolygon(body, -41f, -28f, -59f, -65f, -36f, -54f, -23f, -28f)
        kantoPolygon(body, -12f, -30f, 6f, -64f, 17f, -55f, 7f, -24f)
        kantoPolygon(0xFF785A59, -43f, -34f, -52f, -55f, -37f, -46f, -31f, -31f)
        kantoPolygon(0xFF785A59, -5f, -33f, 7f, -55f, 9f, -42f, 2f, -29f)
    }
    kantoOval(body, -48f, -43f, 57f, 43f)
    when (ndex) {
        134 -> {
            kantoPolygon(0xFF6C91BA, -32f, -39f, -20f, -63f, -7f, -39f)
            kantoLine(0xFFDDE6D4, 2f, -20f, -57f, -20f, -39f)
        }
        135 -> {
            kantoPolygon(body, -46f, -13f, -57f, -12f, -47f, -25f)
            kantoPolygon(body, 6f, -15f, 18f, -12f, 10f, -28f)
            kantoPolygon(body, -30f, -40f, -24f, -54f, -15f, -44f, -5f, -49f, -6f, -37f)
        }
        136 -> lateShape(0xFFF5D998) {
            moveTo(-35f, -37f)
            quadraticTo(-40f, -53f, -25f, -56f)
            lineTo(-15f, -67f); lineTo(-10f, -53f)
            quadraticTo(5f, -54f, 0f, -38f)
            quadraticTo(-16f, -46f, -35f, -37f)
            close()
        }
    }
    lateEye(-34f, -22f, 12f, 17f)
    lateEye(-9f, -22f, 12f, 17f)
    kantoPolygon(KANTO_INK, -25f, -12f, -17f, -12f, -21f, -8f)
    kantoSmile(-21f, -6f, 7f)
}

private fun DrawScope.latePorygon(motion: Float) {
    kantoPolygon(0xFF75ABCD, 33f, 2f, 76f, -21f + motion * 4f,
        72f, 6f, 42f, 28f)
    kantoPolygon(0xFFE58EAB, -16f, -10f, 27f, -19f, 50f, 9f,
        33f, 34f, -17f, 29f, -33f, 7f)
    kantoPolygon(0xFFC87098, 26f, -17f, 50f, 9f, 33f, 34f, 10f, 14f)
    kantoPolygon(0xFF8FC5E0, -17f, 21f, -1f, 34f, -21f, 49f, -46f, 44f)
    kantoPolygon(0xFF8FC5E0, 29f, 26f, 46f, 31f, 48f, 48f, 19f, 43f)
    kantoPolygon(0xFFE58EAB, -53f, -35f, -30f, -55f, -4f, -44f,
        2f, -12f, -29f, 1f, -55f, -14f)
    kantoPolygon(0xFFC87098, -30f, -55f, -4f, -44f, 2f, -12f, -25f, -22f)
    kantoPolygon(0xFF8FC5E0, -53f, -25f, -27f, -15f, -43f, 4f,
        -78f, 0f, -78f, -12f)
    kantoPolygon(0xFF6497BD, -78f, 0f, -43f, 4f, -27f, -15f, -48f, -6f)
    lateEye(-37f, -33f, 15f, 17f)
}

private fun DrawScope.lateAmmonite(evolved: Boolean, motion: Float) {
    val blue = if (evolved) 0xFF67ADC9 else 0xFF87C7DA
    for (n in 0..5) {
        val x = -34f + n * 13f
        lateTube(blue, if (evolved) 10f else 9f) {
            moveTo(x * 0.7f, 6f)
            quadraticTo(x * 1.6f, 22f, x * 1.5f, 36f + (n % 2) * 4f + motion * 2f)
            quadraticTo(x * 1.5f + 8f, 46f, x * 1.5f + 12f, 35f)
        }
    }
    kantoOval(0xFFD8C4A0, -40f, -54f, 80f, 71f)
    if (evolved) {
        for ((x, y) in listOf(-33f to -40f, -14f to -52f, 12f to -52f, 33f to -39f)) {
            kantoPolygon(0xFFE9DAB9, x - 8f, y + 8f, x, y - 13f, x + 8f, y + 8f)
        }
    }
    drawPath(Path().apply {
        moveTo(27f, -5f)
        cubicTo(43f, -30f, 10f, -51f, -12f, -38f)
        cubicTo(-36f, -25f, -14f, 2f, 7f, -9f)
        cubicTo(24f, -21f, 4f, -36f, -5f, -23f)
        quadraticTo(-8f, -15f, 3f, -17f)
    }, Color(0xFF9F8765), style = Stroke(3f, cap = StrokeCap.Round))
    kantoOval(blue, -31f, -7f, 63f, 37f)
    lateEye(-17f, 0f, 20f, 24f)
    lateEye(17f, 0f, 20f, 24f)
    if (evolved) {
        kantoPolygon(0xFFF2E6C7, -10f, 17f, 0f, 9f, 10f, 17f, 0f, 30f)
        kantoLine(KANTO_INK, 2f, 0f, 11f, 0f, 25f)
        kantoLine(KANTO_INK, 2f, -7f, 18f, 7f, 18f)
    } else {
        kantoSmile(0f, 18f, 8f)
    }
}

private fun DrawScope.lateHorseshoeCrab(evolved: Boolean, motion: Float) {
    val shell = 0xFFB49B6C
    if (!evolved) {
        for (side in listOf(-1f, 1f)) {
            kantoPolygon(0xFFE8D6AB, side * 27f, 9f, side * 50f, 12f,
                side * 57f, 31f + motion * 2f, side * 32f, 24f)
            kantoPolygon(0xFFE8D6AB, side * 8f, 19f, side * 23f, 25f,
                side * 22f, 44f - motion * 2f, side * 8f, 34f)
        }
        kantoOval(0xFF494B49, -43f, -11f, 86f, 46f)
        lateShape(shell) {
            moveTo(-51f, 11f)
            cubicTo(-57f, -46f, 52f, -61f, 53f, 8f)
            lineTo(42f, 24f); lineTo(20f, 14f); lineTo(0f, 18f)
            lineTo(-21f, 14f); lineTo(-41f, 24f)
            close()
        }
        kantoOval(0xFFD59B7B, -25f, -5f, 10f, 8f)
        kantoOval(0xFFD59B7B, 15f, -5f, 10f, 8f)
        kantoOval(0xFFF48A73, -27f, 19f, 13f, 9f)
        kantoOval(0xFFF48A73, 14f, 19f, 13f, 9f)
        drawOval(Color.White.copy(alpha = 0.18f), Offset(-28f, -29f), Size(29f, 12f))
    } else {
        kantoPolygon(shell, 10f, 15f, 42f, 29f, 49f, 43f, 14f, 30f)
        for (side in listOf(-1f, 1f)) {
            kantoPolygon(shell, side * 9f, 16f, side * 25f, 21f,
                side * 22f, 35f, side * 38f, 47f, side * 17f, 47f,
                side * 8f, 33f)
            kantoPolygon(0xFFF5E7C9, side * 28f, 41f, side * 44f, 50f,
                side * 18f, 49f)
            kantoLine(0xFFB49B6C, 10f, side * 18f, -18f, side * 41f, -7f)
            lateShape(0xFFF2ECD5) {
                moveTo(side * 38f, -13f)
                quadraticTo(side * 77f, -26f - motion * 3f, side * 80f, 27f + motion * 3f)
                quadraticTo(side * 64f, 9f, side * 42f, 5f)
                close()
            }
        }
        kantoPolygon(shell, -23f, -23f, 23f, -23f, 21f, 8f,
            0f, 27f, -20f, 9f)
        repeat(3) { n ->
            kantoLine(0xFF7C6E52, 2f, -18f + n * 3f, -7f + n * 9f,
                0f, -2f + n * 9f, 18f - n * 3f, -7f + n * 9f)
        }
        lateShape(shell) {
            moveTo(-44f, -24f)
            lineTo(-37f, -47f)
            quadraticTo(0f, -67f, 37f, -47f)
            lineTo(44f, -24f); lineTo(18f, -30f); lineTo(0f, -18f); lineTo(-18f, -30f)
            close()
        }
        kantoPolygon(0xFFFFF6D8, -27f, -32f, -8f, -27f, -21f, -22f)
        kantoPolygon(0xFFFFF6D8, 27f, -32f, 8f, -27f, 21f, -22f)
        kantoEyes(0f, -27f, 19f, 2.5f)
    }
}

private fun DrawScope.lateAerodactyl(motion: Float) {
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(0xFFA79CC3, side * 12f, -13f, side * 42f, -50f - motion * 4f,
            side * 92f, -22f - motion * 3f, side * 75f, -16f,
            side * 64f, 6f, side * 48f, -2f, side * 31f, 21f)
        kantoPolygon(0xFFBB85AB, side * 22f, -11f, side * 44f, -40f - motion * 4f,
            side * 81f, -22f - motion * 3f, side * 69f, -18f,
            side * 60f, -1f, side * 45f, -7f, side * 32f, 10f)
        kantoLine(0xFFA79CC3, 4f, side * 43f, -41f - motion * 4f, side * 48f, -2f)
        kantoPolygon(0xFFA79CC3, side * 10f, 12f, side * 27f, 22f,
            side * 19f, 37f, side * 29f, 44f, side * 7f, 44f, side * 4f, 27f)
        kantoPolygon(0xFFFFF2D9, side * 16f, 39f, side * 31f, 47f, side * 10f, 46f)
    }
    lateTube(0xFFA79CC3, 7f) {
        moveTo(3f, 23f); quadraticTo(35f, 53f, 59f, 34f + motion * 2f)
    }
    kantoPolygon(0xFFA79CC3, 54f, 36f + motion * 2f, 69f, 23f + motion * 2f,
        72f, 44f + motion * 2f)
    kantoOval(0xFFA79CC3, -20f, -21f, 40f, 56f)
    kantoOval(0xFFD0BED4, -13f, -5f, 26f, 32f)
    kantoPolygon(0xFFA79CC3, -19f, -25f, -18f, -50f, -4f, -44f,
        9f, -60f, 19f, -40f, 25f, -29f, 10f, -18f)
    kantoPolygon(0xFFA79CC3, -16f, -39f, -45f, -31f, -47f, -21f,
        -15f, -17f, 5f, -25f)
    kantoLine(KANTO_INK, 2f, -42f, -23f, -18f, -24f)
    kantoPolygon(0xFFFFF7E3, -33f, -25f, -25f, -25f, -29f, -18f)
    lateEye(-3f, -37f, 13f, 11f)
    kantoLine(KANTO_INK, 2.5f, -12f, -43f, 4f, -40f)
}

private fun DrawScope.lateSnorlax(motion: Float) {
    for (side in listOf(-1f, 1f)) {
        kantoOval(0xFF568C9A, side * 48f - 14f, -7f - motion * 2f, 28f, 43f)
    }
    kantoOval(0xFF568C9A, -53f, -24f, 106f, 74f)
    kantoOval(0xFFF1E2BE, -40f, -14f, 80f, 61f)
    kantoPolygon(0xFF568C9A, -38f, -28f, -35f, -63f, -18f, -51f,
        18f, -51f, 36f, -63f, 39f, -27f, 23f, -12f, -25f, -12f)
    lateShape(0xFFF1E2BE) {
        moveTo(-31f, -29f)
        quadraticTo(-34f, -49f, -17f, -46f)
        lineTo(0f, -34f); lineTo(18f, -46f)
        quadraticTo(35f, -48f, 31f, -28f)
        quadraticTo(0f, -10f, -31f, -29f)
        close()
    }
    kantoLine(KANTO_INK, 2.5f, -24f, -32f, -12f, -32f)
    kantoLine(KANTO_INK, 2.5f, 12f, -32f, 24f, -32f)
    kantoLine(KANTO_INK, 2f, -13f, -23f, 13f, -23f)
    kantoPolygon(0xFFFFF9DF, -14f, -23f, -9f, -23f, -12f, -29f)
    kantoPolygon(0xFFFFF9DF, 9f, -23f, 14f, -23f, 12f, -29f)
    for (side in listOf(-1f, 1f)) {
        val x = side * 37f
        kantoOval(0xFFDFCEAB, x - 19f, 27f, 38f, 24f)
        kantoOval(0xFFAE977B, x - 9f, 35f, 18f, 12f)
        repeat(3) { n ->
            val toe = x - 12f + n * 12f
            kantoPolygon(0xFFFFF9DF, toe - 4f, 32f, toe, 23f, toe + 4f, 32f)
        }
    }
}

private fun DrawScope.lateLegendaryBird(ndex: Int, motion: Float) {
    val body = when (ndex) {
        144 -> 0xFF86C8E5
        145 -> 0xFFF6D466
        else -> 0xFFF4CB73
    }
    if (ndex == 144) {
        lateTube(0xFF6DAFD7, 11f) {
            moveTo(4f, 19f)
            cubicTo(61f, 19f, 77f, 53f, 14f, 46f)
            quadraticTo(-19f, 40f, -46f, 47f)
        }
        lateTube(0xFF99D5E9, 6f) {
            moveTo(-2f, 20f)
            cubicTo(29f, 29f, 46f, 49f, 0f, 43f)
            quadraticTo(-20f, 39f, -40f, 46f)
        }
    } else if (ndex == 145) {
        kantoPolygon(KANTO_INK, -12f, 14f, -40f, 47f, -16f, 36f,
            -12f, 50f, 5f, 32f, 18f, 49f, 19f, 28f, 42f, 41f, 17f, 13f)
        kantoPolygon(body, -7f, 13f, -24f, 39f, -6f, 31f, 0f, 43f,
            10f, 28f, 29f, 35f, 11f, 11f)
    } else {
        lateFlame(20f, 30f, 0.78f, motion)
        lateFlame(-13f, 29f, 0.65f, -motion)
    }
    for (side in listOf(-1f, 1f)) {
        val lift = motion * 4f
        when (ndex) {
            144 -> lateShape(body) {
                moveTo(side * 12f, -15f)
                quadraticTo(side * 48f, -52f, side * 86f, -58f + lift)
                lineTo(side * 80f, -35f + lift)
                lineTo(side * 89f, -43f + lift)
                quadraticTo(side * 84f, -17f, side * 63f, -6f)
                lineTo(side * 70f, -7f)
                quadraticTo(side * 53f, 14f, side * 18f, 12f)
                close()
            }
            145 -> {
                kantoPolygon(KANTO_INK, side * 13f, -19f, side * 74f, -59f + lift,
                    side * 63f, -33f, side * 92f, -39f + lift, side * 76f, -12f,
                    side * 88f, -13f, side * 66f, 11f, side * 46f, 4f, side * 18f, 21f)
                kantoPolygon(body, side * 15f, -22f, side * 68f, -53f + lift,
                    side * 51f, -25f, side * 84f, -33f + lift, side * 64f, -8f,
                    side * 73f, -10f, side * 51f, 7f, side * 35f, -1f, side * 15f, 13f)
            }
            else -> {
                lateShape(0xFFEF965B) {
                    moveTo(side * 13f, -15f)
                    quadraticTo(side * 48f, -52f, side * 78f, -44f + lift)
                    quadraticTo(side * 86f, -20f, side * 65f, -5f)
                    quadraticTo(side * 49f, 9f, side * 19f, 13f)
                    close()
                }
                for (n in 0..2) {
                    lateFlame(side * (44f + n * 17f), -24f - n * 7f + lift,
                        0.47f, motion * side)
                }
                kantoPolygon(body, side * 16f, -13f, side * 65f, -35f + lift,
                    side * 52f, -6f, side * 20f, 12f)
            }
        }
        if (ndex == 144) {
            kantoLine(0xFF4F96C2, 2f, side * 28f, -3f, side * 68f, -34f + lift)
            kantoLine(0xFF4F96C2, 2f, side * 35f, 4f, side * 75f, -21f + lift)
        }
        kantoLine(0xFFBA9B72, 4f, side * 11f, 21f, side * 16f, 35f)
        kantoLine(0xFFBA9B72, 3f, side * 7f, 39f, side * 16f, 35f, side * 25f, 40f)
    }
    kantoOval(body, -23f, -23f, 46f, 53f)
    if (ndex == 144) {
        kantoPolygon(0xFFD0E6EE, -18f, -8f, -8f, -17f, 0f, -8f, 10f, -17f,
            19f, -7f, 12f, 9f, 0f, 17f, -14f, 9f)
        kantoPolygon(0xFF6AADD6, -14f, -43f, -22f, -65f, -7f, -55f,
            0f, -68f, 8f, -55f, 20f, -63f, 14f, -40f)
    } else if (ndex == 145) {
        kantoPolygon(body, -19f, -29f, -28f, -53f, -12f, -47f, -8f, -65f,
            4f, -49f, 17f, -61f, 19f, -41f, 33f, -37f, 18f, -21f)
    } else {
        lateFlame(2f, -45f, 0.62f, motion)
    }
    kantoOval(body, -18f, -46f, 36f, 34f)
    kantoPolygon(0xFFE5B466, -5f, -26f, 0f, -6f, 10f, -24f)
    kantoEyes(0f, -32f, 10f, 3f)
    if (ndex == 145) {
        kantoLine(KANTO_INK, 3f, -17f, -39f, -5f, -34f)
        kantoLine(KANTO_INK, 3f, 17f, -39f, 5f, -34f)
    }
}

private fun DrawScope.lateDragonSerpent(evolved: Boolean, motion: Float) {
    val blue = if (evolved) 0xFF74ACD8 else 0xFF9BBBE1
    lateTube(blue, if (evolved) 19f else 24f) {
        moveTo(-24f, -21f)
        cubicTo(-55f, 17f, -28f, 41f, 6f, 29f)
        cubicTo(43f, 12f, 51f, 49f, 70f, 28f)
        quadraticTo(84f, 12f, 70f, 2f + motion * 4f)
    }
    lateTube(0xFFF4EEDC, if (evolved) 8f else 11f) {
        moveTo(-26f, -7f)
        cubicTo(-44f, 19f, -23f, 35f, 7f, 26f)
        quadraticTo(26f, 21f, 37f, 31f)
    }
    kantoOval(blue, -51f, -52f, 48f, 44f)
    if (evolved) {
        kantoPolygon(0xFFFFF7DE, -31f, -48f, -22f, -68f, -17f, -46f)
        for (side in listOf(-1f, 1f)) {
            val x = -27f + side * 23f
            kantoPolygon(0xFFF5F4E9, x, -35f, x + side * 9f, -56f,
                x + side * 16f, -51f, x + side * 12f, -43f,
                x + side * 21f, -43f, x + side * 17f, -35f,
                x + side * 9f, -24f)
        }
        kantoOval(0xFF6396CD, -41f, -12f, 18f, 20f)
        kantoOval(0xFF80BBE5, -37f, -9f, 7f, 8f)
        kantoOval(0xFF6396CD, 60f, 23f, 16f, 17f)
        kantoOval(0xFF6396CD, 68f, 8f, 13f, 14f)
    } else {
        kantoOval(0xFFF2EEE0, -60f, -40f, 16f, 23f)
        kantoOval(0xFFF2EEE0, -12f, -40f, 16f, 23f)
        kantoOval(0xFFB7C6DD, -56f, -35f, 7f, 12f)
        kantoOval(0xFFB7C6DD, -8f, -35f, 7f, 12f)
    }
    kantoOval(0xFFF5EDE0, -43f, -24f, 29f, 18f)
    lateEye(-36f, -35f, 11f, 17f)
    lateEye(-17f, -35f, 11f, 17f)
    kantoSmile(-28f, -15f, 7f)
}

private fun DrawScope.lateDragonite(motion: Float) {
    val orange = 0xFFE9B474
    for (side in listOf(-1f, 1f)) {
        kantoPolygon(orange, side * 17f, -17f, side * 45f, -50f - motion * 3f,
            side * 64f, -39f, side * 66f, -4f, side * 44f, -15f, side * 28f, 4f)
        kantoPolygon(0xFF6FAFAC, side * 27f, -15f, side * 45f, -41f - motion * 3f,
            side * 56f, -34f, side * 58f, -13f, side * 43f, -22f)
    }
    lateTube(orange, 17f) {
        moveTo(23f, 27f); quadraticTo(59f, 44f, 78f, 18f + motion * 3f)
    }
    kantoOval(orange, -35f, -12f, 70f, 61f)
    kantoOval(0xFFF5DDA9, -23f, -6f, 46f, 51f)
    repeat(4) { n -> kantoLine(0xFFC5AD82, 2f, -19f, 6f + n * 10f, 19f, 6f + n * 10f) }
    for (side in listOf(-1f, 1f)) {
        kantoOval(orange, side * 31f - 17f, 31f, 34f, 20f)
        kantoOval(orange, side * 39f - 11f, -9f, 22f, 31f)
        repeat(3) { n ->
            val x = side * 31f - 9f + n * 9f
            kantoPolygon(0xFFFFF5D9, x - 3f, 48f, x, 42f, x + 3f, 48f)
        }
    }
    kantoOval(orange, -28f, -49f, 56f, 47f)
    kantoOval(0xFFF0C285, -28f, -23f, 56f, 25f)
    for (side in listOf(-1f, 1f)) {
        lateTube(orange, 4f) {
            moveTo(side * 15f, -46f)
            quadraticTo(side * 22f, -64f, side * 34f, -63f)
        }
    }
    lateEye(-14f, -33f, 13f, 17f)
    lateEye(14f, -33f, 13f, 17f)
    kantoEyes(0f, -18f, 8f, 2f)
    kantoSmile(0f, -10f, 16f)
}

private fun DrawScope.lateMewtwo(motion: Float) {
    lateTube(0xFFB28EC9, 12f) {
        moveTo(14f, 17f)
        cubicTo(67f, 42f, 79f, -21f, 58f, -25f)
        cubicTo(32f, -37f, 56f, 22f, 29f, 30f)
    }
    kantoOval(0xFFD8D0E3, -27f, 7f, 23f, 31f)
    kantoOval(0xFFD8D0E3, 3f, 7f, 24f, 31f)
    kantoLine(0xFFD8D0E3, 10f, -18f, 27f, -24f, 44f)
    kantoLine(0xFFD8D0E3, 10f, 15f, 29f, 25f, 43f)
    kantoOval(0xFFD8D0E3, -41f, 39f, 28f, 12f)
    kantoOval(0xFFD8D0E3, 14f, 39f, 29f, 12f)
    lateShape(0xFFD8D0E3) {
        moveTo(-21f, -25f)
        quadraticTo(0f, -33f, 21f, -24f)
        lineTo(13f, 0f); lineTo(19f, 21f)
        quadraticTo(0f, 33f, -20f, 20f)
        lineTo(-12f, 0f)
        close()
    }
    kantoOval(0xFFB28EC9, -11f, 5f, 22f, 22f)
    for (side in listOf(-1f, 1f)) {
        lateTube(0xFFD8D0E3, 8f) {
            moveTo(side * 17f, -20f)
            lineTo(side * 36f, -5f)
            lineTo(side * 48f, -14f + motion * 4f)
        }
        kantoOval(0xFFD8D0E3, side * 51f - 8f, -23f + motion * 4f, 16f, 17f)
        for ((dx, dy) in listOf(-7f to -4f, 2f to -10f, 10f to 0f)) {
            kantoOval(0xFFD8D0E3, side * (52f + dx) - 4f,
                -18f + dy + motion * 4f, 8f, 8f)
        }
    }
    lateTube(0xFFBAB0D0, 6f) {
        moveTo(18f, -22f); quadraticTo(33f, -42f, 17f, -44f)
    }
    kantoPolygon(0xFFD8D0E3, -22f, -37f, -23f, -65f, -11f, -54f,
        10f, -54f, 22f, -65f, 23f, -37f, 10f, -24f, -7f, -24f)
    kantoOval(0xFFD8D0E3, -18f, -40f, 38f, 21f)
    kantoPolygon(0xFFFFFBEE, -20f, -42f, -5f, -36f, -14f, -32f)
    kantoPolygon(0xFFFFFBEE, 20f, -42f, 5f, -36f, 14f, -32f)
    kantoLine(0xFF80599F, 3f, -13f, -38f, -13f, -34f)
    kantoLine(0xFF80599F, 3f, 13f, -38f, 13f, -34f)
    kantoLine(KANTO_INK, 2f, -5f, -27f, 6f, -27f)
}

private fun DrawScope.lateMew(motion: Float) {
    lateTube(0xFFE9B4CC, 5f) {
        moveTo(14f, 21f)
        cubicTo(60f, 54f, 86f, 18f, 65f, -8f)
        cubicTo(49f, -33f, 75f, -49f - motion * 4f, 80f, -32f - motion * 4f)
    }
    kantoOval(0xFFEAB7CD, -19f, -8f, 46f, 47f)
    kantoOval(0xFFF4CBD9, -10f, 4f, 30f, 27f)
    kantoOval(0xFFEAB7CD, -25f, 22f, 23f, 22f)
    kantoOval(0xFFEAB7CD, 13f, 20f, 23f, 22f)
    kantoOval(0xFFEAB7CD, -34f, 35f, 30f, 12f)
    kantoOval(0xFFEAB7CD, 15f, 32f, 30f, 12f)
    kantoLine(0xFFEAB7CD, 9f, -15f, 0f, -30f, 14f)
    kantoLine(0xFFEAB7CD, 9f, 18f, 1f, 35f, -9f + motion * 3f)
    kantoOval(0xFFEAB7CD, -38f, 8f, 15f, 13f)
    kantoOval(0xFFEAB7CD, 29f, -17f + motion * 3f, 15f, 13f)
    kantoPolygon(0xFFEAB7CD, -29f, -31f, -34f, -60f, -11f, -45f,
        7f, -44f, 29f, -59f, 24f, -28f)
    kantoPolygon(0xFFD18EAF, -27f, -49f, -26f, -34f, -13f, -40f)
    kantoPolygon(0xFFD18EAF, 22f, -48f, 20f, -33f, 9f, -40f)
    kantoOval(0xFFEAB7CD, -36f, -46f, 66f, 44f)
    kantoOval(0xFFFFFCF2, -26f, -30f, 16f, 21f)
    kantoOval(0xFFFFFCF2, 4f, -30f, 16f, 21f)
    kantoOval(0xFF609BC0, -20f, -28f, 8f, 17f)
    kantoOval(0xFF609BC0, 6f, -28f, 8f, 17f)
    kantoOval(0xFFFFFEF4, -18f, -27f, 3f, 5f)
    kantoOval(0xFFFFFEF4, 7f, -27f, 3f, 5f)
    kantoSmile(-3f, -9f, 6f)
}
