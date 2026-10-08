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
import kotlin.math.abs
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RewardDrawingsTest {
    @Test fun allTenOriginalIllustrationsRenderDistinctWholeAndPartialArtwork() {
        val signatures = mutableSetOf<Int>()
        RewardType.entries.forEach { type ->
            val whole = artwork(type, false)
            val fragment = artwork(type, true)
            val wholeArea = whole.count { it ushr 24 > 64 }
            val fragmentArea = fragment.count { it ushr 24 > 64 }
            assertTrue("$type has a detailed whole illustration", wholeArea > 4_000)
            assertTrue("$type fragment remains visible", fragmentArea > 1_000)
            assertTrue("$type fragment is a cut portion, not another whole reward",
                fragmentArea.toFloat() / wholeArea in 0.10f..0.46f)
            assertTrue("$type whole has varied colors", whole.toSet().size > 20)
            assertTrue("$type fragment has varied colors", fragment.toSet().size > 20)
            signatures += whole.contentHashCode()
            signatures += fragment.contentHashCode()
        }
        assertEquals(10, signatures.size)
    }

    @Test fun ramenFragmentKeepsFullHeightAndHasARealJaggedMissingEdge() {
        val whole = artwork(RewardType.RAMEN, false)
        val fragment = artwork(RewardType.RAMEN, true)
        fun opaque(pixels: IntArray) = pixels.indices.filter { pixels[it] ushr 24 > 128 }
        val wholePoints = opaque(whole)
        val fragmentPoints = opaque(fragment)
        val wholeHeight = wholePoints.maxOf { it / 200 } - wholePoints.minOf { it / 200 }
        val fragmentHeight = fragmentPoints.maxOf { it / 200 } - fragmentPoints.minOf { it / 200 }
        assertTrue("A fragment must retain the full-size noodle block's height",
            abs(wholeHeight - fragmentHeight) <= 3)
        val rightEdges = (56..144).map { y ->
            (0 until 200).last { x -> fragment[y * 200 + x] ushr 24 > 128 }
        }
        assertTrue("The broken side must visibly zig-zag", rightEdges.toSet().size >= 5)
        val width = fragmentPoints.maxOf { it % 200 } - fragmentPoints.minOf { it % 200 }
        assertTrue("Only about a third of the square remains", width in 40..53)
    }

    @Test fun videoGameWholeIncludesTabletButFragmentIsOnlyPartOfController() {
        val whole = artwork(RewardType.VIDEO_GAME, false)
        val fragment = artwork(RewardType.VIDEO_GAME, true)
        assertTrue("Whole game includes the tablet above the controller",
            whole.take(60 * 200).count { it ushr 24 > 128 } > 2_000)
        assertTrue("No tablet is included in a game fragment",
            fragment.take(60 * 200).all { it ushr 24 == 0 })
        assertTrue(fragment.drop(75 * 200).count { it ushr 24 > 128 } > 1_000)
    }

    @Test fun illustrationsStayCenteredAndUndistortedInWideAndTallBounds() {
        RewardType.entries.forEach { type ->
            listOf(false, true).forEach { fragment ->
                val square = artwork(type, fragment)
                val wide = render(400, 200) { drawRewardArtwork(type, fragment) }
                val tall = render(200, 400) { drawRewardArtwork(type, fragment) }
                assertTrue(wide.indices.filter { it % 400 < 100 || it % 400 >= 300 }
                    .all { wide[it] == 0 })
                assertTrue(tall.take(100 * 200).all { it == 0 })
                val wideCenter = IntArray(40_000) { wide[it / 200 * 400 + it % 200 + 100] }
                val tallCenter = tall.copyOfRange(20_000, 60_000)
                listOf(wideCenter, tallCenter).forEach { centered ->
                    val matching = square.indices.count { pixel ->
                        listOf(0, 8, 16, 24).all { shift ->
                            abs((square[pixel] ushr shift and 255) -
                                (centered[pixel] ushr shift and 255)) <= 2
                        }
                    }
                    // Native gradient/edge rasterization can round differently after translation.
                    assertTrue("$type preserves size, shape, and color: $matching of ${square.size} pixels match",
                        matching >= square.size * 0.995f)
                }
            }
        }
    }

    @Test fun giftHopsOpensAndFinishesWithAStableConfettiFreeFrame() {
        fun gift(progress: Float, hop: Float = 0f) = render(280, 220) { drawRewardGift(progress, hop) }
        val closed = gift(0f)
        val hopping = gift(0f, 1f)
        val opening = gift(0.3f)
        val burst = gift(0.6f)
        val finished = gift(1f)
        fun changed(a: IntArray, b: IntArray) = a.indices.count { a[it] != b[it] }
        assertTrue(changed(closed, hopping) > 2_000)
        assertTrue(changed(closed, opening) > 2_000)
        assertTrue(changed(opening, burst) > 2_000)
        assertTrue(changed(burst, finished) > 100)
        assertArrayEquals(finished, gift(1.5f))
        val confettiColors = setOf(Color(0xFF38C9B2).toArgb(), Color(0xFF55BEEA).toArgb())
        fun outwardConfetti(pixels: IntArray) = pixels.indices.count {
            (it % 280 < 84 || it % 280 > 196 || it / 280 < 80) && pixels[it] in confettiColors
        }
        assertTrue("Confetti shoots beyond the box before disappearing",
            outwardConfetti(burst) > outwardConfetti(finished) + 20)
    }

    private fun artwork(type: RewardType, fragment: Boolean) =
        render(200, 200) { drawRewardArtwork(type, fragment) }

    private fun render(width: Int, height: Int, draw: DrawScope.() -> Unit): IntArray {
        val image = ImageBitmap(width, height)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(image),
            Size(width.toFloat(), height.toFloat()), draw)
        return IntArray(width * height).also { image.readPixels(it) }
    }
}
