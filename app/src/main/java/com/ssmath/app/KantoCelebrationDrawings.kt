package com.ssmath.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform

internal const val KANTO_INK = 0xFF30324D
internal fun DrawScope.drawKantoCelebration(celebration: Celebration, progress: Float) {
    val ndex = requireNotNull(celebration.ndex)
    val profile = kantoSceneProfile(ndex)
    val time = progress.coerceIn(0f, 1f)
    val pose = kantoScenePose(ndex, profile.motion, time)
    drawKantoHabitat(ndex, profile.habitat, time)
    drawOval(Color(KANTO_INK).copy(alpha = 0.12f),
        Offset(pose.x - 43f, 201f), Size(86f, 8f))
    if (profile.motion == KantoMotion.HANG) {
        drawLine(Color(0xFFFFF6DC), Offset(160f, 48f),
            Offset(pose.x, pose.y - 58f), 2f)
    }
    clipRect(0f, 0f, 320f, if (profile.motion == KantoMotion.DIG) 202f else 220f) {
        withTransform({
            translate(pose.x, pose.y)
            rotate(pose.rotation, pivot = Offset.Zero)
            scale(pose.scaleX, pose.scaleY, pivot = Offset.Zero)
        }) {
            drawKantoPokemon(ndex, pose.articulation)
        }
    }
    drawKantoEffect(ndex, profile.effect, pose, time)
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
