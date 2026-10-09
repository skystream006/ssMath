package com.ssmath.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

internal fun DrawScope.drawKantoHabitat(ndex: Int, habitat: KantoHabitat, progress: Float) {
    val colors = when (habitat) {
        KantoHabitat.MEADOW -> 0xFFD7F1E6 to 0xFF97C887
        KantoHabitat.RAINFOREST -> 0xFFB6DDD2 to 0xFF518E73
        KantoHabitat.VOLCANO -> 0xFFF4CAB1 to 0xFFAC7366
        KantoHabitat.COAST -> 0xFFBBE8F0 to 0xFFF0D6A2
        KantoHabitat.POND -> 0xFFD2EAD0 to 0xFF77B6B1
        KantoHabitat.RIVER -> 0xFFD7EEDF to 0xFF6EBCDB
        KantoHabitat.REEF -> 0xFFA0DDE7 to 0xFF387FAD
        KantoHabitat.SEABED -> 0xFF84BCCD to 0xFFCCBC9C
        KantoHabitat.ICE -> 0xFFDBF3FA to 0xFF9FCEDB
        KantoHabitat.FOREST -> 0xFFC1DDAB to 0xFF699672
        KantoHabitat.CANOPY -> 0xFFD9E6B3 to 0xFF7BA578
        KantoHabitat.HIVE -> 0xFFF6E4AD to 0xFFB9BF77
        KantoHabitat.SKY -> if (ndex == 145) 0xFF687A9A to 0xFFADB9CF else 0xFFC1E0F6 to 0xFFE7EFF9
        KantoHabitat.MOUNTAIN -> 0xFFD1DEEC to 0xFF9397AA
        KantoHabitat.GRASSLAND -> 0xFFEDE8BF to 0xFFB3C77F
        KantoHabitat.DESERT -> 0xFFF8E5B9 to 0xFFD7B276
        KantoHabitat.BURROW -> 0xFFEBD8AA to 0xFFB88B64
        KantoHabitat.MOONLIT -> 0xFF414A7D to 0xFF8F93B6
        KantoHabitat.CAVE -> 0xFF48566F to 0xFF92909E
        KantoHabitat.CITY -> 0xFFD9DAED to 0xFFAFACBB
        KantoHabitat.DOJO -> 0xFFF3E0C7 to 0xFFC99B80
        KantoHabitat.POWER_PLANT -> 0xFFBACBD2 to 0xFF738E9D
        KantoHabitat.SWAMP -> 0xFFC5D6BF to 0xFF8B9691
        KantoHabitat.HAUNTED -> 0xFF494465 to 0xFF8B80A2
        KantoHabitat.RUINS -> 0xFFD9CDE1 to 0xFFAA9EBC
        KantoHabitat.DIGITAL -> 0xFF244B68 to 0xFF437D8D
    }
    val sky = Color(colors.first)
    val ground = Color(colors.second)
    drawRect(Brush.verticalGradient(listOf(sky, ground), 0f, 220f), size = Size(320f, 220f))

    // Stable species-specific geometry keeps related habitats distinct, including in still previews.
    fun feature(index: Int, range: Int): Float = ((ndex * (37 + index * 12) + index * 53) % range).toFloat()
    val drift = sin(progress * 9f + ndex) * 4f
    val horizon = 118f + feature(0, 32)
    when (habitat) {
        KantoHabitat.MEADOW, KantoHabitat.GRASSLAND -> {
            drawCircle(Color(0xFFFFF3BD), 17f, Offset(37f + feature(1, 242), 35f))
            habitatHills(ground, horizon, feature(2, 130))
            repeat(12) { i ->
                val x = 8f + feature(i + 3, 304)
                val y = 159f + feature(i + 5, 54)
                habitatGrass(x, y, 9f + feature(i, 12), drift, Color(0xFF54835D))
                if (habitat == KantoHabitat.MEADOW) {
                    drawCircle(Color(0xFFF6D0DB), 3.5f, Offset(x + drift, y - 14f))
                    drawCircle(Color(0xFFFBE5A5), 1.5f, Offset(x + drift, y - 14f))
                }
            }
        }
        KantoHabitat.FOREST, KantoHabitat.RAINFOREST, KantoHabitat.CANOPY, KantoHabitat.HIVE -> {
            habitatHills(ground, horizon, feature(2, 160))
            repeat(5) { i ->
                val x = -20f + i * 83f + feature(i, 35)
                val top = 20f + feature(i + 2, 45)
                drawRect(Color(0xFF7C7960), Offset(x, top), Size(12f + feature(i, 9), 180f - top))
                drawOval(Color(0xFF5C9269).copy(alpha = 0.7f),
                    Offset(x - 36f + drift * 0.3f, top - 42f), Size(91f, 82f))
                drawOval(Color(0xFF8AB67C).copy(alpha = 0.7f),
                    Offset(x - 26f, top - 44f), Size(67f, 48f))
            }
            if (habitat == KantoHabitat.CANOPY || habitat == KantoHabitat.HIVE) {
                drawLine(Color(0xFF86765A), Offset(0f, 56f), Offset(320f, 40f), 8f, StrokeCap.Round)
                repeat(7) { i ->
                    drawOval(Color(0xFF568A61), Offset(i * 49f + drift, 39f - i),
                        Size(23f, 11f))
                }
            }
            if (habitat == KantoHabitat.HIVE) {
                val x = if (ndex % 2 == 0) 267f else 48f
                drawOval(Color(0xFFE3BB66), Offset(x - 23f, 54f), Size(46f, 61f))
                repeat(5) { i ->
                    drawArc(Color(0xFFB99850), 0f, 180f, false,
                        Offset(x - 21f, 57f + i * 9f), Size(42f, 14f), style = Stroke(2f))
                }
                drawOval(Color(0xFF81704D), Offset(x - 7f, 89f), Size(14f, 12f))
            }
            if (habitat == KantoHabitat.RAINFOREST) {
                repeat(5) { i ->
                    val x = 13f + feature(i, 291)
                    drawPath(Path().apply {
                        moveTo(x, 0f); quadraticTo(x + 33f + drift, 79f, x - 5f, 115f)
                    }, Color(0xFF467C64), style = Stroke(3f))
                }
            }
            repeat(7) { i ->
                habitatGrass(10f + feature(i + 4, 301), 200f + feature(i, 16),
                    15f + feature(i + 1, 12), drift, Color(0xFF406E54))
            }
        }
        KantoHabitat.COAST -> {
            habitatCloud(40f + feature(0, 130) + drift, 36f, 0.8f)
            drawRect(Color(0xFF72BECD), Offset(0f, horizon - 25f), Size(320f, 100f))
            habitatWaves(ndex, progress, horizon - 20f, Color(0xFFD7F5EF))
            drawPath(Path().apply {
                moveTo(0f, 167f + feature(1, 28))
                quadraticTo(144f, 126f + feature(2, 31), 320f, 195f)
                lineTo(320f, 220f); lineTo(0f, 220f); close()
            }, ground)
            repeat(7) { i ->
                val x = 13f + feature(i + 2, 291)
                val y = 197f + feature(i, 19)
                drawArc(Color(0xFFF9EACE), 180f, 180f, true,
                    Offset(x, y), Size(11f, 8f))
            }
        }
        KantoHabitat.POND, KantoHabitat.RIVER -> {
            habitatHills(Color(0xFF93B77D), horizon - 20f, feature(1, 140))
            if (habitat == KantoHabitat.POND) {
                drawOval(ground, Offset(-32f + feature(2, 40), 114f), Size(368f, 122f))
                repeat(5) { i ->
                    val x = 10f + feature(i + 2, 286)
                    val y = 163f + feature(i + 4, 45)
                    drawArc(Color(0xFF7BA273), 30f, 310f, true,
                        Offset(x, y), Size(22f, 10f))
                }
            } else {
                drawPath(Path().apply {
                    moveTo(110f + feature(0, 90), 105f)
                    cubicTo(45f, 156f, 257f, 153f, 310f, 220f)
                    lineTo(25f, 220f)
                    cubicTo(116f, 174f, -30f, 153f, 110f + feature(0, 90), 105f); close()
                }, ground)
            }
            habitatWaves(ndex, progress, 151f, Color(0xFFCDEFE3))
            for (side in listOf(22f, 288f)) {
                repeat(4) { i ->
                    habitatGrass(side + i * 3f, 193f + i * 4f, 24f + feature(i, 15),
                        drift, Color(0xFF517D60))
                }
            }
        }
        KantoHabitat.REEF, KantoHabitat.SEABED -> {
            repeat(4) { i ->
                val x = feature(i, 320)
                drawPath(Path().apply {
                    moveTo(x, 0f); lineTo(x + 13f, 0f)
                    lineTo(x + 66f + drift, 197f); lineTo(x + 25f + drift, 197f); close()
                }, Color.White.copy(alpha = 0.08f))
            }
            habitatHills(if (habitat == KantoHabitat.REEF) Color(0xFF839FAD) else ground,
                198f, feature(1, 170))
            repeat(9) { i ->
                val x = 8f + feature(i + 1, 303)
                val y = 193f + feature(i, 24)
                if (habitat == KantoHabitat.REEF) {
                    val coral = if (i % 2 == 0) Color(0xFFC794B4) else Color(0xFFDFB88F)
                    habitatGrass(x, y, 22f + feature(i + 3, 23), drift, coral, 4f)
                } else {
                    drawOval(Color(0xFFE6D6BA), Offset(x, y), Size(13f, 7f))
                    drawArc(Color(0xFFAC9C82), 190f, 150f, false,
                        Offset(x + 2f, y + 1f), Size(9f, 5f), style = Stroke(1f))
                }
            }
            repeat(8) { i ->
                val t = (progress * 0.7f + i * 0.127f) % 1f
                drawCircle(Color(0xFFDAF6EF).copy(alpha = 0.45f), 2f + i % 3,
                    Offset(8f + feature(i, 304) + drift, 215f - t * 220f), style = Stroke(1f))
            }
        }
        KantoHabitat.ICE, KantoHabitat.MOUNTAIN, KantoHabitat.SKY -> {
            repeat(4) { i ->
                val x = -70f + i * 112f + feature(i, 49)
                val top = (if (habitat == KantoHabitat.SKY) 159f else 78f) + feature(i + 2, 35)
                val color = if (habitat == KantoHabitat.ICE) Color(0xFFACD7E3) else Color(0xFF9CAFC4)
                drawPath(Path().apply {
                    moveTo(x - 71f, 220f); lineTo(x + 24f, top); lineTo(x + 120f, 220f); close()
                }, color)
                drawPath(Path().apply {
                    moveTo(x + 24f, top); lineTo(x - 1f, top + 39f)
                    lineTo(x + 23f, top + 31f); lineTo(x + 48f, top + 39f); close()
                }, Color(0xFFF0F5F6))
            }
            repeat(4) { i ->
                habitatCloud(-15f + feature(i + 2, 300) + drift, 25f + feature(i, 56),
                    0.5f + feature(i, 5) / 10f)
            }
            if (habitat == KantoHabitat.ICE) {
                drawRect(Color(0xFFDBF0F2), Offset(0f, 200f), Size(320f, 20f))
                habitatWaves(ndex, progress, 188f, Color.White)
            }
        }
        KantoHabitat.VOLCANO -> {
            val peak = 63f + feature(0, 190)
            drawPath(Path().apply {
                moveTo(-20f, 200f); lineTo(peak - 24f, 82f); lineTo(peak + 30f, 82f)
                lineTo(340f, 207f); close()
            }, Color(0xFF957D7E))
            drawOval(Color(0xFFF7AB70), Offset(peak - 25f, 77f), Size(57f, 13f))
            drawPath(Path().apply {
                moveTo(peak, 88f); lineTo(peak - 7f, 111f); lineTo(peak + 28f, 143f)
                lineTo(peak + 14f, 170f); lineTo(peak + 45f, 214f)
            }, Color(0xFFF5AF78), style = Stroke(9f + drift * 0.3f, cap = StrokeCap.Round))
            repeat(5) { i ->
                val t = (progress * 0.8f + i * 0.2f) % 1f
                drawCircle(Color(0xFFAD9998).copy(alpha = (1f - t) * 0.35f),
                    8f + t * 16f, Offset(peak + t * 27f, 70f - t * 80f))
            }
            repeat(7) { i -> habitatRock(feature(i, 303), 193f + feature(i + 1, 23), 10f, Color(0xFF947773)) }
        }
        KantoHabitat.DESERT, KantoHabitat.BURROW -> {
            habitatHills(ground, horizon, feature(1, 150))
            if (habitat == KantoHabitat.DESERT) {
                drawCircle(Color(0xFFFFF4D1), 20f, Offset(34f + feature(2, 250), 37f))
                for (x in listOf(27f + feature(0, 26), 270f + feature(1, 25))) {
                    habitatGrass(x, 170f, 39f + feature(3, 15), 0f, Color(0xFF879C70), 6f)
                }
            } else {
                repeat(3) { i ->
                    drawPath(Path().apply {
                        moveTo(0f, 177f + i * 17f)
                        quadraticTo(150f + feature(i, 30), 163f + i * 22f, 320f, 178f + i * 17f)
                    }, Color(0xFFD5B18B), style = Stroke(3f))
                }
                drawOval(Color(0xFF8B715E), Offset(100f + feature(2, 31), 188f), Size(105f, 19f))
            }
            repeat(11) { i -> habitatRock(feature(i, 316), 172f + feature(i + 1, 45),
                2f + feature(i, 5), Color(0xFFA98962)) }
        }
        KantoHabitat.MOONLIT, KantoHabitat.CAVE, KantoHabitat.HAUNTED, KantoHabitat.RUINS -> {
            if (habitat != KantoHabitat.CAVE) {
                val moonX = 42f + feature(0, 235)
                drawCircle(Color(0xFFF5EBC8), 18f, Offset(moonX, 32f))
                drawCircle(sky, 16f, Offset(moonX + 8f, 27f))
                repeat(13) { i ->
                    drawCircle(Color(0xFFEEE6F5).copy(alpha = 0.5f + sin(progress * 8f + i) * 0.25f),
                        1.3f, Offset(feature(i, 320), 8f + feature(i + 1, 80)))
                }
            }
            habitatHills(ground, 171f, feature(2, 180))
            if (habitat == KantoHabitat.CAVE) {
                repeat(9) { i ->
                    val x = i * 43f - 10f
                    drawPath(Path().apply {
                        moveTo(x - 17f, 0f); lineTo(x + 18f, 0f)
                        lineTo(x + feature(i, 17), 24f + feature(i + 1, 64)); close()
                    }, Color(0xFF687184))
                }
                repeat(5) { i -> habitatRock(feature(i, 300), 181f + feature(i + 2, 31),
                    12f + feature(i + 1, 15), Color(0xFF778093)) }
            } else if (habitat == KantoHabitat.MOONLIT) {
                repeat(5) { i -> habitatRock(feature(i, 292), 174f + feature(i + 1, 40),
                    10f + feature(i, 9), Color(0xFF8186A5)) }
            } else {
                for (i in 0..3) {
                    val x = -8f + i * 97f + feature(i, 14)
                    val y = 70f + feature(i + 2, 33)
                    drawRect(Color(0xFF77758E), Offset(x, y), Size(26f, 190f - y))
                    drawRect(Color(0xFFA29AB0), Offset(x - 5f, y), Size(36f, 9f))
                    if (habitat == KantoHabitat.HAUNTED) {
                        drawRect(Color(0xFFC9C4B9), Offset(x + 8f, y + 20f), Size(9f, 17f))
                    } else {
                        drawLine(Color(0xFFB7ABBF), Offset(x + 8f, y + 14f), Offset(x + 8f, 178f), 2f)
                    }
                }
            }
        }
        KantoHabitat.CITY, KantoHabitat.POWER_PLANT, KantoHabitat.DOJO -> {
            if (habitat == KantoHabitat.DOJO) {
                repeat(7) { i ->
                    drawLine(Color(0xFFB7937F), Offset(i * 54f, 0f), Offset(i * 54f, 167f), 3f)
                }
                for (x in listOf(25f + feature(0, 30), 260f + feature(1, 28))) {
                    drawRect(Color(0xFFC68D80), Offset(x, 34f), Size(24f, 76f))
                    drawCircle(Color(0xFFF3D8AA), 7f, Offset(x + 12f, 59f))
                }
            } else {
                repeat(7) { i ->
                    val x = i * 53f - 13f
                    val top = 44f + feature(i, 72)
                    drawRect(Color(0xFF8797A7), Offset(x, top), Size(43f, 160f - top))
                    repeat(3) { row ->
                        drawRect(Color(0xFFE1DDC2), Offset(x + 8f, top + 9f + row * 14f), Size(7f, 7f))
                        drawRect(Color(0xFFBACDCE), Offset(x + 25f, top + 9f + row * 14f), Size(7f, 7f))
                    }
                }
            }
            drawRect(ground, Offset(0f, 166f), Size(320f, 54f))
            repeat(4) { i ->
                drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, 174f + i * 15f),
                    Offset(320f, 174f + i * 15f), 1f)
            }
            if (habitat == KantoHabitat.POWER_PLANT) {
                for (x in listOf(28f, 292f)) {
                    drawLine(Color(0xFF5D7886), Offset(x, 39f), Offset(x, 184f), 6f)
                    drawLine(Color(0xFF5D7886), Offset(x - 15f, 53f), Offset(x + 15f, 53f), 4f)
                }
                drawPath(Path().apply {
                    moveTo(28f, 48f); quadraticTo(160f, 90f + drift, 292f, 48f)
                }, Color(0xFF526C81), style = Stroke(2f))
            }
        }
        KantoHabitat.SWAMP -> {
            habitatHills(Color(0xFF8DA28C), horizon, feature(0, 150))
            drawOval(ground, Offset(-25f, 142f), Size(376f, 99f))
            habitatWaves(ndex, progress, 160f, Color(0xFFB8C3A1))
            repeat(5) { i ->
                val x = 10f + feature(i, 300)
                drawCircle(Color(0xFFC3C8AA).copy(alpha = 0.5f),
                    3f + sin(progress * 12f + i) * 2f, Offset(x, 186f + feature(i + 1, 21)),
                    style = Stroke(1.5f))
            }
            for (x in listOf(24f + feature(1, 15), 272f + feature(2, 20))) {
                drawLine(Color(0xFF6D7E70), Offset(x, 187f), Offset(x + 6f, 82f), 7f)
                drawLine(Color(0xFF6D7E70), Offset(x + 4f, 127f), Offset(x - 16f, 97f), 4f)
            }
        }
        KantoHabitat.DIGITAL -> {
            val grid = Color(0xFF93D9CE).copy(alpha = 0.45f)
            repeat(9) { i ->
                drawLine(grid, Offset(160f, 102f), Offset(i * 55f - 60f, 220f), 1f)
            }
            repeat(7) { i ->
                val y = 112f + i * i * 3f
                drawLine(grid, Offset(0f, y), Offset(320f, y), 1f)
            }
            repeat(13) { i ->
                val x = feature(i, 310)
                val y = 10f + feature(i + 1, 105) + drift
                drawRect(Color(0xFFB2E5E7).copy(alpha = 0.3f + sin(progress * 14f + i) * 0.2f),
                    Offset(x, y), Size(5f + feature(i, 9), 5f + feature(i, 9)), style = Stroke(1.5f))
            }
        }
    }
}

