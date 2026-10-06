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

        assertNull(model.celebration)
        compose.onNodeWithText("Congratulations!").assertDoesNotExist()
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

    @Test fun correctAnswerVisibilityIsRemembered() {
        val model = model()
        assertTrue(model.showCorrectAnswers)
        model.chooseShowCorrectAnswers(false)
        assertFalse(model.showCorrectAnswers)
        val restored = model()
        assertFalse(restored.showCorrectAnswers)
        restored.chooseShowCorrectAnswers(true)
        assertTrue(model().showCorrectAnswers)
    }

    @Test fun settingsCanHideWrongAnswerFeedbackDuringPractice() {
        val model = model()
        model.selectOperation(Operation.ADDITION)
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        assertNull(model.feedback)

        val first = model.game!!.problem
        answer(first.answer + 1)
        val revealedFeedback = "Not quite: ${first.text} = ${first.answer}"
        compose.onNodeWithText(revealedFeedback).assertIsDisplayed()
        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithContentDescription("Show correct answers").assertIsOn().performClick().assertIsOff()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText(revealedFeedback).assertDoesNotExist()
        compose.onNodeWithText("Not quite!").assertIsDisplayed()
        assertEquals(Feedback("Not quite!", false), model.feedback)

        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithContentDescription("Show correct answers").performClick().assertIsOn()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText(revealedFeedback).assertIsDisplayed()
        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithContentDescription("Show correct answers").performClick().assertIsOff()
        compose.onNodeWithContentDescription("Back").performClick()

        val second = model.game!!.problem
        answer(second.answer + 1)
        compose.onNodeWithText("Not quite: ${second.text} = ${second.answer}").assertDoesNotExist()
        compose.onNodeWithText("Not quite!").assertIsDisplayed()
        compose.onNodeWithTag("points").assertTextEquals("Points: 0")
        compose.onNodeWithTag("wrong-tally").assertTextEquals("Wrong: 2")
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 3 of 10")
        answer(model.game!!.problem.answer)
        compose.onNodeWithText("Correct! +1 point").assertIsDisplayed()
        compose.onNodeWithTag("points").assertTextEquals("Points: 1")
        compose.onNodeWithTag("wrong-tally").assertTextEquals("Wrong: 2")
        compose.runOnIdle {
            model.backToSetup()
            assertNull(model.feedback)
            model.start()
            assertNull(model.feedback)
        }
    }

    @Test fun hiddenCorrectAnswersStayHiddenInResultsAndHistory() {
        val model = model()
        model.chooseShowCorrectAnswers(false)
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount("1")
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        val problem = model.game!!.problem
        val given = problem.answer + 1
        answer(given)

        compose.onNodeWithTag("result-correct").assertTextEquals("You got 0 right!")
        compose.onNodeWithContentDescription("Wrong").assertIsDisplayed()
        compose.onNodeWithText("${problem.text} = $given").assertIsDisplayed()
        compose.onNodeWithText("Correct answer:", substring = true).assertDoesNotExist()
        assertNull(model.celebration)
        compose.waitUntil(5_000) { model.history.size == 1 }
        val saved = HistoryStore(File(application.filesDir, "practice_history.json")).load().single()
        assertEquals(listOf(Attempt(problem, given)), saved.attempts)
        assertEquals(1, saved.wrong)

        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithContentDescription("Show correct answers").performClick()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Correct answer: ${problem.answer}").assertIsDisplayed()
        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithContentDescription("Show correct answers").performClick()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Correct answer:", substring = true).assertDoesNotExist()
        compose.onNodeWithTag("done-button").performClick()
        assertNull(model.feedback)

        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithTag("practice-history").performClick()
        compose.onNodeWithText("Addition · up to 10").performClick()
        compose.onNodeWithText("${problem.text} = $given").assertIsDisplayed()
        compose.onNodeWithText("Correct answer:", substring = true).assertDoesNotExist()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithContentDescription("Show correct answers").performClick()
        compose.onNodeWithTag("practice-history").performClick()
        compose.onNodeWithText("Addition · up to 10").performClick()
        compose.onNodeWithText("Correct answer: ${problem.answer}").assertIsDisplayed()
    }

    @Test fun questionCountIsValidatedAndRemembered() {
        val model = model()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("operation-ADDITION").performClick()
        compose.onNodeWithTag("question-count-input").assertTextContains(DEFAULT_QUESTION_COUNT.toString())
        listOf("", "0", "-1", "1.5", "abc", (MAX_QUESTION_COUNT + 1).toString(), "999999").forEach { invalid ->
            compose.onNodeWithTag("question-count-input").performTextReplacement(invalid)
            compose.onNodeWithTag("submit-setup").assertIsNotEnabled()
            compose.runOnIdle { model.submitSetup() }
            assertEquals(Screen.SETUP, model.screen)
        }
        compose.onNodeWithTag("question-count-input").performTextReplacement("3")
        compose.onNodeWithTag("submit-setup").performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("3 questions", substring = true).assertIsDisplayed()
        assertEquals(3, MathViewModel(application).questionCount)
        compose.onNodeWithTag("start-button").performClick()
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 1 of 3")
        answer(model.game!!.problem.answer)
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 2 of 3")
    }

    @Test fun perfectPracticeCelebratesSavesOnceAndShowsResults() {
        val model = model()
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount("2")
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        answer(model.game!!.problem.answer)
        assertNull(model.celebration)
        now += 3_000
        compose.onNodeWithTag("answer-input").performTextReplacement(model.game!!.problem.answer.toString())
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("submit-answer").performClick()
        compose.mainClock.advanceTimeBy(64)

        val celebration = model.celebration!!
        compose.onNodeWithText("Congratulations!").assertIsDisplayed()
        compose.onNodeWithText("Hurray!!").assertIsDisplayed()
        compose.onNodeWithContentDescription(celebration.description).assertIsDisplayed()
        assertEquals(Screen.RESULTS, model.screen)
        assertEquals(2, model.lastResult!!.correct)
        assertEquals(0, model.lastResult!!.wrong)
        assertEquals(3_000L, model.lastResult!!.durationMs)
        compose.runOnIdle {
            model.updateAnswer("0")
            model.submitAnswer()
        }
        assertEquals(2, model.lastResult!!.attempts.size)
        assertEquals(celebration, model.celebration)
        compose.waitUntil(5_000) { model.history.size == 1 }
        assertEquals(model.lastResult, HistoryStore(File(application.filesDir, "practice_history.json")).load().single())

        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.autoAdvance = true
        compose.onNodeWithText("Congratulations!").assertDoesNotExist()
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 2 right!")
        compose.onNodeWithTag("done-button").performClick()
        assertNull(model.celebration)
        compose.onNodeWithTag("submit-setup").performScrollTo().performClick()
        compose.onNodeWithTag("start-button").performClick()
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 1 of 2")
        assertEquals(0, model.game!!.attempts.size)
    }

    @Test fun reachingQuestionCountWithMistakesShowsResultsWithoutCelebrating() {
        val model = model()
        model.selectOperation(Operation.DIVISION)
        model.updateQuestionCount("2")
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        answer(model.game!!.problem.answer + 1)
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 2 of 2")
        answer(model.game!!.problem.answer)
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 1 right!")
        compose.onNodeWithText("Congratulations!").assertDoesNotExist()
        assertNull(model.celebration)
        assertEquals(2, model.lastResult!!.attempts.size)
        assertEquals(1, model.lastResult!!.wrong)
        compose.waitUntil(5_000) { model.history.size == 1 }
        assertEquals(model.lastResult, HistoryStore(File(application.filesDir, "practice_history.json")).load().single())
    }

    private fun answer(value: Int) {
        compose.onNodeWithTag("answer-input").performTextReplacement(value.toString())
        compose.onNodeWithTag("submit-answer").performClick()
        compose.waitForIdle()
    }
}
