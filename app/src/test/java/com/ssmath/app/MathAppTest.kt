package com.ssmath.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import java.io.File
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp")
class MathAppTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val application get() = ApplicationProvider.getApplicationContext<Application>()
    private var now = 1_000L

    @Before fun setup() {
        application.getSharedPreferences("settings", 0).edit().clear().commit()
        File(application.filesDir, "practice_history.json").delete()
        DebugLog.initialize(application)
    }

    private fun model() = MathViewModel(application, ProblemGenerator(Random(7)), clock = { now }, wallClock = { 1_700_000_000_000 })

    @Test fun timerOnlyRunsWhileTheGameIsVisible() {
        val model = model()
        model.selectOperation(Operation.ADDITION)
        model.updateMaximum("10")
        model.submitSetup()
        assertEquals(Screen.READY, model.screen)
        model.start()
        now += 5_000
        assertEquals(5_000, model.elapsedMs())
        model.openSettings()
        now += 10_000
        assertEquals(5_000, model.elapsedMs())
        model.closeOverlay()
        model.setForeground(false)
        now += 10_000
        model.setForeground(true)
        now += 1_000
        assertEquals(6_000, model.elapsedMs())
    }

    @Test fun fullPracticeShowsResultsSavesHistoryAndReturnsToSetup() {
        val model = model()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithText("What would you like to practice?").assertIsDisplayed()
        compose.onNodeWithText("What is the maximum number?").assertIsDisplayed()
        compose.onNodeWithTag("submit-setup").assertIsNotEnabled()
        compose.onNodeWithTag("operation-MULTIPLICATION").performClick()
        compose.onNodeWithTag("maximum-input").performTextReplacement("6")
        compose.onNodeWithTag("submit-setup").performClick()

        compose.onNodeWithText("Press Start when Ready").assertIsDisplayed()
        compose.onNodeWithTag("start-button").performClick()
        compose.onNodeWithTag("timer").assertIsDisplayed()
        compose.onNodeWithTag("wrong-tally").assertTextEquals("Wrong: 0")

        answer(model.game!!.problem.answer)
        compose.onNodeWithTag("points").assertTextEquals("Points: 1")
        repeat(MAX_WRONG_ANSWERS - 1) { wrong ->
            answer(model.game!!.problem.answer + 1)
            compose.onNodeWithTag("wrong-tally").assertTextEquals("Wrong: ${wrong + 1}")
        }
        answer(model.game!!.problem.answer + 1)

        compose.onNodeWithTag("result-correct").assertTextEquals("You got 1 right!")
        assertEquals(1, compose.onAllNodesWithContentDescription("Correct").fetchSemanticsNodes().size)
        assertEquals(MAX_WRONG_ANSWERS, compose.onAllNodesWithContentDescription("Wrong").fetchSemanticsNodes().size)
        compose.waitUntil(5_000) { model.history.size == 1 }
        assertEquals(1, HistoryStore(File(application.filesDir, "practice_history.json")).load().single().correct)

        compose.onNodeWithTag("done-button").performClick()
        compose.onNodeWithText("What would you like to practice?").assertIsDisplayed()
    }

    @Test fun settingsCanHideTheTimerAndOpenPracticeHistory() {
        val model = model()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithContentDescription("Settings").assertIsDisplayed().performClick()
        compose.onNodeWithContentDescription("Show timer").assertIsOn().performClick()
        assertFalse(model.showTimer)
        compose.onNodeWithTag("practice-history").performClick()
        compose.onNodeWithText("No practice results yet", substring = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Settings").assertIsDisplayed()
        compose.onNodeWithContentDescription("Back").performClick()

        compose.onNodeWithTag("operation-ADDITION").performClick()
        compose.onNodeWithTag("submit-setup").performClick()
        compose.onNodeWithTag("start-button").performClick()
        compose.onNodeWithTag("problem").assertIsDisplayed()
        compose.onNodeWithTag("timer").assertDoesNotExist()
    }

    private fun answer(value: Int) {
        compose.onNodeWithTag("answer-input").performTextReplacement(value.toString())
        compose.onNodeWithTag("submit-answer").performClick()
        compose.waitForIdle()
    }
}
