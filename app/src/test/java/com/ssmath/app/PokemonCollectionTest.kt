package com.ssmath.app

import android.app.Application
import android.os.Looper
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
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
class PokemonCollectionTest {
    private val application get() = ApplicationProvider.getApplicationContext<Application>()
    private val file get() = File(application.filesDir, "practice_history.json")
    private val blockedWrite get() = File(application.filesDir, "practice_history.json.tmp")
    private val models = mutableListOf<ViewModelStore>()
    private var now = 1_000L

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

    private fun model(
        chosen: Celebration = Celebration.PIKACHU,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    ): MathViewModel {
        val seed = (0..1_000).first { Celebration.entries.random(Random(it)) == chosen }
        return MathViewModel(application, clock = { now }, ioDispatcher = dispatcher,
            celebrationRandom = Random(seed)).also {
            models += ViewModelStore().apply { put("model", it) }
            shadowOf(Looper.getMainLooper()).idle()
        }
    }

    private fun start(model: MathViewModel, count: Int = 1) {
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount(count.toString())
        model.submitSetup()
        model.start()
    }

    private fun answer(model: MathViewModel, correct: Boolean = true) {
        model.updateAnswer((model.game!!.problem.answer + if (correct) 0 else 1).toString())
        model.submitAnswer()
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test fun allThirteenPokemonsHaveExactLabelsAndStableStoredNames() {
        assertEquals("Pokémons", CelebrationCategory.POKEMONS.label)
        assertEquals(listOf("Pikachu", "Squirtle", "Bulbasaur", "Charmander", "Jigglypuff",
            "Palafin", "Finizen", "Wailmer", "Wailord", "Bouffalant", "Veluza", "Mantyke", "Mantine"),
            Celebration.pokemons.map { it.label })
        assertEquals(18, Celebration.entries.size)
        Celebration.pokemons.forEach {
            assertEquals("\"${it.name}\"", Json.encodeToString(Celebration.serializer(), it))
            assertEquals(it, Json.decodeFromString(Celebration.serializer(), "\"${it.name}\""))
        }
    }

    @Test fun eachPresentedPokemonIsCollectedOnceEvenWithRewardsDisabled() {
        Celebration.pokemons.forEachIndexed { index, pokemon ->
            val model = model(pokemon)
            model.chooseRewardsEnabled(false)
            start(model)
            answer(model)
            assertEquals(pokemon, model.celebration)
            assertFalse(pokemon in model.pokemons)
            repeat(3) { model.collectPresentedCelebration(pokemon) }
            assertEquals(index + 1, model.pokemons.size)
            model.dismissCelebration()
            model.done()
            assertEquals(model.pokemons, model().pokemons)
            assertTrue(model.rewardBalances.isEmpty())
        }
        assertEquals(Celebration.pokemons.toSet(), HistoryStore(file).loadSnapshot().pokemons)
    }

    @Test fun ordinaryOrHiddenCelebrationsAndEarlyFinishesDoNotCollect() {
        Celebration.entries.filter { it.category != CelebrationCategory.POKEMONS }.forEach { ordinary ->
            val model = model(ordinary)
            start(model)
            answer(model)
            model.collectPresentedCelebration(ordinary)
            model.collectPresentedCelebration(Celebration.PIKACHU)
            assertTrue(model.pokemons.isEmpty())
        }
        val model = model()
        start(model, count = 10)
        repeat(5) { answer(model, correct = false) }
        assertNull(model.celebration)
        model.collectPresentedCelebration(Celebration.PIKACHU)
        assertTrue(model.pokemons.isEmpty())
        model.chooseShowTimer(true)
        model.chooseTimeLimitMinutes(5)
        start(model)
        now += 300_000L
        assertTrue(model.checkTimeLimit())
        model.collectPresentedCelebration(Celebration.PIKACHU)
        assertTrue(model.pokemons.isEmpty())
        start(model)
        answer(model)
        model.openPokemons()
        model.collectPresentedCelebration(Celebration.PIKACHU)
        assertTrue(model.pokemons.isEmpty())
    }

    @Test fun galleryPausesPracticeAndReturnsThroughSettings() {
        val model = model()
        start(model, count = 10)
        now += 1_000
        model.openSettings()
        model.openPokemons()
        now += 5_000
        assertEquals(1_000L, model.elapsedMs())
        model.updateAnswer("123")
        assertEquals("", model.answerText)
        model.closeOverlay()
        assertEquals(Overlay.SETTINGS, model.overlay)
        now += 5_000
        assertEquals(1_000L, model.elapsedMs())
        model.closeOverlay()
        now += 1_000
        assertEquals(2_000L, model.elapsedMs())
    }

    @Test fun lateCollectionSerializesWithClaimAndHistoryClearAfterDismissal() {
        val dispatcher = QueuedDispatcher()
        val model = model(dispatcher = dispatcher)
        dispatcher.drain()
        start(model, count = 25)
        repeat(25) { answer(model) }
        repeat(3) { model.collectPresentedCelebration(Celebration.PIKACHU) }
        model.dismissCelebration()
        model.claimReward()
        model.clearHistory()
        dispatcher.drain()
        assertNull(model.celebration)
        assertEquals(setOf(Celebration.PIKACHU), model.pokemons)
        assertEquals(1, model.rewardBalances.values.sumOf { it.totalFragments })
        assertTrue(model.history.isEmpty())
        val restored = model()
        assertEquals(model.pokemons, restored.pokemons)
        assertEquals(model.rewardBalances, restored.rewardBalances)
    }

    @Test fun failedCollectionIsNotReportedAsCollectedAndCanBeRetried() {
        val model = model()
        start(model)
        answer(model)
        val before = file.readText()
        assertTrue(blockedWrite.mkdir())
        model.collectPresentedCelebration(Celebration.PIKACHU)
        assertTrue(model.pokemons.isEmpty())
        assertNotNull(model.message)
        assertEquals(before, file.readText())
        assertTrue(blockedWrite.delete())
        model.collectPresentedCelebration(Celebration.PIKACHU)
        assertEquals(setOf(Celebration.PIKACHU), model().pokemons)
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
