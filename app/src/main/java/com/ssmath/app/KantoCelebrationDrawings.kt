package com.ssmath.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

internal const val KANTO_INK = 0xFF30324D
private val confettiColors = listOf(Color(0xFFFFCA60), Color(0xFFEF86AB), Color(0xFF71CABB))

internal fun DrawScope.drawKantoCelebration(celebration: Celebration, progress: Float) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFFEAF5FF), Color(0xFFF4E9FF)), 0f, 220f),
        size = Size(320f, 220f))
    repeat(24) { index ->
        val phase = (progress * 1.7f + index * 0.137f) % 1f
        val x = 10f + (index * 73 % 300) + sin(phase * 8f + index) * 6f
        withTransform({
            translate(x, -10f + phase * 245f)
            rotate(index * 31f + phase * 240f, pivot = Offset.Zero)
        }) {
            drawRect(confettiColors[index % confettiColors.size], Offset(-2f, -4f), Size(4f, 8f))
        }
    }
    val motion = sin(progress * 7f * PI.toFloat())
    val hop = abs(sin(progress * 3.5f * PI.toFloat()))
    drawOval(Color(KANTO_INK).copy(alpha = 0.12f),
        Offset(111f + hop * 8f, 203f), Size(98f - hop * 16f, 9f))
    withTransform({
        translate(160f + motion * 8f, 148f - hop * 12f)
        rotate(motion * 4f, pivot = Offset.Zero)
    }) {
        drawKantoPokemon(requireNotNull(celebration.ndex), motion)
    }
}

internal fun DrawScope.drawKantoPokemon(ndex: Int, motion: Float) {
    when (ndex) {
        in 2..50 -> drawKantoEarly(ndex, motion)
        in 51..100 -> drawKantoMiddle(ndex, motion)
        in 101..151 -> drawKantoLate(ndex, motion)
        else -> error("No Kanto artwork for Ndex $ndex")
    }
}

internal fun DrawScope.kantoOval(color: Long, x: Float, y: Float, width: Float, height: Float) {
    drawOval(Color(color), Offset(x, y), Size(width, height))
    drawOval(Color(KANTO_INK), Offset(x, y), Size(width, height), style = Stroke(2f))
}

internal fun DrawScope.kantoPolygon(color: Long, vararg points: Float) {
    val path = Path().apply {
        moveTo(points[0], points[1])
        for (index in 2 until points.size step 2) lineTo(points[index], points[index + 1])
        close()
    }
    drawPath(path, Color(color))
    drawPath(path, Color(KANTO_INK), style = Stroke(2f, join = StrokeJoin.Round))
}

internal fun DrawScope.kantoLine(color: Long, width: Float, vararg points: Float) {
    val path = Path().apply {
        moveTo(points[0], points[1])
        for (index in 2 until points.size step 2) lineTo(points[index], points[index + 1])
    }
    drawPath(path, Color(color), style = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

internal fun DrawScope.kantoEyes(x: Float, y: Float, spacing: Float = 18f, radius: Float = 4f) {
    for (side in listOf(-1f, 1f)) {
        drawCircle(Color(KANTO_INK), radius, Offset(x + side * spacing, y))
        drawCircle(Color.White, radius * 0.3f, Offset(x + side * spacing - radius * 0.25f, y - radius * 0.3f))
    }
}

internal fun DrawScope.kantoSmile(x: Float, y: Float, width: Float = 12f) {
    drawPath(Path().apply {
        moveTo(x - width / 2f, y)
        quadraticTo(x, y + width * 0.65f, x + width / 2f, y)
    }, Color(KANTO_INK), style = Stroke(2f, cap = StrokeCap.Round))
}
