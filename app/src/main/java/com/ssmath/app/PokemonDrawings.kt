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
import kotlin.math.cos
import kotlin.math.sin

private val pokemonInk = Color(0xFF30324D)
private val seaFoam = Color(0xFFE8FAFF)
private val heartPink = Color(0xFFF36EAB)
private val fullTurn = 2f * PI.toFloat()

internal fun DrawScope.drawPalafinCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFB6EAF4))
    val beat = progress * fullTurn
    val lift = sin(beat * 2f)
    val x = 158f + sin(beat) * 24f
    val y = 108f - lift * 16f
    drawWaterRing(x, 192f, 47f + lift * 8f)
    withTransform({
        translate(x, y)
        rotate(-17f + sin(beat) * 12f, pivot = Offset.Zero)
    }) {
        val body = Color(0xFF53B9E1)
        val fin = Color(0xFF377EC3)
        rotate(sin(beat * 3f) * 18f, pivot = Offset(0f, 48f)) {
            pokemonPath(Path().apply {
                moveTo(-9f, 43f); quadraticTo(-11f, 60f, -31f, 61f)
                quadraticTo(-22f, 81f, 0f, 66f)
                quadraticTo(22f, 80f, 33f, 57f)
                quadraticTo(12f, 60f, 9f, 42f); close()
            }, fin)
        }
        pokemonPath(Path().apply {
            moveTo(-23f, -13f); quadraticTo(-51f, -28f, -38f, -47f)
            quadraticTo(-32f, -31f, -13f, -33f); close()
        }, fin)
        for (side in listOf(-1f, 1f)) {
            withTransform({ scale(side, 1f, pivot = Offset.Zero) }) {
                rotate(-18f + sin(beat * 3f) * 22f, pivot = Offset(18f, 6f)) {
                    pokemonPath(Path().apply {
                        moveTo(17f, -2f); quadraticTo(39f, -1f, 52f, -22f)
                        quadraticTo(58f, 12f, 23f, 24f); close()
                    }, fin)
                }
            }
        }
        pokemonPath(Path().apply {
            moveTo(-22f, -37f); cubicTo(-45f, -13f, -25f, 35f, -8f, 53f)
            quadraticTo(0f, 59f, 10f, 51f)
            cubicTo(25f, 30f, 36f, -4f, 23f, -30f); close()
        }, body)
        drawOval(seaFoam, Offset(-17f, -15f), Size(35f, 57f))
        pokemonOval(body, -31f, -64f, 64f, 55f)
        pokemonPath(Path().apply {
            moveTo(20f, -40f); quadraticTo(55f, -46f, 54f, -34f)
            quadraticTo(45f, -21f, 22f, -24f)
        }, body)
        drawPath(Path().apply {
            moveTo(29f, -31f); quadraticTo(42f, -27f, 51f, -34f)
        }, pokemonInk, style = Stroke(2f, cap = StrokeCap.Round))
        pokemonEye(15f, -45f, 6.5f, Color(0xFFB34F7E))
        drawArc(Color.White.copy(alpha = 0.8f), 215f, 65f, false,
            Offset(-24f, -58f), Size(37f, 29f), style = Stroke(3f, cap = StrokeCap.Round))
        drawHeart(Offset(1f, 13f), 13f, heartPink)
    }
    repeat(7) { index ->
        val t = (progress * 2f + index / 7f) % 1f
        val side = if (index % 2 == 0) -1f else 1f
        drawHeart(Offset(x + side * (49f + t * 55f), 142f - t * 111f),
            3f + (1f - t) * 4f, heartPink.copy(alpha = 1f - t * 0.7f))
    }
}

