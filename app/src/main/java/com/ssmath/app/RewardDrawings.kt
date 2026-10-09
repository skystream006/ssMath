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

internal fun DrawScope.drawRewardArtwork(type: RewardType, fragment: Boolean, progress: Float = 0f) {
    val scale = min(size.width, size.height) / 200f
    withTransform({
        translate((size.width - 200f * scale) / 2f, (size.height - 200f * scale) / 2f)
        scale(scale, scale, pivot = Offset.Zero)
    }) {
        val phase = progress.coerceIn(0f, 1f)
        if (fragment) drawRewardFragment(type, phase) else drawWholeReward(type, phase)
    }
}

private fun DrawScope.drawWholeReward(type: RewardType, progress: Float) {
    when (type) {
        RewardType.LOLLIPOP -> drawLollipop()
        RewardType.ICE_CREAM -> drawIceCream()
        RewardType.GUMMI_BEAR -> drawGummiBear()
        RewardType.RAMEN -> drawRamen()
        RewardType.VIDEO_GAME -> {
            drawTablet()
            drawController()
        }
        RewardType.BED_TIME -> drawBedTime(progress)
        RewardType.RESTAURANT -> drawRestaurant(progress)
    }
}

private fun DrawScope.drawRewardFragment(type: RewardType, progress: Float) {
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
            if (type == RewardType.VIDEO_GAME) drawController() else drawWholeReward(type, progress)
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

private fun DrawScope.drawBedTime(progress: Float) {
    val night = Color(0xFF494579)
    val breath = (1f - cos(progress * 2f * PI.toFloat())) / 2f
    drawRoundRect(night, Offset(28f, 24f), Size(144f, 152f), CornerRadius(18f))
    drawRoundRect(rewardInk, Offset(28f, 24f), Size(144f, 152f), CornerRadius(18f), style = rewardOutline)
    drawPath(Path().apply {
        moveTo(151f, 37f)
        cubicTo(123f, 33f, 120f, 70f, 147f, 73f)
        quadraticTo(160f, 74f, 164f, 62f)
        cubicTo(140f, 72f, 137f, 48f, 151f, 37f)
        close()
    }, Color(0xFFFFDF8C))
    listOf(Offset(47f, 43f), Offset(98f, 38f), Offset(113f, 78f), Offset(160f, 91f))
        .forEachIndexed { index, point ->
            val twinkle = (1f + cos(progress * 2f * PI.toFloat() + index)) / 2f
            drawGiftStar(point, 3f + twinkle * 2f, Color(0xFFFFEDB2).copy(alpha = 0.5f + twinkle * 0.5f))
        }
    withTransform({ translate(0f, -breath * 5f) }) {
        drawPath(Path().apply {
            moveTo(61f, 59f); lineTo(72f, 59f); lineTo(61f, 70f); lineTo(72f, 70f)
            moveTo(82f, 45f); lineTo(90f, 45f); lineTo(82f, 53f); lineTo(90f, 53f)
        }, Color(0xFFD5CBFF), style = Stroke(2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
    drawRoundRect(Color(0xFFE7AA79), Offset(36f, 91f), Size(10f, 80f), CornerRadius(4f))
    drawRoundRect(rewardInk, Offset(36f, 91f), Size(10f, 80f), CornerRadius(4f), style = rewardOutline)
    drawRoundRect(Color(0xFFE7AA79), Offset(153f, 115f), Size(10f, 56f), CornerRadius(4f))
    drawRoundRect(rewardInk, Offset(153f, 115f), Size(10f, 56f), CornerRadius(4f), style = rewardOutline)
    drawRoundRect(Color(0xFFFFE9D0), Offset(43f, 115f), Size(111f, 36f), CornerRadius(8f))
    drawRoundRect(rewardInk, Offset(43f, 115f), Size(111f, 36f), CornerRadius(8f), style = rewardOutline)
    drawRoundRect(Color(0xFFECE7FF), Offset(44f, 107f), Size(48f, 20f), CornerRadius(8f))
    withTransform({ translate(0f, -breath * 2f) }) {
        drawCircle(Color(0xFFF5C69E), 17f, Offset(66f, 106f))
        drawCircle(rewardInk, 17f, Offset(66f, 106f), style = Stroke(2f))
        drawArc(Color(0xFF8B573F), 180f, 160f, false,
            Offset(49f, 89f), Size(34f, 26f), style = Stroke(6f, cap = StrokeCap.Round))
        drawArc(rewardInk, 15f, 150f, false,
            Offset(62f, 102f), Size(10f, 6f), style = Stroke(2f, cap = StrokeCap.Round))
        drawCircle(Color(0xFFEB9A93), 3f, Offset(76f, 111f))
        val blanket = Path().apply {
            moveTo(87f, 113f)
            quadraticTo(114f, 101f, 153f, 117f)
            lineTo(153f, 151f); lineTo(85f, 151f)
            quadraticTo(80f, 132f, 87f, 113f); close()
        }
        drawPath(blanket, Color(0xFF8D80D6))
        drawPath(blanket, rewardInk, style = rewardOutline)
        drawGiftStar(Offset(106f, 126f), 5f, Color(0xFFFFDF8C))
        drawGiftStar(Offset(136f, 137f), 5f, Color(0xFFFFDF8C))
    }
    drawRoundRect(Color(0xFFC88A5D), Offset(43f, 151f), Size(111f, 9f), CornerRadius(3f))
    drawRoundRect(rewardInk, Offset(43f, 151f), Size(111f, 9f), CornerRadius(3f), style = rewardOutline)
}

private fun DrawScope.drawRestaurant(progress: Float) {
    val wave = sin(progress * 2f * PI.toFloat())
    drawRoundRect(Color(0xFFFFE9C8), Offset(28f, 35f), Size(144f, 139f), CornerRadius(9f))
    drawRoundRect(rewardInk, Offset(28f, 35f), Size(144f, 139f), CornerRadius(9f), style = rewardOutline)
    drawRoundRect(Color(0xFF398F89), Offset(68f, 22f), Size(64f, 32f), CornerRadius(9f))
    drawRoundRect(rewardInk, Offset(68f, 22f), Size(64f, 32f), CornerRadius(9f), style = rewardOutline)
    drawCircle(Color(0xFFFFF6E5), 10f, Offset(100f, 38f))
    drawCircle(Color(0xFF86C7BC), 6f, Offset(100f, 38f), style = Stroke(1.5f))
    drawLine(Color.White, Offset(82f, 28f), Offset(82f, 47f), 2f, cap = StrokeCap.Round)
    drawPath(Path().apply {
        moveTo(78f, 28f); lineTo(78f, 35f); quadraticTo(82f, 40f, 86f, 35f); lineTo(86f, 28f)
    }, Color.White, style = Stroke(2f, cap = StrokeCap.Round))
    drawOval(Color.White, Offset(114f, 28f), Size(7f, 10f))
    drawLine(Color.White, Offset(117.5f, 36f), Offset(117.5f, 47f), 2f, cap = StrokeCap.Round)
    drawRoundRect(Color(0xFF6EBAB6), Offset(39f, 99f), Size(62f, 53f), CornerRadius(5f))
    drawRoundRect(rewardInk, Offset(39f, 99f), Size(62f, 53f), CornerRadius(5f), style = rewardOutline)
    drawLine(Color(0xFFD1F1E4), Offset(45f, 106f), Offset(55f, 106f), 3f, cap = StrokeCap.Round)
    repeat(3) { index ->
        val x = 55f + index * 14f
        val drift = sin(progress * 2f * PI.toFloat() + index) * 3f
        drawPath(Path().apply {
            moveTo(x, 126f)
            cubicTo(x - 5f + drift, 121f, x + 5f + drift, 117f, x + drift, 112f)
        }, Color(0xFFE9FFF5).copy(alpha = 0.75f), style = Stroke(2.5f, cap = StrokeCap.Round))
    }
    drawOval(Color(0xFFFFF6E5), Offset(47f, 135f), Size(47f, 10f))
    drawArc(Color(0xFFF3AB50), 180f, 180f, true, Offset(53f, 123f), Size(35f, 25f))
    drawCircle(Color(0xFFD65560), 4f, Offset(72f, 129f))
    drawLine(Color(0xFF398F89), Offset(72f, 125f), Offset(77f, 122f), 2f, cap = StrokeCap.Round)
    drawRoundRect(Color(0xFF967BC3), Offset(118f, 96f), Size(41f, 73f), CornerRadius(5f))
    drawRoundRect(rewardInk, Offset(118f, 96f), Size(41f, 73f), CornerRadius(5f), style = rewardOutline)
    drawRoundRect(Color(0xFFCAEAE3), Offset(125f, 104f), Size(27f, 35f), CornerRadius(3f))
    drawCircle(Color(0xFFFFDF8C), 3f, Offset(150f, 149f))
    repeat(8) { index ->
        val left = 28f + index * 18f
        val bottom = 81f + wave * 2f
        val stripe = Path().apply {
            moveTo(left + 4f, 59f); lineTo(left + 14f, 59f)
            lineTo(left + 18f, bottom)
            quadraticTo(left + 9f, bottom + 10f, left, bottom)
            close()
        }
        drawPath(stripe, if (index % 2 == 0) Color(0xFFE46B71) else Color(0xFFFFF6E5))
        drawPath(stripe, rewardInk, style = Stroke(1.5f, join = StrokeJoin.Round))
    }
    drawLine(rewardInk, Offset(32f, 59f), Offset(168f, 59f), 3f, cap = StrokeCap.Round)
    drawRoundRect(Color(0xFFC88A5D), Offset(24f, 169f), Size(152f, 7f), CornerRadius(3f))
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
