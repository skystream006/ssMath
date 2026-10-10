package com.ssmath.app

import android.app.Application
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
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
class ProgressIconsTest {
    @Test fun tenDistinctIconsRenderAtProgressBarSizeWithoutClipping() {
        assertEquals(10, ProgressIcon.entries.size)
        assertEquals(10, ProgressIcon.entries.map { it.label }.toSet().size)
        listOf(32, 64, 100).forEach { size ->
            val signatures = mutableSetOf<Int>()
            ProgressIcon.entries.forEach { icon ->
                val pixels = render(icon, size, size)
                assertTrue("$icon is visible at $size", pixels.count { it ushr 24 > 128 } > size * size / 5)
                assertTrue("$icon has multiple colors", pixels.filter { it ushr 24 == 255 }.toSet().size >= 3)
                assertTrue("$icon stays inside its bounds", pixels.indices.filter {
                    it % size == 0 || it % size == size - 1 || it / size == 0 || it / size == size - 1
                }.all { pixels[it] == 0 })
                signatures += pixels.contentHashCode()
            }
            assertEquals("Every icon has different artwork", 10, signatures.size)
        }
    }

    @Test fun iconsStayCenteredAndKeepTheirShapeInNonSquareBounds() {
        ProgressIcon.entries.forEach { icon ->
            val square = render(icon, 32, 32)
            val wide = render(icon, 64, 32)
            val tall = render(icon, 32, 64)
            assertTrue(wide.indices.filter { it % 64 < 16 || it % 64 >= 48 }.all { wide[it] == 0 })
            assertTrue(tall.take(16 * 32).all { it == 0 })
            assertTrue(tall.takeLast(16 * 32).all { it == 0 })
            val wideCenter = IntArray(32 * 32) { wide[it / 32 * 64 + it % 32 + 16] }
            val tallCenter = tall.copyOfRange(16 * 32, 48 * 32)
            listOf(wideCenter, tallCenter).forEach { centered ->
                assertTrue("$icon keeps its artwork when centered",
                    square.indices.count { square[it] == centered[it] } >= square.size * 0.99f)
            }
        }
    }

    private fun render(icon: ProgressIcon, width: Int, height: Int): IntArray {
        val image = ImageBitmap(width, height)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(image),
            Size(width.toFloat(), height.toFloat())) { drawProgressIcon(icon) }
        return IntArray(width * height).also { image.readPixels(it) }
    }
}