internal fun DrawScope.drawFinizenCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFABE5ED))
    val beat = progress * fullTurn
    val x = 161f + sin(beat) * 35f
    val y = 115f + sin(beat * 2f) * 19f
    repeat(3) { index ->
        val t = (progress * 1.7f + index / 3f) % 1f
        val center = Offset(258f - t * 202f, 111f + sin(t * fullTurn) * 21f)
        drawCircle(Color.White.copy(alpha = 0.65f), 20f + t * 14f, center, style = Stroke(3f))
        drawCircle(Color(0xFF51BACC).copy(alpha = 0.35f), 25f + t * 14f, center, style = Stroke(1.5f))
    }
    withTransform({
        translate(x, y)
        rotate(cos(beat * 2f) * 13f, pivot = Offset.Zero)
    }) {
        val body = Color(0xFF77CDEB)
        val fin = Color(0xFF467EBA)
        rotate(sin(beat * 3f) * 23f, pivot = Offset(-51f, 5f)) {
            pokemonPath(Path().apply {
                moveTo(-48f, -4f); quadraticTo(-78f, -7f, -90f, -29f)
                quadraticTo(-102f, -6f, -78f, 8f)
                quadraticTo(-99f, 15f, -88f, 37f)
                quadraticTo(-75f, 18f, -49f, 13f); close()
            }, fin)
        }
        pokemonPath(Path().apply {
            moveTo(-27f, -20f); quadraticTo(-22f, -40f, -7f, -49f)
            quadraticTo(-12f, -29f, 13f, -23f); close()
        }, fin)
        pokemonPath(Path().apply {
            moveTo(-62f, 5f); cubicTo(-35f, -20f, -10f, -34f, 26f, -31f)
            cubicTo(49f, -32f, 60f, -21f, 63f, -7f)
            quadraticTo(92f, -8f, 94f, 1f)
            quadraticTo(92f, 12f, 63f, 13f)
            cubicTo(27f, 40f, -9f, 31f, -62f, 5f); close()
        }, body)
        drawPath(Path().apply {
            moveTo(-52f, 8f); quadraticTo(9f, 16f, 57f, 10f)
            cubicTo(21f, 36f, -9f, 24f, -52f, 8f); close()
        }, seaFoam)
        rotate(-sin(beat * 3f) * 18f, pivot = Offset(5f, 15f)) {
            pokemonPath(Path().apply {
                moveTo(14f, 9f); quadraticTo(13f, 37f, -13f, 46f)
                quadraticTo(-8f, 23f, -2f, 13f); close()
            }, fin)
        }
        pokemonEye(45f, -12f, 6.5f, Color(0xFFAE618C))
        drawPath(Path().apply {
            moveTo(61f, 5f); quadraticTo(75f, 9f, 87f, 3f)
        }, pokemonInk, style = Stroke(2f, cap = StrokeCap.Round))
        drawArc(Color.White.copy(alpha = 0.8f), 230f, 60f, false,
            Offset(0f, -27f), Size(46f, 24f), style = Stroke(3f, cap = StrokeCap.Round))
    }
}

