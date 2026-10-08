package com.ssmath.app

import android.app.Application
import android.os.Looper
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
@LooperMode(LooperMode.Mode.PAUSED)
class RewardsModelTest {
    private val application get() = ApplicationProvider.getApplicationContext<Application>()
    private val file get() = File(application.filesDir, "practice_history.json")
    private val blockedWrite get() = File(application.filesDir, "practice_history.json.tmp")
    private val models = mutableListOf<ViewModelStore>()
    private var now = 1_000L
    private var wallTime = 1_700_000_000_000L

    @Before fun setup() {
        application.getSharedPreferences("settings", 0).edit().clear().commit()
        file.delete()
        blockedWrite.deleteRecursively()
        DebugLog.initialize(application)
    }

    @After fun cleanup() {
        models.forEach { it.clear() }
        shadowOf(Looper.getMainLooper()).idle()
        blockedWrite.deleteRecursively()
        file.delete()
    }

    private fun model(dispatcher: CoroutineDispatcher = Dispatchers.Unconfined): MathViewModel {
        val model = MathViewModel(application, ProblemGenerator(Random(7)), clock = { now },
            wallClock = { wallTime }, ioDispatcher = dispatcher, rewardRandom = Random(3))
        models += ViewModelStore().apply { put("model", model) }
        shadowOf(Looper.getMainLooper()).idle()
        return model
    }

    private fun start(model: MathViewModel, count: Int = 26, rewards: Boolean = true, minutes: Int = 0) {
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount(count.toString())
        model.chooseRewardsEnabled(rewards)
        model.chooseTimeLimitMinutes(minutes)
        model.submitSetup()
        model.start()
    }

    private fun answer(model: MathViewModel, correct: Boolean = true) {
        model.updateAnswer((model.game!!.problem.answer + if (correct) 0 else 1).toString())
        model.submitAnswer()
    }