private fun DrawScope.habitatHills(color: Color, horizon: Float, bend: Float) {
    drawPath(Path().apply {
        moveTo(0f, horizon + 16f)
        quadraticTo(70f + bend, horizon - 49f, 320f, horizon + 7f)
        lineTo(320f, 220f); lineTo(0f, 220f); close()
    }, color.copy(alpha = 0.65f))
    drawPath(Path().apply {
        moveTo(0f, horizon + 49f)
        quadraticTo(220f - bend, horizon + 2f, 320f, horizon + 43f)
        lineTo(320f, 220f); lineTo(0f, 220f); close()
    }, color)
}

private fun DrawScope.habitatGrass(
    x: Float, y: Float, height: Float, sway: Float, color: Color, width: Float = 2f
) {
    for (side in -1..1) {
        drawPath(Path().apply {
            moveTo(x, y)
            quadraticTo(x + side * 7f, y - height * 0.4f, x + side * 10f + sway, y - height)
        }, color, style = Stroke(width, cap = StrokeCap.Round))
    }
}

private fun DrawScope.habitatCloud(x: Float, y: Float, scale: Float) {
    drawOval(Color.White.copy(alpha = 0.6f), Offset(x, y), Size(66f * scale, 17f * scale))
    drawCircle(Color.White.copy(alpha = 0.6f), 14f * scale, Offset(x + 29f * scale, y))
}

private fun DrawScope.habitatWaves(ndex: Int, progress: Float, y: Float, color: Color) {
    repeat(7) { i ->
        val x = (i * 57f + ndex * 17f) % 340f - 15f
        drawPath(Path().apply {
            moveTo(x, y + i * 6f)
            quadraticTo(x + 14f, y + i * 6f + sin(progress * 10f + i) * 4f, x + 30f, y + i * 6f)
        }, color.copy(alpha = 0.6f), style = Stroke(1.5f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.habitatRock(x: Float, y: Float, radius: Float, color: Color) {
    drawPath(Path().apply {
        moveTo(x - radius, y); lineTo(x - radius * 0.7f, y - radius)
        lineTo(x + radius * 0.3f, y - radius * 1.3f)
        lineTo(x + radius, y - radius * 0.5f); lineTo(x + radius * 0.8f, y); close()
    }, color)
}