internal fun DrawScope.drawWailmerCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFB6DFF7))
    val beat = progress * fullTurn
    val bounce = abs(sin(beat * 2f))
    val x = 160f + sin(beat) * 16f
    val y = 133f - bounce * 25f
    drawWaterRing(x, 190f, 68f - bounce * 12f)
    withTransform({ translate(x, y) }) {
        val body = Color(0xFF609CE6)
        for (side in listOf(-1f, 1f)) {
            withTransform({ scale(side, 1f, pivot = Offset.Zero) }) {
                rotate(12f + sin(beat * 3f) * 22f, pivot = Offset(45f, 3f)) {
                    pokemonPath(Path().apply {
                        moveTo(44f, -10f); quadraticTo(77f, -8f, 85f, 20f)
                        quadraticTo(62f, 20f, 44f, 9f); close()
                    }, Color(0xFF3D77BF))
                }
            }
        }
        withTransform({ scale(1f + bounce * 0.04f, 1f - bounce * 0.04f, pivot = Offset.Zero) }) {
            pokemonOval(body, -59f, -57f, 118f, 114f)
            drawPath(Path().apply {
                moveTo(-55f, 8f); quadraticTo(0f, 28f, 55f, 8f)
                cubicTo(45f, 74f, -45f, 74f, -55f, 8f); close()
            }, Color(0xFFFFE6BE))
            repeat(5) { index ->
                val lineX = -36f + index * 18f
                drawPath(Path().apply {
                    moveTo(lineX, 27f)
                    quadraticTo(lineX * 1.03f, 40f, lineX * 0.8f, 48f)
                }, Color(0xFFCCAA83), style = Stroke(1.5f, cap = StrokeCap.Round))
            }
            pokemonEye(-37f, -6f, 4.7f)
            pokemonEye(37f, -6f, 4.7f)
            drawArc(pokemonInk, 0f, 180f, false, Offset(-31f, 2f), Size(62f, 20f),
                style = Stroke(2.5f, cap = StrokeCap.Round))
            drawOval(Color(0xFF365B98), Offset(-9f, -49f), Size(18f, 6f))
            drawArc(Color.White.copy(alpha = 0.55f), 206f, 51f, false,
                Offset(-48f, -48f), Size(78f, 65f), style = Stroke(4f, cap = StrokeCap.Round))
        }
    }
    drawSpout(Offset(x, y - 49f), progress, 44f)
}

internal fun DrawScope.drawWailordCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFB8DDF2))
    val beat = progress * fullTurn
    val x = 174f + sin(beat) * 13f
    val y = 129f + sin(beat * 2f) * 12f
    drawWaterRing(x, 185f, 114f + sin(beat * 2f) * 8f)
    withTransform({
        translate(x, y)
        rotate(sin(beat) * 3f, pivot = Offset.Zero)
    }) {
        val body = Color(0xFF4D89CB)
        rotate(sin(beat * 3f) * 15f, pivot = Offset(-91f, -1f)) {
            pokemonPath(Path().apply {
                moveTo(-84f, -12f); quadraticTo(-114f, -11f, -131f, -37f)
                quadraticTo(-149f, -18f, -129f, -1f)
                quadraticTo(-145f, 14f, -125f, 32f)
                quadraticTo(-113f, 12f, -85f, 13f); close()
            }, Color(0xFF376BA8))
        }
        pokemonPath(Path().apply {
            moveTo(-102f, -7f)
            cubicTo(-73f, -41f, 66f, -53f, 102f, -27f)
            cubicTo(124f, -8f, 116f, 32f, 94f, 39f)
            cubicTo(45f, 57f, -74f, 33f, -102f, 7f); close()
        }, body)
        drawPath(Path().apply {
            moveTo(-85f, 8f); quadraticTo(9f, 34f, 112f, 6f)
            quadraticTo(115f, 31f, 93f, 37f)
            cubicTo(44f, 53f, -56f, 34f, -85f, 8f); close()
        }, Color(0xFFFFEED1))
        repeat(4) { index ->
            val yLine = 19f + index * 6f
            drawPath(Path().apply {
                moveTo(-47f + index * 12f, yLine)
                quadraticTo(25f, yLine + 17f, 101f - index * 3f, yLine - 3f)
            }, Color(0xFFBEA782), style = Stroke(1.5f))
        }
        rotate(sin(beat * 3f) * 12f, pivot = Offset(24f, 20f)) {
            pokemonPath(Path().apply {
                moveTo(15f, 16f); quadraticTo(5f, 48f, 42f, 52f)
                quadraticTo(38f, 27f, 35f, 18f); close()
            }, Color(0xFF376BA8))
        }
        pokemonEye(90f, -8f, 4.2f)
        drawPath(Path().apply {
            moveTo(94f, 16f); quadraticTo(105f, 14f, 111f, 9f)
        }, pokemonInk, style = Stroke(2f, cap = StrokeCap.Round))
        for (holeX in listOf(49f, 58f)) {
            drawOval(Color(0xFF2D5894), Offset(holeX, -39f), Size(6f, 3f))
        }
        drawPath(Path().apply {
            moveTo(-65f, -16f); quadraticTo(1f, -41f, 69f, -31f)
        }, Color.White.copy(alpha = 0.5f), style = Stroke(4f, cap = StrokeCap.Round))
        drawSpout(Offset(56f, -40f), progress, 42f)
    }
}

