package com.ssmath.app

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private val rewardInk = Color(0xFF38324F)
private val rewardOutline = Stroke(3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
private val giftColors = listOf(
    Color(0xFFFFC857), Color(0xFFFF6F9F), Color(0xFF38C9B2),
    Color(0xFF8472E8), Color(0xFF55BEEA)
)

internal fun DrawScope.drawRewardArtwork(type: RewardType, fragment: Boolean) {
    val scale = min(size.width, size.height) / 200f
    withTransform({
        translate((size.width - 200f * scale) / 2f, (size.height - 200f * scale) / 2f)
        scale(scale, scale, pivot = Offset.Zero)
    }) {
        if (fragment) drawRewardFragment(type) else drawWholeReward(type)
    }
}

private fun DrawScope.drawWholeReward(type: RewardType) {
    when (type) {
        RewardType.LOLLIPOP -> drawLollipop()
        RewardType.ICE_CREAM -> drawIceCream()
        RewardType.GUMMI_BEAR -> drawGummiBear()
        RewardType.RAMEN -> drawRamen()
        RewardType.VIDEO_GAME -> {
            drawTablet()
            drawController()
        }
    }
}

private fun DrawScope.drawRewardFragment(type: RewardType) {
    // Cut the full-size drawing, then center the piece. Details are never miniaturized.
    if (type == RewardType.LOLLIPOP) {
        val wedge = Path().apply {
            moveTo(100f, 26f)
            lineTo(98f, 42f); lineTo(104f, 50f); lineTo(98f, 58f)
            lineTo(103f, 68f); lineTo(100f, 80f)
            lineTo(110f, 82f); lineTo(115f, 91f); lineTo(126f, 91f)
            lineTo(132f, 102f); lineTo(151f, 110f)
            lineTo(180f, 110f); lineTo(180f, 10f); lineTo(100f, 10f)
            close()
        }
        val edge = Path().apply {
            moveTo(100f, 29f)
            lineTo(98f, 42f); lineTo(104f, 50f); lineTo(98f, 58f)
            lineTo(103f, 68f); lineTo(100f, 80f)
            lineTo(110f, 82f); lineTo(115f, 91f); lineTo(126f, 91f)
            lineTo(132f, 102f); lineTo(145f, 107f)
        }
        withTransform({ translate(-23f, 34f) }) {
            clipPath(wedge) { drawLollipop() }
            drawPath(edge, rewardInk, style = rewardOutline)
        }
        return
    }
    val cutX = when (type) {
        RewardType.ICE_CREAM -> 83f
        RewardType.GUMMI_BEAR -> 84f
        RewardType.RAMEN -> 79f
        else -> 78f
    }
    val edge = Path().apply {
        moveTo(cutX, 0f)
        for (y in 0..200 step 8) {
            lineTo(cutX + if (y % 16 == 0) 3f else -3f, y.toFloat())
        }
    }
    val piece = Path().apply {
        addPath(edge)
        lineTo(0f, 200f); lineTo(0f, 0f); close()
    }
    val edgeStart = if (type == RewardType.VIDEO_GAME) 104 else 48
    val edgeEnd = when (type) {
        RewardType.ICE_CREAM -> 136
        RewardType.VIDEO_GAME -> 144
        else -> 160
    }
    val brokenEdge = Path().apply {
        for (y in edgeStart..edgeEnd step 8) {
            val x = cutX + if (y % 16 == 0) 3f else -3f
            if (y == edgeStart) moveTo(x, y.toFloat()) else lineTo(x, y.toFloat())
        }
    }
    withTransform({ translate(32f, if (type == RewardType.VIDEO_GAME) -23f else 0f) }) {
        clipPath(piece) {
            // A game fragment is part of the controller, never a miniature controller/tablet set.
            if (type == RewardType.VIDEO_GAME) drawController() else drawWholeReward(type)
            drawPath(brokenEdge, rewardInk.copy(alpha = 0.8f), style = Stroke(3f))
        }
    }
}

private fun DrawScope.drawLollipop() {
    drawRoundRect(rewardInk, Offset(95f, 116f), Size(11f, 69f), CornerRadius(5f))
    drawRoundRect(Color(0xFFFFF2DA), Offset(98f, 117f), Size(5f, 65f), CornerRadius(3f))
    drawCircle(Color(0xFFFF6C9F), 53f, Offset(100f, 80f))
    val swirl = Path().apply {
        for (step in 0..180) {
            val angle = step / 180f * PI.toFloat() * 5.4f
            val radius = 2f + step / 180f * 47f
            val x = 100f + cos(angle) * radius
            val y = 80f + sin(angle) * radius
            if (step == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    drawPath(swirl, Color(0xFFFFEDB2), style = Stroke(10f, cap = StrokeCap.Round))
    drawCircle(rewardInk, 53f, Offset(100f, 80f), style = rewardOutline)
    drawArc(Color.White.copy(alpha = 0.7f), 215f, 54f, false,
        Offset(57f, 37f), Size(86f, 86f), style = Stroke(4f, cap = StrokeCap.Round))
    drawCircle(Color.White.copy(alpha = 0.6f), 3f, Offset(63f, 71f))
}

private fun DrawScope.drawIceCream() {
    val cone = Path().apply {
        moveTo(61f, 98f); lineTo(140f, 98f); lineTo(103f, 184f); close()
    }
    drawPath(cone, Brush.linearGradient(listOf(Color(0xFFF7C778), Color(0xFFDE9447)),
        Offset(65f, 100f), Offset(133f, 180f)))
    clipPath(cone) {
        for (x in 0..190 step 17) {
            drawLine(Color(0xFFC8883F), Offset(x.toFloat(), 93f), Offset(x + 68f, 191f), 2.5f)
            drawLine(Color(0xFFFFDF9E), Offset(x.toFloat(), 93f), Offset(x - 68f, 191f), 3f)
        }
    }
    drawPath(cone, rewardInk, style = rewardOutline)
    val scoop = Path().apply {
        moveTo(48f, 92f)
        cubicTo(43f, 67f, 64f, 36f, 94f, 36f)
        cubicTo(124f, 30f, 153f, 57f, 151f, 87f)
        cubicTo(161f, 101f, 149f, 115f, 136f, 106f)
        cubicTo(129f, 119f, 115f, 118f, 109f, 108f)
        cubicTo(98f, 122f, 82f, 116f, 80f, 107f)
        cubicTo(64f, 118f, 43f, 109f, 48f, 92f)
        close()
    }
    drawPath(scoop, Brush.verticalGradient(listOf(Color(0xFFFFC3D9), Color(0xFFFF89B6)), 36f, 116f))
    drawPath(scoop, rewardInk, style = rewardOutline)
    drawArc(Color.White.copy(alpha = 0.65f), 200f, 66f, false,
        Offset(58f, 47f), Size(69f, 62f), style = Stroke(5f, cap = StrokeCap.Round))
    listOf(Offset(76f, 72f), Offset(112f, 67f), Offset(129f, 88f), Offset(91f, 95f)).forEachIndexed { i, p ->
        drawLine(if (i % 2 == 0) Color(0xFF8472E8) else Color(0xFF2B9D8A),
            p, p + Offset(5f, 3f), 3f, cap = StrokeCap.Round)
    }
    drawCircle(Color(0xFFF05B66), 10f, Offset(104f, 32f))
    drawCircle(rewardInk, 10f, Offset(104f, 32f), style = Stroke(2.5f))
    drawPath(Path().apply {
        moveTo(105f, 24f); quadraticTo(105f, 15f, 114f, 14f)
    }, Color(0xFF318664), style = Stroke(3f, cap = StrokeCap.Round))
    drawCircle(Color(0xFFFFD1D6), 2.5f, Offset(101f, 29f))
}

private fun DrawScope.drawGummiBear() {
    val bear = Path().apply {
        moveTo(66f, 55f)
        cubicTo(43f, 40f, 55f, 17f, 72f, 25f)
        quadraticTo(82f, 28f, 83f, 37f)
        quadraticTo(100f, 30f, 117f, 37f)
        cubicTo(119f, 16f, 148f, 18f, 147f, 39f)
        quadraticTo(147f, 50f, 135f, 55f)
        quadraticTo(148f, 72f, 135f, 92f)
        cubicTo(165f, 94f, 168f, 123f, 144f, 129f)
        quadraticTo(146f, 144f, 137f, 154f)
        cubicTo(152f, 178f, 125f, 189f, 109f, 169f)
        quadraticTo(100f, 173f, 91f, 169f)
        cubicTo(73f, 190f, 47f, 177f, 63f, 153f)
        quadraticTo(53f, 142f, 57f, 129f)
        cubicTo(30f, 125f, 36f, 93f, 65f, 93f)
        quadraticTo(51f, 74f, 66f, 55f)
        close()
    }
    drawPath(bear, Brush.linearGradient(listOf(Color(0xFF69E2B1), Color(0xFF24B59C)),
        Offset(55f, 35f), Offset(145f, 166f)))
    drawPath(bear, rewardInk, style = rewardOutline)
    drawOval(Color(0xFF9AF1BD), Offset(71f, 106f), Size(58f, 48f))
    drawOval(Color(0xFFAEF2CC), Offset(82f, 70f), Size(36f, 24f))
    drawCircle(rewardInk, 3.5f, Offset(79f, 68f))
    drawCircle(rewardInk, 3.5f, Offset(121f, 68f))
    drawOval(rewardInk, Offset(95f, 76f), Size(10f, 7f))
    drawArc(rewardInk, 15f, 150f, false, Offset(90f, 80f), Size(20f, 10f), style = Stroke(2f))
    drawOval(Color.White.copy(alpha = 0.6f), Offset(62f, 29f), Size(10f, 12f))
    drawOval(Color.White.copy(alpha = 0.5f), Offset(64f, 58f), Size(8f, 17f))
    drawOval(Color.White.copy(alpha = 0.4f), Offset(48f, 104f), Size(7f, 12f))
    drawOval(Color.White.copy(alpha = 0.4f), Offset(76f, 114f), Size(11f, 19f))
    drawLine(Color(0xFF1D978A), Offset(72f, 163f), Offset(82f, 165f), 3f, cap = StrokeCap.Round)
    drawLine(Color(0xFF1D978A), Offset(119f, 165f), Offset(129f, 163f), 3f, cap = StrokeCap.Round)
}

private fun DrawScope.drawRamen() {
    val block = Path().apply {
        moveTo(46f, 39f); quadraticTo(98f, 34f, 155f, 40f)
        quadraticTo(164f, 42f, 162f, 53f); lineTo(166f, 151f)
        quadraticTo(165f, 164f, 153f, 165f); lineTo(47f, 163f)
        quadraticTo(34f, 163f, 36f, 149f); lineTo(37f, 52f)
        quadraticTo(36f, 41f, 46f, 39f); close()
    }
    drawPath(block, Color(0xFFD99B44))
    drawPath(block, rewardInk, style = rewardOutline)
    clipPath(block) {
        for (row in 0..13) {
            val noodle = Path().apply {
                for (step in 0..62) {
                    val x = 40f + step * 2f
                    val y = 44f + row * 8.6f + sin(step * 0.76f + row * 0.9f) * 2.6f
                    if (step == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(noodle, Color(0xFFB57836), style = Stroke(7f, cap = StrokeCap.Round))
            drawPath(noodle, Color(0xFFFFD477), style = Stroke(4.8f, cap = StrokeCap.Round))
            drawPath(noodle, Color(0xFFFFE5A2), style = Stroke(1.3f, cap = StrokeCap.Round))
        }
    }
    drawPath(block, rewardInk, style = rewardOutline)
}

private fun DrawScope.drawTablet() {
    drawRoundRect(rewardInk, Offset(43f, 18f), Size(115f, 98f), CornerRadius(12f))
    drawRoundRect(Color(0xFFA3C5FF), Offset(50f, 26f), Size(101f, 78f), CornerRadius(6f))
    drawCircle(Color(0xFFFFE39B), 12f, Offset(126f, 46f))
    drawCircle(Color(0xFFD7E5FF), 2f, Offset(68f, 41f))
    drawCircle(Color(0xFFD7E5FF), 3f, Offset(97f, 34f))
    drawPath(Path().apply {
        moveTo(50f, 91f); lineTo(74f, 65f); lineTo(93f, 88f)
        lineTo(112f, 72f); lineTo(151f, 96f); lineTo(151f, 104f)
        lineTo(50f, 104f); close()
    }, Color(0xFF776DCA))
    drawRoundRect(Color(0xFF4D4788), Offset(58f, 88f), Size(83f, 8f), CornerRadius(3f))
    drawCircle(Color(0xFFBAC9E6), 2.5f, Offset(101f, 110f))
    drawLine(Color.White.copy(alpha = 0.35f), Offset(54f, 31f), Offset(83f, 31f), 3f, cap = StrokeCap.Round)
}

private fun DrawScope.drawController() {
    val body = Path().apply {
        moveTo(61f, 101f)
        cubicTo(76f, 96f, 84f, 105f, 100f, 105f)
        cubicTo(116f, 105f, 125f, 96f, 139f, 101f)
        cubicTo(157f, 102f, 167f, 132f, 174f, 154f)
        cubicTo(181f, 178f, 159f, 186f, 143f, 166f)
        lineTo(129f, 151f); lineTo(71f, 151f); lineTo(56f, 166f)
        cubicTo(38f, 186f, 18f, 177f, 26f, 153f)
        cubicTo(33f, 129f, 41f, 103f, 61f, 101f); close()
    }
    drawPath(body, Brush.verticalGradient(listOf(Color(0xFFACA1F5), Color(0xFF7762C9)), 101f, 176f))
    drawPath(body, rewardInk, style = rewardOutline)
    drawRoundRect(Color(0xFFD6CFFD), Offset(86f, 112f), Size(28f, 12f), CornerRadius(5f))
    drawRoundRect(rewardInk, Offset(56f, 115f), Size(11f, 29f), CornerRadius(2f))
    drawRoundRect(rewardInk, Offset(47f, 124f), Size(29f, 11f), CornerRadius(2f))
    drawCircle(Color(0xFFDBD5FA), 3f, Offset(61.5f, 129.5f))
    listOf(Offset(139f, 117f), Offset(150f, 129f), Offset(128f, 129f), Offset(139f, 140f))
        .forEachIndexed { index, point ->
            drawCircle(rewardInk, 6f, point)
            drawCircle(giftColors[index], 4f, point)
        }
    drawCircle(rewardInk, 10f, Offset(83f, 144f))
    drawCircle(Color(0xFF5B5389), 6f, Offset(83f, 143f))
    drawCircle(rewardInk, 10f, Offset(117f, 144f))
    drawCircle(Color(0xFF5B5389), 6f, Offset(117f, 143f))
    drawPath(Path().apply {
        moveTo(36f, 149f); quadraticTo(46f, 115f, 55f, 112f)
    }, Color.White.copy(alpha = 0.5f), style = Stroke(4f, cap = StrokeCap.Round))
}

internal fun DrawScope.drawRewardGift(openProgress: Float, hop: Float = 0f) {
    val scale = min(size.width / 280f, size.height / 220f)
    val progress = openProgress.coerceIn(0f, 1f)
    withTransform({
        translate((size.width - 280f * scale) / 2f, (size.height - 220f * scale) / 2f)
        scale(scale, scale, pivot = Offset.Zero)
    }) {
        drawRoundRect(Color(0xFFF0EBFF), Offset.Zero, Size(280f, 220f), CornerRadius(24f))
        drawCircle(Color(0xFFF9F6FF), 93f, Offset(140f, 108f))
        listOf(Offset(40f, 49f), Offset(233f, 64f), Offset(52f, 149f), Offset(221f, 160f))
            .forEachIndexed { index, point ->
                drawGiftStar(point, 6f + index % 2 * 3f, giftColors[index])
            }
        val alpha = (1f - ((progress - 0.52f) / 0.28f).coerceIn(0f, 1f))
        if (alpha > 0f) {
            val jump = if (progress == 0f) hop.coerceIn(0f, 1f) else 0f
            drawOval(rewardInk.copy(alpha = 0.12f * alpha),
                Offset(87f + jump * 10f, 198f), Size(106f - jump * 20f, 10f))
            withTransform({
                translate(0f, -jump * 17f)
                rotate(jump * -5f, pivot = Offset(140f, 196f))
            }) {
                drawRoundRect(Color(0xFFEE6599).copy(alpha = alpha),
                    Offset(84f, 121f), Size(112f, 78f), CornerRadius(9f))
                drawRoundRect(rewardInk.copy(alpha = alpha),
                    Offset(84f, 121f), Size(112f, 78f), CornerRadius(9f), style = rewardOutline)
                drawRect(Color(0xFFFFCF69).copy(alpha = alpha), Offset(130f, 123f), Size(21f, 74f))
                drawLine(Color(0xFFFF9EBF).copy(alpha = alpha), Offset(94f, 140f), Offset(94f, 184f),
                    4f, cap = StrokeCap.Round)
                val lift = (progress / 0.48f).coerceIn(0f, 1f)
                withTransform({
                    translate(-lift * 24f, -lift * 73f)
                    rotate(-lift * 28f, pivot = Offset(140f, 115f))
                }) {
                    val bow = Path().apply {
                        moveTo(139f, 109f)
                        cubicTo(98f, 105f, 96f, 67f, 118f, 79f)
                        quadraticTo(132f, 89f, 139f, 109f)
                        cubicTo(146f, 75f, 177f, 67f, 173f, 91f)
                        quadraticTo(166f, 108f, 139f, 109f); close()
                    }
                    drawPath(bow, Color(0xFFFFCF69).copy(alpha = alpha))
                    drawPath(bow, rewardInk.copy(alpha = alpha), style = rewardOutline)
                    drawRoundRect(Color(0xFF9679E8).copy(alpha = alpha),
                        Offset(77f, 108f), Size(126f, 25f), CornerRadius(6f))
                    drawRoundRect(rewardInk.copy(alpha = alpha),
                        Offset(77f, 108f), Size(126f, 25f), CornerRadius(6f), style = rewardOutline)
                    drawRect(Color(0xFFFFCF69).copy(alpha = alpha), Offset(130f, 109f), Size(21f, 23f))
                }
            }
        }
        drawGiftConfetti(progress)
    }
}

private fun DrawScope.drawGiftConfetti(progress: Float) {
    if (progress <= 0.08f || progress >= 0.92f) return
    val flight = ((progress - 0.08f) / 0.84f).coerceIn(0f, 1f)
    val fade = (1f - ((flight - 0.62f) / 0.38f).coerceIn(0f, 1f))
    repeat(26) { index ->
        val angle = (-165f + index * 6.2f) * PI.toFloat() / 180f
        val speed = 80f + (index * 29 % 75)
        val point = Offset(
            140f + cos(angle) * speed * flight,
            119f + sin(angle) * speed * flight + 85f * flight * flight
        )
        rotate(index * 33f + flight * 260f, pivot = point) {
            drawRoundRect(giftColors[index % giftColors.size].copy(alpha = fade),
                point, Size(5f, 10f), CornerRadius(1f))
        }
    }
}

private fun DrawScope.drawGiftStar(center: Offset, radius: Float, color: Color) {
    drawPath(Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + radius * 0.3f, center.y - radius * 0.3f)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + radius * 0.3f, center.y + radius * 0.3f)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - radius * 0.3f, center.y + radius * 0.3f)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - radius * 0.3f, center.y - radius * 0.3f)
        close()
    }, color)
}