    private fun finish(model: MathViewModel, count: Int = 26, wrong: Int = 0) {
        repeat(count) { answer(model, it < count - wrong) }
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test fun rewardsDefaultOnAndSettingsPersistWithValidTimeSteps() {
        val first = model()
        assertTrue(first.rewardsEnabled)
        assertEquals(0, first.timeLimitMinutes)
        assertNull(first.activeTimeLimitMs)
        first.chooseRewardsEnabled(false)
        first.chooseTimeLimitMinutes(15)
        val second = model()
        assertFalse(second.rewardsEnabled)
        assertEquals(15, second.timeLimitMinutes)
        second.chooseRewardsEnabled(true)
        assertTrue(model().rewardsEnabled)
        listOf(-5 to 0, 4 to 0, 7 to 5, 60 to 60, 100 to 60).forEach { (input, expected) ->
            second.chooseTimeLimitMinutes(input)
            assertEquals(expected, second.timeLimitMinutes)
            assertEquals(expected, model().timeLimitMinutes)
        }
    }

    @Test fun celebrationPrecedesThePrizeAndClaimIsIdempotentUntilExplicitDismissal() {
        val model = model()
        start(model)
        finish(model)
        assertNotNull(model.celebration)
        assertFalse(model.rewardDialogVisible)
        assertNotNull(model.rewardResult?.prizeType)
        assertNull(model.history.single().prize)
        model.claimReward()
        assertTrue(model.rewardBalances.isEmpty())
        model.dismissCelebration()
        assertTrue(model.rewardDialogVisible)
        repeat(3) { model.claimReward() }
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
        assertNotNull(model.lastResult?.prize)
        assertEquals(model.lastResult, model.rewardResult)
        assertEquals(model.lastResult, model.history.single())
        assertTrue(model.rewardDialogVisible)
        model.dismissReward()
        assertFalse(model.rewardDialogVisible)
        model.done()
        assertEquals(1, model.history.size)
    }

    @Test fun pendingHistoryPrizeCanBeClaimedAfterReloadWithoutChangingUnrelatedLastResult() {
        val first = model()
        start(first)
        finish(first)
        val pending = first.history.single()
        first.done()
        val restored = model()
        assertFalse(restored.rewardDialogVisible)
        restored.openHistory()
        restored.showHistoryDetail(restored.history.single())
        restored.showRewardForResult(pending)
        assertEquals(pending, restored.rewardResult)
        assertTrue(restored.rewardDialogVisible)
        restored.claimReward()
        assertNull(restored.lastResult)
        assertNotNull(restored.historyDetail?.prize)
        assertEquals(1, restored.rewardBalances.values.sumOf { it.totalFragments })
        val again = model()
        again.showRewardForResult(pending)
        repeat(3) { again.claimReward() }
        assertEquals(1, again.rewardBalances.values.sumOf { it.totalFragments })
        assertNotNull(again.rewardResult?.prize)
    }

    @Test fun automaticPrizeDialogDoesNotAppearOverUnrelatedHistoryOrSettings() {
        val model = model()
        start(model)
        finish(model)
        model.openHistory()
        model.dismissCelebration()
        assertFalse(model.rewardDialogVisible)
        model.closeOverlay()
        assertEquals(Overlay.SETTINGS, model.overlay)
        assertFalse(model.rewardDialogVisible)
        model.closeOverlay()
        assertTrue(model.rewardDialogVisible)
        model.dismissReward()
        model.openHistory()
        model.showRewardForResult(model.history.single())
        assertTrue(model.rewardDialogVisible)
        model.openSettings()
        assertFalse(model.rewardDialogVisible)
        model.openHistory()
        assertFalse(model.rewardDialogVisible)
    }

    @Test fun countAndAccuracyBoundariesAreAppliedToCompletedPractices() {
        val model = model()
        listOf(Triple(24, 0, false), Triple(25, 0, true), Triple(25, 2, true), Triple(25, 3, false),
            Triple(26, 2, true), Triple(49, 4, true), Triple(50, 0, true), Triple(50, 4, true),
            Triple(50, 5, false), Triple(51, 4, true), Triple(100, 10, false)).forEach { (count, wrong, eligible) ->
            start(model, count)
            finish(model, count, wrong)
            assertEquals(count, model.lastResult!!.questionCount)
            assertEquals(eligible, model.lastResult!!.prizeType != null)
            if (eligible) {
                val type = model.lastResult!!.prizeType!!
                if (count >= 50) assertEquals(RewardType.VIDEO_GAME, type)
                else assertNotEquals(RewardType.VIDEO_GAME, type)
                model.dismissCelebration()
                model.claimReward()
                assertEquals(type, model.lastResult!!.prize!!.type)
                assertEquals(model.lastResult, HistoryStore(file).load().first())
                assertEquals(model.rewardBalances, HistoryStore(file).loadSnapshot().rewards)
            }
            model.done()
        }
    }

    @Test fun rewardsMustBeEnabledAtBothStartAndFinish() {
        val model = model()
        model.chooseShowTimer(true)
        start(model, rewards = false, minutes = 5)
        model.chooseRewardsEnabled(true)
        assertNull(model.activeTimeLimitMs)
        finish(model)
        assertNull(model.lastResult!!.prizeType)
        start(model)
        model.chooseRewardsEnabled(false)
        finish(model)
        assertNull(model.lastResult!!.prizeType)
        start(model, count = 100)
        repeat(10) { answer(model, correct = false) }
        assertEquals(Screen.RESULTS, model.screen)
        assertEquals(10, model.lastResult!!.attempts.size)
        assertNull(model.lastResult!!.prizeType)
    }

    @Test fun timeoutIsCheckedBeforeParsingAndCapsDurationAtTheExactLimit() {
        val model = model()
        model.chooseShowTimer(true)
        start(model, minutes = 5)
        now += 299_999
        assertFalse(model.checkTimeLimit())
        model.updateAnswer("")
        now++
        model.submitAnswer()
        assertEquals(Screen.RESULTS, model.screen)
        assertTrue(model.lastResult!!.timedOut)
        assertEquals(300_000L, model.lastResult!!.durationMs)
        assertTrue(model.lastResult!!.attempts.isEmpty())
        assertNull(model.celebration)
        assertNull(model.lastResult!!.prizeType)
        assertFalse(model.rewardDialogVisible)
        assertFalse(model.checkTimeLimit())
        model.submitAnswer()
        assertEquals(1, model.history.size)
        assertTrue(HistoryStore(file).load().single().timedOut)
    }

    @Test fun timerCoroutineFinishesPracticeWithoutAnyInput() {
        val model = model()
        model.chooseShowTimer(true)
        start(model, minutes = 5)
        now += 400_000
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200))
        assertEquals(Screen.RESULTS, model.screen)
        assertTrue(model.lastResult!!.timedOut)
        assertEquals(300_000L, model.lastResult!!.durationMs)
        assertEquals(1, model.history.size)
    }

    @Test fun limitAndInputPauseInEveryOverlayAndInTheBackground() {
        val model = model()
        model.chooseShowTimer(true)
        start(model, minutes = 5)
        now += 1_000
        val initialAnswer = model.game!!.problem.answer.toString()
        model.updateAnswer(initialAnswer)
        listOf<() -> Unit>(model::openSettings, model::openHistory, model::openRewards).forEach { open ->
            open()
            now += 400_000
            model.updateAnswer("99")
            model.submitAnswer()
            assertEquals(initialAnswer, model.answerText)
            assertTrue(model.game!!.attempts.isEmpty())
            assertFalse(model.checkTimeLimit())
            assertEquals(1_000L, model.elapsedMs())
            while (model.overlay != null) model.closeOverlay()
        }
        model.setForeground(false)
        now += 400_000
        model.updateAnswer("99")
        model.submitAnswer()
        assertFalse(model.checkTimeLimit())
        assertEquals(initialAnswer, model.answerText)
        assertEquals(1_000L, model.elapsedMs())
        model.setForeground(true)
        now += 298_999
        assertFalse(model.checkTimeLimit())
        now++
        assertTrue(model.checkTimeLimit())
        assertEquals(300_000L, model.lastResult!!.durationMs)
    }

    @Test fun timeLimitIsCapturedAtStartAndCannotBeDisabledByHidingTheTimer() {
        val model = model()
        model.chooseShowTimer(true)
        start(model, minutes = 5)
        assertEquals(300_000L, model.activeTimeLimitMs)
        model.chooseShowTimer(false)
        model.chooseTimeLimitMinutes(60)
        model.chooseRewardsEnabled(false)
        now += 300_000
        assertTrue(model.checkTimeLimit())
        assertTrue(model.lastResult!!.timedOut)
        start(model, minutes = 5)
        assertNull(model.activeTimeLimitMs)
        model.chooseShowTimer(true)
        now += 400_000
        assertFalse(model.checkTimeLimit())
        model.backToSetup()
        assertNull(model.activeTimeLimitMs)
    }

    @Test fun saveAndClaimFailuresLeaveAPendingPrizeThatCanBeRetriedExactlyOnce() {
        val model = model()
        start(model)
        assertTrue(blockedWrite.mkdir())
        finish(model)
        assertTrue(model.history.isEmpty())
        assertNotNull(model.message)
        model.dismissCelebration()
        model.claimReward()
        assertNotNull(model.rewardError)
        assertNull(model.rewardResult?.prize)
        assertTrue(model.rewardBalances.isEmpty())
        assertTrue(blockedWrite.delete())
        model.claimReward()
        assertNull(model.rewardError)
        assertNotNull(model.rewardResult?.prize)
        assertEquals(1, model.history.size)
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
        model.claimReward()
        assertEquals(model.history, HistoryStore(file).load())
        assertEquals(model.rewardBalances, HistoryStore(file).loadSnapshot().rewards)
    }

    @Test fun claimFailureDoesNotUpdateDisplayedOrPersistedBalances() {
        val model = model()
        start(model)
        finish(model)
        model.dismissCelebration()
        val before = file.readText()
        assertTrue(blockedWrite.mkdir())
        model.claimReward()
        assertNotNull(model.rewardError)
        assertFalse(model.claimingReward)
        assertTrue(model.rewardBalances.isEmpty())
        assertNull(model.lastResult!!.prize)
        assertEquals(before, file.readText())
        assertTrue(blockedWrite.delete())
        model.claimReward()
        assertNotNull(model.lastResult!!.prize)
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
    }

    @Test fun claimsWaitForResultSavingAndRepeatedTapsDoNotDuplicateIt() {
        val dispatcher = QueuedDispatcher()
        val model = model(dispatcher)
        start(model)
        finish(model)
        model.dismissCelebration()
        repeat(4) { model.claimReward() }
        assertTrue(model.claimingReward)
        assertTrue(model.history.isEmpty())
        dispatcher.drain()
        assertFalse(model.claimingReward)
        assertNotNull(model.lastResult!!.prize)
        assertEquals(1, model.history.size)
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
    }

    @Test fun lateSavesAndClaimsDoNotReopenResultsAfterNavigation() {
        val dispatcher = QueuedDispatcher()
        val model = model(dispatcher)
        start(model)
        finish(model)
        model.dismissCelebration()
        model.claimReward()
        model.done()
        dispatcher.drain()
        assertEquals(Screen.SETUP, model.screen)
        assertNull(model.lastResult)
        assertNull(model.rewardResult)
        assertFalse(model.rewardDialogVisible)
        assertEquals(1, model.history.size)
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
        start(model)
        finish(model)
        model.backToSetup()
        dispatcher.drain()
        assertEquals(Screen.SETUP, model.screen)
        assertNull(model.lastResult)
        assertNull(model.rewardResult)
        assertEquals(2, model.history.size)
    }

    @Test fun clockRollbackAndLateSavesKeepResultIdsUniqueAndTheNewestScreenIntact() {
        val original = model()
        start(original)
        finish(original)
        val firstId = original.lastResult!!.id
        wallTime -= 100_000
        val dispatcher = QueuedDispatcher()
        val restored = model(dispatcher)
        start(restored)
        finish(restored)
        restored.done()
        start(restored, count = 50)
        finish(restored, count = 50)
        restored.dismissCelebration()
        restored.claimReward()
        dispatcher.drain()
        assertEquals(3, restored.history.map { it.id }.toSet().size)
        assertTrue(restored.history.filter { it.id != firstId }.all { it.id > firstId })
        assertEquals(50, restored.lastResult!!.questionCount)
        assertEquals(RewardType.VIDEO_GAME, restored.rewardResult!!.prize!!.type)
        assertEquals(1, restored.rewardBalances.values.sumOf { it.totalFragments })
    }

    @Test fun deletingAndClearingResultsRemovesTheirRewardsThroughTheModel() {
        val model = model()
        start(model)
        finish(model)
        model.dismissCelebration()
        model.claimReward()
        val deleted = model.lastResult!!
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
        model.deleteResult(model.lastResult!!.id)
        assertTrue(model.history.isEmpty())
        assertEquals(0, model.rewardBalances.values.sumOf { it.totalFragments })
        assertNull(model.lastResult!!.prize)
        assertNull(model.lastResult!!.prizeType)
        model.showRewardForResult(deleted)
        assertNull(model.rewardResult)
        assertEquals(model.rewardBalances, model().rewardBalances)
        model.done()
        start(model)
        finish(model)
        model.dismissCelebration()
        model.claimReward()
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
        model.clearHistory()
        assertEquals(0, model().rewardBalances.values.sumOf { it.totalFragments })
        assertNull(model.lastResult!!.prize)
        assertNull(model.rewardResult)
    }

    @Test fun unreadableInventoryReportsAnErrorAndCannotBeCleared() {
        file.writeText("""{"history":[],"rewards":{"LOLLIPOP":{"whole":1,"fragments":9}}}""")
        val before = file.readText()
        val model = model()
        assertNotNull(model.message)
        model.clearHistory()
        assertNotNull(model.message)
        assertEquals(before, file.readText())
    }

    @Test fun deletionAndClearSerializeWithPendingClaimsInEitherOrder() {
        listOf(false, true).forEach { clear ->
            listOf(false, true).forEach { claimFirst ->
                val dispatcher = QueuedDispatcher()
                val model = model(dispatcher)
                dispatcher.drain()
                start(model)
                finish(model)
                dispatcher.drain()
                model.dismissCelebration()
                val id = model.lastResult!!.id
                if (claimFirst) model.claimReward()
                if (clear) model.clearHistory() else model.deleteResult(id)
                if (!claimFirst) model.claimReward()
                dispatcher.drain()
                assertTrue(model.history.isEmpty())
                assertEquals(0, model.rewardBalances.values.sumOf { it.totalFragments })
                assertEquals(model.rewardBalances, HistoryStore(file).loadSnapshot().rewards)
                assertNull(model.rewardResult)
                assertNull(model.lastResult!!.prizeType)
                assertFalse(model.claimingReward)
            }
        }
    }

    @Test fun usingRewardsWorksWhenEarningIsDisabledAndSurvivesReload() {
        collectLollipops()
        val model = model()
        model.chooseRewardsEnabled(false)
        val history = model.history
        assertFalse(model.rewardsEnabled)
        model.openRewards()
        model.useReward(RewardType.LOLLIPOP)
        assertFalse(model.usingReward)
        assertNull(model.rewardUseError)
        assertEquals(RewardBalance(1, 2), model.rewardBalances[RewardType.LOLLIPOP])
        assertEquals(history, model.history)
        assertEquals(model.rewardBalances, model().rewardBalances)
        model.clearHistory()
        assertEquals(RewardBalance(), model().rewardBalances[RewardType.LOLLIPOP])
    }

    @Test fun useRequiresTheRewardsPageAndAnAvailableWholeReward() {
        collectLollipops()
        val model = model()
        model.useReward(RewardType.LOLLIPOP)
        assertEquals(RewardBalance(2, 2), model.rewardBalances[RewardType.LOLLIPOP])
        model.openRewards()
        model.useReward(RewardType.VIDEO_GAME)
        repeat(4) { model.useReward(RewardType.LOLLIPOP) }
        assertEquals(RewardBalance(0, 2), model.rewardBalances[RewardType.LOLLIPOP])
        assertNull(model.rewardUseError)
        assertEquals(model.rewardBalances, model().rewardBalances)
    }

    @Test fun rapidUseRequestsOnlyConsumeOnceAndSerializeWithHistoryChanges() {
        collectLollipops()
        val dispatcher = QueuedDispatcher()
        val model = model(dispatcher)
        dispatcher.drain()
        model.openRewards()
        dispatcher.drain()
        repeat(4) { model.useReward(RewardType.LOLLIPOP) }
        assertTrue(model.usingReward)
        assertEquals(RewardBalance(2, 2), model.rewardBalances[RewardType.LOLLIPOP])
        model.clearHistory()
        model.closeOverlay()
        dispatcher.drain()
        assertFalse(model.usingReward)
        assertEquals(Overlay.SETTINGS, model.overlay)
        assertTrue(model.history.isEmpty())
        assertEquals(RewardBalance(), model.rewardBalances[RewardType.LOLLIPOP])
        assertEquals(model.rewardBalances, model().rewardBalances)
    }

    @Test fun failedUseDoesNotChangeBalancesAndAllowsRetry() {
        collectLollipops()
        val model = model()
        model.openRewards()
        val before = file.readText()
        assertTrue(blockedWrite.mkdir())
        repeat(2) {
            model.useReward(RewardType.LOLLIPOP)
            assertFalse(model.usingReward)
            assertNotNull(model.rewardUseError)
            assertEquals(RewardBalance(2, 2), model.rewardBalances[RewardType.LOLLIPOP])
            assertEquals(before, file.readText())
        }
        assertTrue(blockedWrite.delete())
        model.useReward(RewardType.LOLLIPOP)
        assertNull(model.rewardUseError)
        assertEquals(RewardBalance(1, 2), model.rewardBalances[RewardType.LOLLIPOP])
    }

    private fun collectLollipops() {
        val store = HistoryStore(file)
        repeat(8) {
            val result = PracticeResult(it + 1L, it + 1L, Operation.ADDITION, 10, 1_000,
                List(26) { Attempt(Problem(1, 1, Operation.ADDITION), 2) },
                prizeType = RewardType.LOLLIPOP)
            store.add(result)
            store.claimReward(result.id)
        }
    }

    private class QueuedDispatcher : CoroutineDispatcher() {
        private val tasks = ArrayDeque<Runnable>()
        override fun dispatch(context: CoroutineContext, block: Runnable) { tasks.addLast(block) }
        fun drain() {
            shadowOf(Looper.getMainLooper()).idle()
            while (tasks.isNotEmpty()) {
                tasks.removeFirst().run()
                shadowOf(Looper.getMainLooper()).idle()
            }
        }
    }
}
