package com.ssmath.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
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
import org.robolectric.annotation.GraphicsMode

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
        compose.onNodeWithText("What is the minimum number?").assertIsDisplayed()
        compose.onNodeWithText("What is the maximum number?").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("submit-setup").assertIsNotEnabled()
        compose.onNodeWithTag("operation-MULTIPLICATION").performScrollTo().performClick()
        compose.onNodeWithTag("minimum-input").performScrollTo().performTextReplacement("4")
        compose.onNodeWithTag("maximum-input").performScrollTo().performTextReplacement("6")
        compose.onNodeWithTag("submit-setup").performScrollTo().performClick()

        compose.onNodeWithText("Press Start when Ready").assertIsDisplayed()
        compose.onNodeWithText("numbers 4 to 6", substring = true).assertIsDisplayed()
        compose.onNodeWithTag("start-button").performClick()
        compose.onNodeWithTag("timer").assertIsDisplayed()
        compose.onNodeWithTag("wrong-tally").assertTextEquals("Wrong: 0")

        answer(model.game!!.problem.answer)
        compose.onNodeWithTag("points").assertTextEquals("Points: 1")
        repeat(MAX_WRONG_ANSWERS - 1) { wrong ->
            answer(model.game!!.problem.answer + 1)
            compose.onNodeWithTag("wrong-tally").assertTextEquals("Wrong: ${wrong + 1}")
            assertNull(model.earlyFinishMessage)
        }
        answer(model.game!!.problem.answer + 1)

        assertNull(model.celebration)
        compose.onNodeWithText("Congratulations!").assertDoesNotExist()
        compose.onNode(isDialog()).assertIsDisplayed()
        compose.onNodeWithText("Nice try! You got 1 right out of 10").assertIsDisplayed()
        compose.waitUntil(5_000) { model.history.size == 1 }
        compose.runOnIdle {
            model.updateAnswer("0")
            model.submitAnswer()
        }
        assertEquals(MAX_WRONG_ANSWERS + 1, model.lastResult!!.attempts.size)
        compose.onNodeWithTag("view-results").performClick()
        compose.onNode(isDialog()).assertDoesNotExist()
        assertNull(model.earlyFinishMessage)
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 1 right!")
        assertEquals(1, compose.onAllNodesWithContentDescription("Correct").fetchSemanticsNodes().size)
        assertEquals(MAX_WRONG_ANSWERS, compose.onAllNodesWithContentDescription("Wrong").fetchSemanticsNodes().size)
        compose.waitUntil(5_000) { model.history.size == 1 }
        assertEquals(1, HistoryStore(File(application.filesDir, "practice_history.json")).load().single().correct)
        assertEquals(4, model.lastResult!!.minimum)
        assertEquals(4, HistoryStore(File(application.filesDir, "practice_history.json")).load().single().minimum)
        model.lastResult!!.attempts.forEach {
            assertTrue(it.problem.left in 4..6)
            assertTrue(it.problem.right in 4..6)
        }
        compose.onNodeWithText("Multiplication · numbers 4 to 6").assertIsDisplayed()

        compose.onNodeWithTag("done-button").performClick()
        compose.onNodeWithText("What would you like to practice?").assertIsDisplayed()
        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithTag("practice-history").performClick()
        compose.onNodeWithText("Multiplication · 4 to 6").performClick()
        compose.onNodeWithText("Multiplication · numbers 4 to 6").assertIsDisplayed()
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun appUpdatesAppearFirstWithTheCheckButtonBesideTheDetails() {
        val model = model()
        compose.setContent { MathTheme { SettingsScreen(model) } }

        val heading = compose.onNodeWithText("App updates").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val installed = compose.onNodeWithText("Installed:", substring = true)
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val check = compose.onNodeWithText("Check for updates").assertIsDisplayed().assertHasClickAction()
            .assertIsEnabled().fetchSemanticsNode().boundsInRoot
        val message = compose.onNodeWithText(UpdateState().message).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val history = compose.onNodeWithTag("practice-history").assertIsDisplayed().fetchSemanticsNode().boundsInRoot

        assertTrue(heading.right <= check.left)
        assertTrue(installed.right <= check.left)
        assertEquals((heading.top + installed.bottom) / 2f, check.center.y, 1f)
        assertTrue(maxOf(installed.bottom, check.bottom) <= message.top)
        assertTrue(message.bottom <= history.top)
        compose.onNodeWithText("Download and install").assertDoesNotExist()
        compose.onNodeWithText("Cancel").assertDoesNotExist()
    }

    @Test
    @Config(qualifiers = "w320dp-h800dp")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun appUpdateDetailsAndCheckButtonFitWithLargeTextOnNarrowScreens() {
        val model = model()
        model.chooseTextSize(200)
        compose.setContent { MathTheme(textSizePercent = model.textSizePercent) { SettingsScreen(model) } }

        val heading = compose.onNodeWithText("App updates").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val check = compose.onNodeWithText("Check for updates").assertIsDisplayed().assertIsEnabled()
            .fetchSemanticsNode().boundsInRoot
        assertTrue(heading.right <= check.left)
        listOf("App updates", "Installed: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", "Check for updates").forEach {
            compose.onNodeWithText(it).assertIsDisplayed()
            val layout = textLayout(it)
            assertFalse("$it overflows horizontally", layout.didOverflowWidth)
            assertFalse("$it overflows vertically", layout.didOverflowHeight)
        }
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
        compose.onNodeWithTag("submit-setup").performScrollTo().performClick()
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

    @Test fun textSizeDefaultsToNormalAndIsRemembered() {
        val model = model()
        assertEquals(100, model.textSizePercent)
        model.chooseTextSize(140)
        assertEquals(140, model.textSizePercent)
        val restored = model()
        assertEquals(140, restored.textSizePercent)
        restored.chooseTextSize(100)
        assertEquals(100, model().textSizePercent)
    }

    @Test fun textSizeIsClampedWhenChangedOrRestored() {
        val model = model()
        listOf(Int.MIN_VALUE to 80, 79 to 80, 201 to 200, Int.MAX_VALUE to 200).forEach { (invalid, expected) ->
            model.chooseTextSize(invalid)
            assertEquals(expected, model.textSizePercent)
            assertEquals(expected, model().textSizePercent)
            application.getSharedPreferences("settings", 0).edit().putInt("text_size_percent", invalid).commit()
            assertEquals(expected, model().textSizePercent)
        }
    }

    @Test fun textSizeRespectsSystemFontScaleWithoutChangingLayoutDensity() {
        val model = model()
        val systemDensity = Density(2f, 1.2f)
        var appliedDensity: Density? = null
        var appliedTypography: Typography? = null
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides systemDensity) {
                MathTheme(textSizePercent = model.textSizePercent) {
                    val density = LocalDensity.current
                    val typography = MaterialTheme.typography
                    SideEffect {
                        appliedDensity = density
                        appliedTypography = typography
                    }
                }
            }
        }
        compose.runOnIdle { assertSame(systemDensity, appliedDensity) }
        listOf(150, 200, 80, 100).forEach { percent ->
            compose.runOnIdle { model.chooseTextSize(percent) }
            compose.runOnIdle {
                assertSame(systemDensity, appliedDensity)
                val scale = percent / 100f
                val typography = requireNotNull(appliedTypography)
                assertEquals(Typography().bodyLarge.fontSize * scale, typography.bodyLarge.fontSize)
                assertEquals(Typography().bodyLarge.lineHeight * scale, typography.bodyLarge.lineHeight)
                assertEquals(Typography().bodyLarge.letterSpacing * scale, typography.bodyLarge.letterSpacing)
            }
        }
        compose.runOnIdle { assertSame(systemDensity, appliedDensity) }
    }

    @Test fun settingsSliderUpdatesTextImmediatelyAndKeepsPracticeUsable() {
        val model = model()
        model.updateQuestionCount("1")
        compose.setContent { MathAppContent(model) }
        assertEquals(Typography().titleLarge.fontSize, textLayout("What would you like to practice?").layoutInput.style.fontSize)
        compose.onNodeWithContentDescription("Settings").performClick()
        val slider = compose.onNodeWithContentDescription("Text size")
        slider.performScrollTo().assertRangeInfoEquals(ProgressBarRangeInfo(100f, 80f..200f, 11))
        listOf(150, 80, 100, 160, 170, 180, 190, 200).forEach { percent ->
            slider.performScrollTo().performSemanticsAction(SemanticsActions.SetProgress) { assertTrue(it(percent.toFloat())) }
            val rangeInfo = slider.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
            assertEquals(percent.toFloat(), rangeInfo.current, 0.001f)
            assertEquals(80f..200f, rangeInfo.range)
            assertEquals(11, rangeInfo.steps)
            slider.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "$percent%"))
            compose.onNodeWithText("Text size: $percent%").performScrollTo().assertIsDisplayed()
            assertEquals(Typography().bodyLarge.fontSize * (percent / 100f),
                textLayout("Text size: $percent%").layoutInput.style.fontSize)
            assertEquals(percent, model.textSizePercent)
            assertEquals(percent, model().textSizePercent)
        }
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithTag("operation-ADDITION").performScrollTo().performClick()
        assertFalse(textLayout("+").didOverflowHeight)
        compose.onNodeWithTag("submit-setup").performScrollTo().performClick()
        compose.onNodeWithTag("start-button").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithTag("problem").performScrollTo().assertIsDisplayed()
        assertEquals(Typography().displayMedium.fontSize * 2f, textLayout("${model.game!!.problem.text} = ?").layoutInput.style.fontSize)
        compose.onNodeWithTag("answer-input").performScrollTo().performTextReplacement((model.game!!.problem.answer + 1).toString())
        compose.onNodeWithTag("submit-answer").performScrollTo().performClick()
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 0 right!")
        assertEquals(Typography().titleLarge.fontSize * 2f, textLayout("You got 0 right!").layoutInput.style.fontSize)
        compose.onNodeWithTag("done-button").assertIsDisplayed().performClick()
        compose.onNodeWithContentDescription("Settings").performClick()
        slider.performScrollTo().assertRangeInfoEquals(ProgressBarRangeInfo(200f, 80f..200f, 11))
    }

    @Test fun textSizeSliderSupportsOneContinuousDrag() {
        val model = model()
        model.chooseTextSize(80)
        model.openSettings()
        compose.setContent { MathAppContent(model) }
        val slider = compose.onNodeWithContentDescription("Text size")
        slider.performScrollTo().assertIsDisplayed().performTouchInput {
            down(Offset(24f, centerY))
            moveTo(Offset(width * 0.25f, centerY))
            moveTo(center)
        }
        compose.runOnIdle { assertTrue("Size after first drag: ${model.textSizePercent}", model.textSizePercent in 90..190) }
        slider.performTouchInput {
            moveTo(centerRight)
            up()
        }
        compose.runOnIdle { assertEquals(200, model.textSizePercent) }
    }

    @Test fun textSizeAppliesInsideConfirmationDialogs() {
        val model = model()
        model.selectOperation(Operation.ADDITION)
        model.submitSetup()
        model.start()
        model.chooseTextSize(200)
        compose.setContent { MathAppContent(model) }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Quit this practice?").assertIsDisplayed()
        assertEquals(Typography().headlineSmall.fontSize * 2f,
            textLayout("Quit this practice?").layoutInput.style.fontSize)
        compose.onNodeWithText("Keep practicing").performClick()
        assertEquals(Screen.PLAYING, model.screen)
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
        compose.onNodeWithTag("answer-input").performTextReplacement(given.toString())
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("submit-answer").performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText("Congratulations!").assertIsDisplayed()
        compose.onNodeWithText("You answered every question!").assertIsDisplayed()
        assertNotNull(model.celebration)
        assertNull(model.earlyFinishMessage)
        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.autoAdvance = true

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
        compose.onNodeWithText("Addition · 1 to 10").performClick()
        compose.onNodeWithText("${problem.text} = $given").assertIsDisplayed()
        compose.onNodeWithText("Correct answer:", substring = true).assertDoesNotExist()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithContentDescription("Show correct answers").performClick()
        compose.onNodeWithTag("practice-history").performClick()
        compose.onNodeWithText("Addition · 1 to 10").performClick()
        compose.onNodeWithText("Correct answer: ${problem.answer}").assertIsDisplayed()
    }

    @Test fun setupShowsMinimumBeforeMaximumAndAQuestionCountHeading() {
        val model = model()
        compose.setContent { MathAppContent(model) }
        val minimumHeading = compose.onNodeWithText("What is the minimum number?")
        val minimumInput = compose.onNodeWithTag("minimum-input")
        val maximumHeading = compose.onNodeWithText("What is the maximum number?")
        maximumHeading.performScrollTo().assertIsDisplayed()
        minimumHeading.assertIsDisplayed()
        minimumInput.assertIsDisplayed()
        assertTrue(minimumHeading.fetchSemanticsNode().boundsInRoot.bottom <=
            minimumInput.fetchSemanticsNode().boundsInRoot.top)
        assertTrue(minimumInput.fetchSemanticsNode().boundsInRoot.bottom <=
            maximumHeading.fetchSemanticsNode().boundsInRoot.top)
        compose.onNodeWithTag("question-count-input").performScrollTo().assertIsDisplayed()
        val questionHeading = compose.onNodeWithText("How many questions would you like?")
        questionHeading.assertIsDisplayed()
        assertTrue(questionHeading.fetchSemanticsNode().boundsInRoot.bottom <=
            compose.onNodeWithTag("question-count-input").fetchSemanticsNode().boundsInRoot.top)
    }

    @Test fun minimumIsValidatedAndRemembered() {
        val model = model()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("operation-ADDITION").performClick()
        compose.onNodeWithTag("minimum-input").assertTextContains("1")
        listOf("", "0", "-1", "1.5", "abc", "11", (MAX_MAXIMUM + 1).toString(), "999999").forEach { invalid ->
            compose.onNodeWithTag("minimum-input").performScrollTo().performTextReplacement(invalid)
            compose.onNodeWithTag("submit-setup").assertIsNotEnabled()
            compose.runOnIdle {
                model.submitSetup()
                model.start()
            }
            assertEquals(Screen.SETUP, model.screen)
            assertNull(model.game)
        }
        compose.onNodeWithTag("minimum-input").performTextReplacement("7")
        compose.onNodeWithTag("maximum-input").performScrollTo().performTextReplacement("6")
        compose.onNodeWithTag("submit-setup").assertIsNotEnabled()
        compose.onNodeWithTag("question-count-input").performScrollTo().performImeAction()
        assertEquals(Screen.SETUP, model.screen)
        compose.onNodeWithTag("maximum-input").performScrollTo().performTextReplacement("7")
        compose.onNodeWithTag("submit-setup").performScrollTo().assertIsEnabled().performClick()
        assertEquals(7, model().minimum)
        assertEquals(7, model().maximum)
        compose.onNodeWithTag("start-button").performClick()
        assertEquals(Problem(7, 7, Operation.ADDITION), model.game!!.problem)
    }

    @Test fun questionCountIsValidatedAndRemembered() {
        val model = model()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("operation-ADDITION").performClick()
        compose.onNodeWithTag("question-count-input").assertTextContains(DEFAULT_QUESTION_COUNT.toString())
        listOf("", "0", "-1", "1.5", "abc", (MAX_QUESTION_COUNT + 1).toString(), "999999").forEach { invalid ->
            compose.onNodeWithTag("question-count-input").performScrollTo().performTextReplacement(invalid)
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
        assertNull(model.earlyFinishMessage)
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

    @Test fun reachingQuestionCountWithMistakesCelebratesAndShowsResults() {
        val model = model()
        model.selectOperation(Operation.DIVISION)
        model.updateQuestionCount("2")
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        answer(model.game!!.problem.answer + 1)
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 2 of 2")
        assertNull(model.celebration)
        compose.onNodeWithTag("answer-input").performTextReplacement(model.game!!.problem.answer.toString())
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("submit-answer").performClick()
        compose.mainClock.advanceTimeBy(64)
        assertNotNull(model.celebration)
        assertNull(model.earlyFinishMessage)
        compose.onNodeWithText("Congratulations!").assertIsDisplayed()
        compose.onNodeWithText("You answered every question!").assertIsDisplayed()
        compose.onNodeWithText("You answered every question correctly!").assertDoesNotExist()
        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.autoAdvance = true
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 1 right!")
        compose.onNodeWithText("Congratulations!").assertDoesNotExist()
        assertNull(model.celebration)
        assertEquals(2, model.lastResult!!.attempts.size)
        assertEquals(1, model.lastResult!!.wrong)
        compose.waitUntil(5_000) { model.history.size == 1 }
        assertEquals(model.lastResult, HistoryStore(File(application.filesDir, "practice_history.json")).load().single())
    }

    @Test fun fifthWrongAnswerOnTheLastQuestionCelebratesInsteadOfStoppingEarly() {
        val model = model()
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount("6")
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        answer(model.game!!.problem.answer)
        repeat(MAX_WRONG_ANSWERS - 1) { answer(model.game!!.problem.answer + 1) }
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 6 of 6")
        assertNull(model.celebration)
        assertNull(model.earlyFinishMessage)
        compose.onNodeWithTag("answer-input").performTextReplacement((model.game!!.problem.answer + 1).toString())
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("submit-answer").performClick()
        compose.mainClock.advanceTimeBy(64)

        assertNotNull(model.celebration)
        assertNull(model.earlyFinishMessage)
        compose.onNodeWithText("Congratulations!").assertIsDisplayed()
        compose.onNodeWithText("Nice try!", substring = true).assertDoesNotExist()
        assertEquals(6, model.lastResult!!.attempts.size)
        assertEquals(MAX_WRONG_ANSWERS, model.lastResult!!.wrong)
        compose.waitUntil(5_000) { model.history.size == 1 }
        assertEquals(model.lastResult, HistoryStore(File(application.filesDir, "practice_history.json")).load().single())
        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.autoAdvance = true
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 1 right!")
    }

    @Test fun fifthWrongAnswerBeforeTheLastQuestionShowsZeroScoreAndDoesNotReplay() {
        val model = model()
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount("6")
        model.submitSetup()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("start-button").performClick()
        repeat(MAX_WRONG_ANSWERS) { answer(model.game!!.problem.answer + 1) }

        assertNull(model.celebration)
        assertEquals(5, model.lastResult!!.attempts.size)
        compose.onNode(isDialog()).assertIsDisplayed()
        compose.onNodeWithText("Nice try! You got 0 right out of 6").assertIsDisplayed()
        compose.onNodeWithTag("view-results").performClick()
        compose.onNodeWithTag("result-correct").assertTextEquals("You got 0 right!")
        compose.onNodeWithContentDescription("Settings").performClick()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNode(isDialog()).assertDoesNotExist()
        compose.onNodeWithTag("done-button").performClick()
        assertNull(model.earlyFinishMessage)
        compose.onNodeWithTag("submit-setup").performScrollTo().performClick()
        compose.onNodeWithTag("start-button").performClick()
        compose.onNodeWithTag("question-progress").assertTextEquals("Question 1 of 6")
        compose.onNode(isDialog()).assertDoesNotExist()
    }

    private fun textLayout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { assertTrue(it(results)) }
        return results.single()
    }

    private fun answer(value: Int) {
        compose.onNodeWithTag("answer-input").performTextReplacement(value.toString())
        compose.onNodeWithTag("submit-answer").performClick()
        compose.waitForIdle()
    }
}