internal fun DrawScope.drawBouffalantCelebration(progress: Float) {
    val beat = progress * fullTurn
    drawRect(Brush.verticalGradient(listOf(Color(0xFFFFE9BD), Color(0xFFE4EBC2)), 0f, 220f),
        size = Size(320f, 220f))
    drawOval(Color(0xFFC6D994), Offset(-25f, 158f), Size(370f, 90f))
    drawOval(Color(0xFFE8C994), Offset(14f, 184f), Size(290f, 41f))
    val x = 166f + sin(beat) * 20f
    val hop = abs(sin(beat * 3f))
    val y = 135f - hop * 12f
    drawOval(pokemonInk.copy(alpha = 0.12f), Offset(x - 83f + hop * 10f, 192f),
        Size(160f - hop * 20f, 12f))
    repeat(9) { index ->
        val t = (progress * 3f + index / 9f) % 1f
        drawCircle(Color(0xFFF7E5C5).copy(alpha = 1f - t), 3f + t * 9f,
            Offset(x - 65f + index * 15f + t * 18f, 195f - t * 24f))
    }
    withTransform({ translate(x, y) }) {
        val body = Color(0xFFAA754B)
        val darkBrown = Color(0xFF50372F)
        val tail = Path().apply {
            moveTo(57f, -8f)
            quadraticTo(89f, -10f + sin(beat * 3f) * 17f, 91f, -40f + sin(beat * 3f) * 10f)
        }
        drawPath(tail, pokemonInk, style = Stroke(7f, cap = StrokeCap.Round))
        drawPath(tail, body, style = Stroke(4f, cap = StrokeCap.Round))
        pokemonOval(darkBrown, 82f, -53f + sin(beat * 3f) * 10f, 19f, 25f)
        for (index in 0..3) {
            val legX = if (index % 2 == 0) -26f else 42f
            val near = index >= 2
            val stride = sin(beat * 3f + if (index % 3 == 0) 0f else PI.toFloat())
            rotate(stride * 18f, pivot = Offset(legX, 12f)) {
                pokemonOval(if (near) body else Color(0xFF80543B),
                    legX - 9f + if (near) 6f else -5f, 9f, 19f, 44f)
                pokemonOval(darkBrown, legX - 10f + if (near) 6f else -5f, 42f, 23f, 14f)
            }
        }
        pokemonOval(body, -48f, -38f, 124f, 78f)
        drawArc(Color(0xFFC7915F), 210f, 67f, false, Offset(-5f, -28f), Size(66f, 50f),
            style = Stroke(4f, cap = StrokeCap.Round))
        withTransform({
            translate(-38f, -16f)
            rotate(sin(beat * 3f) * 5f, pivot = Offset.Zero)
        }) {
            pokemonOval(darkBrown, -34f, -30f, 72f, 71f)
            pokemonOval(Color(0xFF674732), -24f, -8f, 51f, 55f)
            pokemonOval(darkBrown, -47f, -46f, 95f, 72f)
            repeat(10) { index ->
                val angle = index * fullTurn / 10f
                pokemonOval(darkBrown, cos(angle) * 33f - 15f, -13f + sin(angle) * 28f - 15f,
                    30f, 30f)
            }
            drawOval(darkBrown, Offset(-34f, -41f), Size(68f, 63f))
            drawOval(Color(0xFF69493A), Offset(-23f, -41f), Size(31f, 17f))
            for (side in listOf(-1f, 1f)) {
                withTransform({ scale(side, 1f, pivot = Offset.Zero) }) {
                    pokemonPath(Path().apply {
                        moveTo(36f, -10f); cubicTo(62f, -4f, 78f, -16f, 69f, -43f)
                        cubicTo(69f, -24f, 56f, -21f, 38f, -25f); close()
                    }, Color(0xFFFFF2CF))
                    drawLine(Color(0xFFE3B548), Offset(44f, -22f), Offset(43f, -10f),
                        7f, StrokeCap.Round)
                    drawLine(pokemonInk, Offset(37f, -26f), Offset(36f, -10f), 2f)
                }
            }
            pokemonEye(-13f, 13f, 4.4f, Color(0xFFC98E37))
            pokemonEye(14f, 13f, 4.4f, Color(0xFFC98E37))
            pokemonOval(Color(0xFFC29976), -23f, 22f, 49f, 29f)
            drawOval(darkBrown, Offset(-13f, 30f), Size(6f, 4f))
            drawOval(darkBrown, Offset(11f, 30f), Size(6f, 4f))
            pokemonSmile(1f, 39f, 16f)
        }
    }
    repeat(5) { index ->
        val t = (progress * 2f + index / 5f) % 1f
        val center = Offset(31f + index * 62f, 81f - t * 42f)
        val radius = 3f + sin(t * PI.toFloat()) * 3f
        drawLine(Color(0xFFE0A347), center - Offset(radius, 0f), center + Offset(radius, 0f), 2f)
        drawLine(Color(0xFFE0A347), center - Offset(0f, radius), center + Offset(0f, radius), 2f)
    }
}

