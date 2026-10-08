package com.ssmath.app

import android.app.Application
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CelebrationDrawingsTest {
    private val drawings = mapOf<String, DrawScope.(Float) -> Unit>(
        "Candy shower" to DrawScope::drawCandyShower,
        "Pikachu" to DrawScope::drawPikachuCelebration,
        "Squirtle" to DrawScope::drawSquirtleCelebration,
        "Bulbasaur" to DrawScope::drawBulbasaurCelebration,
        "Charmander" to DrawScope::drawCharmanderCelebration,
        "Jigglypuff" to DrawScope::drawJigglypuffCelebration
    )

    @Test fun everyNewSceneDrawsChangingFramesThroughoutPlayback() {
        drawings.forEach { (name, draw) ->
            var previous = render(draw, 0f)
            listOf(0.2f, 0.4f, 0.6f, 0.8f, 1f).forEach { progress ->
                val frame = render(draw, progress)
                val changed = frame.indices.count { frame[it] != previous[it] }
                assertTrue("$name must animate at progress $progress", changed > 300)
                assertTrue("$name must fill the scene", frame.all { (it ushr 24) == 255 })
                previous = frame
            }
        }
    }

    @Test fun pikachuApproachesBeforeZappingLightning() {
        val early = render(DrawScope::drawPikachuCelebration, 0f)
        val arrived = render(DrawScope::drawPikachuCelebration, 0.45f)
        val zapping = render(DrawScope::drawPikachuCelebration, 0.7f)
        val yellow = Color(0xFFFFD84F).toArgb()
        val lightning = Color(0xFFE5A900).toArgb()
        assertTrue(arrived.count { it == yellow } > early.count { it == yellow } * 3)
        assertEquals(0, early.count { it == lightning })
        assertTrue(zapping.count { it == lightning } > 100)
    }

    @Test fun waterLeavesAndFireTravelOutwardFromTheirCharacters() {
        val water = render(DrawScope::drawSquirtleCelebration, 0.5f)
        val leaves = render(DrawScope::drawBulbasaurCelebration, 0.5f)
        val fire = render(DrawScope::drawCharmanderCelebration, 0.5f)
        val waterColor = Color(0xFF259EDB).toArgb()
        val leafColor = Color(0xFF55B85A).toArgb()
        val fireColor = Color(0xFFFF7540).toArgb()
        assertTrue(water.indices.count { it % 320 > 200 && water[it] == waterColor } > 100)
        assertTrue(leaves.indices.count { it % 320 > 170 && leaves[it] == leafColor } > 100)
        assertTrue(fire.indices.count { it / 320 < 100 && fire[it] == fireColor } > 100)
    }

    @Test fun jigglypuffRollsAcrossTheSceneThenJumpsUp() {
        val start = render(DrawScope::drawJigglypuffCelebration, 0f)
        val rolled = render(DrawScope::drawJigglypuffCelebration, 0.48f)
        val jumping = render(DrawScope::drawJigglypuffCelebration, 0.61f)
        val pink = Color(0xFFFFB5D8).toArgb()
        fun center(pixels: IntArray, axis: (Int) -> Int) =
            pixels.indices.filter { pixels[it] == pink }.map(axis).average()
        assertTrue(center(rolled) { it % 320 } > center(start) { it % 320 } + 100)
        assertTrue(center(jumping) { it / 320 } < center(rolled) { it / 320 } - 40)
    }

    @Test fun jigglypuffDoesNotClipAtTheBottomWhileRolling() {
        val pink = Color(0xFFFFB5D8).toArgb()
        val outline = Color(0xFF30324D).toArgb()
        for (step in 0..48) {
            val progress = step / 100f
            val frame = render(DrawScope::drawJigglypuffCelebration, progress)
            assertFalse("Jigglypuff clips at progress $progress",
                frame.takeLast(320).any { it == pink || it == outline })
        }
    }

    private fun render(draw: DrawScope.(Float) -> Unit, progress: Float): IntArray {
        val image = ImageBitmap(320, 220)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(image), Size(320f, 220f)) {
            draw(progress)
        }
        return IntArray(320 * 220).also { image.readPixels(it) }
    }
}
