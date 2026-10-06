package com.ssmath.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp")
class CelebrationUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun dolphinsPlayAndFinish() = playsAndFinishes(Celebration.DOLPHINS)
    @Test fun whalesPlayAndFinish() = playsAndFinishes(Celebration.WHALES)
    @Test fun anchoviesPlayAndFinish() = playsAndFinishes(Celebration.ANCHOVIES)
    @Test fun partyPlaysAndFinishes() = playsAndFinishes(Celebration.PARTY)

    private fun playsAndFinishes(celebration: Celebration) {
        var finished by mutableStateOf(false)
        var completions = 0
        compose.mainClock.autoAdvance = false
        compose.setContent {
            MathTheme {
                if (finished) Text("Results")
                else CelebrationDialog(celebration) { completions++; finished = true }
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithContentDescription(celebration.description).assertIsDisplayed()
        compose.onNodeWithText("Hurray!!").assertIsDisplayed()
        compose.mainClock.advanceTimeBy(CELEBRATION_DURATION_MS / 2L)
        compose.runOnIdle { assertFalse(finished) }
        compose.mainClock.advanceTimeBy(CELEBRATION_DURATION_MS.toLong())
        compose.onNodeWithText("Results").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, completions) }
    }

    @Test fun celebrationCanBeSkippedImmediately() {
        var finished by mutableStateOf(false)
        compose.mainClock.autoAdvance = false
        compose.setContent {
            MathTheme {
                if (!finished) CelebrationDialog(Celebration.PARTY) { finished = true }
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithTag("celebration-PARTY").assertDoesNotExist()
        compose.runOnIdle { assertTrue(finished) }
    }
}