internal fun DrawScope.drawVeluzaCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFB9EAD8))
    val beat = progress * fullTurn
    repeat(12) { index ->
        val t = (progress * 4f + index / 12f) % 1f
        val x = 340f - t * 380f
        val y = 39f + index * 13f
        drawLine(Color.White.copy(alpha = 0.65f), Offset(x, y), Offset(x + 14f + index % 3 * 9f, y),
            2f, StrokeCap.Round)
    }
    withTransform({
        translate(164f + sin(beat) * 35f, 117f + sin(beat * 2f) * 15f)
        rotate(cos(beat) * 8f, pivot = Offset.Zero)
    }) {
        val silver = Color(0xFFC6D3DE)
        val fin = Color(0xFF88D948)
        val finShade = Color(0xFF4D9A3D)
        rotate(sin(beat * 4f) * 20f, pivot = Offset(-56f, 0f)) {
            pokemonPath(Path().apply {
                moveTo(-52f, 0f); lineTo(-91f, -33f); lineTo(-83f, -5f)
                lineTo(-96f, 30f); lineTo(-59f, 11f); close()
            }, fin)
            drawLine(finShade, Offset(-56f, 3f), Offset(-85f, -16f), 2f)
            drawLine(finShade, Offset(-56f, 3f), Offset(-85f, 21f), 2f)
        }
        val flare = sin(beat * 3f) * 5f
        pokemonPath(Path().apply {
            moveTo(-28f, -16f); lineTo(-21f, -55f - flare); lineTo(28f, -24f)
            lineTo(10f, -14f); close()
            moveTo(-22f, 17f); lineTo(-24f, 47f + flare); lineTo(16f, 23f); close()
        }, fin)
        pokemonPath(Path().apply {
            moveTo(-66f, 0f); lineTo(-38f, -23f); lineTo(30f, -29f)
            lineTo(85f, -2f); lineTo(46f, 23f); lineTo(-36f, 25f); close()
        }, silver)
        drawPath(Path().apply {
            moveTo(-62f, 0f); lineTo(-22f, -15f); lineTo(32f, -20f)
            lineTo(76f, -3f); lineTo(22f, -7f); close()
        }, Color(0xFFF4FAFC))
        drawPath(Path().apply {
            moveTo(-58f, 5f); lineTo(31f, 9f); lineTo(74f, 2f)
            lineTo(44f, 21f); lineTo(-35f, 23f); close()
        }, Color(0xFF889BB3))
        drawPath(Path().apply {
            moveTo(-42f, 0f); lineTo(-18f, -8f); lineTo(-27f, 3f)
            lineTo(5f, -1f); lineTo(-5f, 9f); lineTo(-35f, 11f); close()
        }, fin)
        pokemonPath(Path().apply {
            moveTo(13f, 6f); lineTo(4f, 39f + flare); lineTo(39f, 15f); close()
        }, fin)
        drawPath(Path().apply {
            moveTo(20f, -17f); lineTo(11f, -6f); lineTo(22f, 3f)
        }, pokemonInk, style = Stroke(2f))
        pokemonEye(45f, -8f, 5f, Color(0xFFC65780))
        drawLine(pokemonInk, Offset(64f, 7f), Offset(79f, 0f), 2f, StrokeCap.Round)
    }
}

internal fun DrawScope.drawMantykeCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFBFEAF4))
    val beat = progress * fullTurn
    val flap = sin(beat * 3f)
    withTransform({
        translate(160f + sin(beat) * 29f, 121f + cos(beat * 2f) * 13f)
        rotate(sin(beat) * 11f, pivot = Offset.Zero)
    }) {
        val body = Color(0xFF4ABAE0)
        val tail = Path().apply {
            moveTo(0f, 25f); quadraticTo(28f, 67f, 44f + flap * 12f, 44f)
        }
        drawPath(tail, pokemonInk, style = Stroke(7f, cap = StrokeCap.Round))
        drawPath(tail, body, style = Stroke(4f, cap = StrokeCap.Round))
        for (side in listOf(-1f, 1f)) {
            withTransform({ scale(side, 1f, pivot = Offset.Zero) }) {
                pokemonPath(Path().apply {
                    moveTo(23f, -21f); quadraticTo(44f, -24f, 68f, -21f - flap * 15f)
                    quadraticTo(59f, 18f - flap * 8f, 26f, 25f); close()
                }, body)
                drawPath(Path().apply {
                    moveTo(29f, 7f); quadraticTo(46f, 8f, 59f, -9f - flap * 10f)
                    quadraticTo(50f, 17f - flap * 5f, 26f, 25f); close()
                }, seaFoam)
                val antenna = Path().apply {
                    moveTo(18f, -28f)
                    cubicTo(21f, -49f, 38f, -48f - flap * 5f, 30f, -65f - flap * 5f)
                }
                drawPath(antenna, pokemonInk, style = Stroke(10f, cap = StrokeCap.Round))
                drawPath(antenna, body, style = Stroke(6f, cap = StrokeCap.Round))
            }
        }
        pokemonOval(body, -34f, -37f, 68f, 76f)
        drawOval(seaFoam, Offset(-28f, -7f), Size(56f, 42f))
        pokemonEye(-16f, -7f, 6.5f)
        pokemonEye(16f, -7f, 6.5f)
        drawCircle(Color(0xFFEF928D), 5f, Offset(-25f, 9f))
        drawCircle(Color(0xFFEF928D), 5f, Offset(25f, 9f))
        pokemonSmile(0f, 13f, 17f)
        drawArc(Color.White.copy(alpha = 0.75f), 218f, 73f, false,
            Offset(-23f, -29f), Size(33f, 20f), style = Stroke(3f, cap = StrokeCap.Round))
    }
}

internal fun DrawScope.drawMantineCelebration(progress: Float) {
    drawOcean(progress, Color(0xFFAED5EB))
    val beat = progress * fullTurn
    val flap = sin(beat * 2f)
    withTransform({
        translate(157f + sin(beat) * 15f, 110f + cos(beat * 2f) * 12f)
        rotate(sin(beat) * 7f, pivot = Offset.Zero)
    }) {
        val body = Color(0xFF457BAC)
        val tail = Path().apply {
            moveTo(0f, 27f); cubicTo(5f, 79f, 48f, 90f, 67f + flap * 11f, 65f)
        }
        drawPath(tail, pokemonInk, style = Stroke(9f, cap = StrokeCap.Round))
        drawPath(tail, body, style = Stroke(5f, cap = StrokeCap.Round))
        for (side in listOf(-1f, 1f)) {
            withTransform({ scale(side, 1f, pivot = Offset.Zero) }) {
                pokemonPath(Path().apply {
                    moveTo(19f, -29f)
                    cubicTo(52f, -24f, 76f, -47f - flap * 17f, 118f, -48f - flap * 17f)
                    quadraticTo(97f, 13f + flap * 10f, 29f, 41f)
                    lineTo(13f, 26f); close()
                }, body)
                drawPath(Path().apply {
                    moveTo(24f, -10f)
                    quadraticTo(65f, -5f, 104f, -32f - flap * 14f)
                    quadraticTo(84f, 18f + flap * 7f, 29f, 38f)
                    lineTo(18f, 20f); close()
                }, Color(0xFFDCEEF5))
                drawPath(Path().apply {
                    moveTo(45f, 5f); quadraticTo(67f, 0f, 82f, -10f - flap * 8f)
                }, Color(0xFFA4C9DE), style = Stroke(2f))
            }
        }
        pokemonOval(body, -36f, -43f, 72f, 90f)
        drawOval(Color(0xFFDCEEF5), Offset(-29f, -22f), Size(58f, 65f))
        for (side in listOf(-1f, 1f)) {
            withTransform({ scale(side, 1f, pivot = Offset.Zero) }) {
                pokemonPath(Path().apply {
                    moveTo(9f, -28f); quadraticTo(5f, -53f, 22f, -58f)
                    quadraticTo(38f, -60f, 30f, -42f)
                    quadraticTo(20f, -48f, 24f, -24f); close()
                }, body)
                drawLine(Color(0xFFA4C9DE), Offset(19f, -39f), Offset(21f, -28f),
                    3f, StrokeCap.Round)
            }
        }
        pokemonEye(-24f, -8f, 4.5f)
        pokemonEye(24f, -8f, 4.5f)
        pokemonSmile(0f, 3f, 21f)
        repeat(3) { index ->
            for (side in listOf(-1f, 1f)) {
                drawLine(Color(0xFF7FA6BC), Offset(side * 11f, 17f + index * 6f),
                    Offset(side * 21f, 15f + index * 6f), 1.5f, StrokeCap.Round)
            }
        }
        withTransform({
            translate(65f + cos(beat * 2f) * 13f, 40f + sin(beat * 3f) * 8f)
            rotate(-10f + sin(beat * 3f) * 10f, pivot = Offset.Zero)
        }) {
            pokemonPath(Path().apply {
                moveTo(-17f, 0f); lineTo(-30f, -10f); lineTo(-27f, 8f); close()
            }, Color(0xFF92ABC1))
            pokemonOval(Color(0xFFE7CE7A), -20f, -9f, 45f, 19f)
            drawLine(Color(0xFF64829C), Offset(-10f, -6f), Offset(-7f, 5f), 3f)
            drawLine(Color(0xFF64829C), Offset(0f, -6f), Offset(3f, 5f), 3f)
            pokemonEye(14f, -1f, 2.8f)
            pokemonPath(Path().apply {
                moveTo(-3f, -8f); lineTo(3f, -18f); lineTo(10f, -7f); close()
            }, Color(0xFF92ABC1))
        }
    }
}

private fun DrawScope.drawOcean(progress: Float, bottom: Color) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFFF0FCFF), bottom), 0f, 220f),
        size = Size(320f, 220f))
    repeat(3) { index ->
        val y = 177f + index * 15f
        drawPath(Path().apply {
            moveTo(-10f, y)
            for (segment in 0..7) {
                val x = -10f + segment * 50f
                quadraticTo(x + 25f, y - 8f + sin(progress * fullTurn * 2f + index) * 5f,
                    x + 50f, y)
            }
        }, Color.White.copy(alpha = 0.38f), style = Stroke(2f))
    }
    repeat(13) { index ->
        val t = (progress * 1.3f + index / 13f) % 1f
        val x = 12f + index * 73 % 296 + sin(t * fullTurn + index) * 7f
        val center = Offset(x, 237f - t * 263f)
        val radius = 3f + index % 4 * 1.5f
        drawCircle(Color.White.copy(alpha = 0.68f), radius, center, style = Stroke(1.5f))
        drawCircle(Color.White.copy(alpha = 0.85f), 1.1f, center - Offset(radius * 0.3f, radius * 0.3f))
    }
}

private fun DrawScope.drawWaterRing(x: Float, y: Float, radius: Float) {
    drawOval(Color(0xFF5CAFCF).copy(alpha = 0.24f), Offset(x - radius, y - 5f), Size(radius * 2f, 11f))
    drawOval(Color.White.copy(alpha = 0.85f), Offset(x - radius, y - 5f), Size(radius * 2f, 11f),
        style = Stroke(2f))
}

private fun DrawScope.drawSpout(origin: Offset, progress: Float, height: Float) {
    val sway = sin(progress * fullTurn * 3f) * 5f
    for (side in listOf(-1f, 1f)) {
        val jet = Path().apply {
            moveTo(origin.x, origin.y)
            cubicTo(origin.x + sway, origin.y - height,
                origin.x + side * 20f, origin.y - height - 7f,
                origin.x + side * 28f, origin.y - height + 12f)
        }
        drawPath(jet, Color(0xFF57BDDF), style = Stroke(5f, cap = StrokeCap.Round))
        drawPath(jet, Color.White.copy(alpha = 0.8f), style = Stroke(2f, cap = StrokeCap.Round))
        repeat(6) { index ->
            val t = (progress * 3f + index / 6f) % 1f
            drawCircle(Color(0xFF57BDDF).copy(alpha = 1f - t * 0.6f), 2f + t,
                Offset(origin.x + side * (22f + t * 20f),
                    origin.y - height + 5f + t * t * 29f))
        }
    }
}

private fun DrawScope.pokemonPath(path: Path, color: Color) {
    drawPath(path, color)
    drawPath(path, pokemonInk, style = Stroke(2f))
}

private fun DrawScope.pokemonOval(color: Color, x: Float, y: Float, width: Float, height: Float) {
    drawOval(color, Offset(x, y), Size(width, height))
    drawOval(pokemonInk, Offset(x, y), Size(width, height), style = Stroke(2f))
}

private fun DrawScope.pokemonEye(x: Float, y: Float, radius: Float, iris: Color = pokemonInk) {
    drawOval(Color.White, Offset(x - radius, y - radius * 1.2f), Size(radius * 2f, radius * 2.4f))
    drawOval(iris, Offset(x - radius * 0.75f, y - radius), Size(radius * 1.5f, radius * 2f))
    drawOval(pokemonInk, Offset(x - radius * 0.4f, y - radius * 0.8f),
        Size(radius * 0.8f, radius * 1.6f))
    drawCircle(Color.White, radius * 0.3f, Offset(x - radius * 0.2f, y - radius * 0.5f))
}

private fun DrawScope.pokemonSmile(x: Float, y: Float, width: Float) {
    drawArc(pokemonInk, 0f, 180f, false, Offset(x - width / 2f, y - 3f), Size(width, 9f),
        style = Stroke(2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawHeart(center: Offset, radius: Float, color: Color) {
    withTransform({ translate(center.x, center.y) }) {
        drawPath(Path().apply {
            moveTo(0f, radius * 0.9f)
            cubicTo(-radius * 1.7f, -radius * 0.2f, -radius * 0.7f, -radius * 1.4f, 0f, -radius * 0.5f)
            cubicTo(radius * 0.7f, -radius * 1.4f, radius * 1.7f, -radius * 0.2f, 0f, radius * 0.9f)
            close()
        }, color)
    }
}
