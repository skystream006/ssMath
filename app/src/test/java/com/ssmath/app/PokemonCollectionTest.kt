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
        val seed = (0..10_000).first { Celebration.entries.random(Random(it)) == chosen }
        return MathViewModel(application, clock = { now }, ioDispatcher = dispatcher,
            celebrationRandom = Random(seed)).also {
            models += ViewModelStore().apply { put("model", it) }
            shadowOf(Looper.getMainLooper()).idle()
        }
    }

    private fun start(model: MathViewModel, count: Int = 15) {
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

    @Test fun originalPokemonsKeepExactLabelsAndAllStoredNamesAreStable() {
        assertEquals("Pokémons", CelebrationCategory.POKEMONS.label)
        assertEquals(listOf("Pikachu", "Squirtle", "Bulbasaur", "Charmander", "Jigglypuff",
            "Palafin", "Finizen", "Wailmer", "Wailord", "Bouffalant", "Veluza", "Mantyke", "Mantine"),
            Celebration.pokemons.take(13).map { it.label })
        assertEquals(162, Celebration.entries.size)
        assertEquals("Other", CelebrationCategory.OTHER.label)
        assertEquals(setOf(Celebration.DOLPHINS, Celebration.WHALES, Celebration.ANCHOVIES,
            Celebration.PARTY, Celebration.CANDY_SHOWER),
            Celebration.entries.filter { it.category == CelebrationCategory.OTHER }.toSet())
        Celebration.entries.forEach {
            assertEquals("\"${it.name}\"", Json.encodeToString(Celebration.serializer(), it))
            assertEquals(it, Json.decodeFromString(Celebration.serializer(), "\"${it.name}\""))
        }
    }

    @Test fun pokemonSelectionStartsAtFifteenQuestions() {
        (1..14).forEach { count ->
            val selected = (0..200).map { Celebration.select(count, Random(it)) }.toSet()
            assertEquals(Celebration.entries.filter { it.category == CelebrationCategory.OTHER }.toSet(), selected)
        }
        listOf(15, 16, 25, 1000).forEach { count ->
            assertEquals(Celebration.entries.toSet(),
                (0..10_000).map { Celebration.select(count, Random(it)) }.toSet())
        }
    }

    @Test fun originalPokemonsKeepNationalPokedexNumbersAndPaddedCollectionLabels() {
        val expected = mapOf(
            Celebration.PIKACHU to (25 to "#0025 Pikachu"),
            Celebration.SQUIRTLE to (7 to "#0007 Squirtle"),
            Celebration.BULBASAUR to (1 to "#0001 Bulbasaur"),
            Celebration.CHARMANDER to (4 to "#0004 Charmander"),
            Celebration.JIGGLYPUFF to (39 to "#0039 Jigglypuff"),
            Celebration.PALAFIN to (964 to "#0964 Palafin"),
            Celebration.FINIZEN to (963 to "#0963 Finizen"),
            Celebration.WAILMER to (320 to "#0320 Wailmer"),
            Celebration.WAILORD to (321 to "#0321 Wailord"),
            Celebration.BOUFFALANT to (626 to "#0626 Bouffalant"),
            Celebration.VELUZA to (976 to "#0976 Veluza"),
            Celebration.MANTYKE to (458 to "#0458 Mantyke"),
            Celebration.MANTINE to (226 to "#0226 Mantine")
        )
        assertEquals(Celebration.pokemons.take(13).toSet(), expected.keys)
        expected.forEach { (pokemon, values) ->
            assertEquals(pokemon.name, values.first, pokemon.ndex)
            assertEquals(values.second, pokemon.collectionLabel)
        }
    }

    @Test fun otherCelebrationsKeepTheirNamesWithoutNationalPokedexNumbers() {
        Celebration.entries.filter { it.category == CelebrationCategory.OTHER }.forEach {
            assertNull(it.ndex)
            assertEquals(it.label, it.collectionLabel)
        }
    }

    @Test fun shortPracticesOnlyPresentAndCollectOtherCelebrationsEvenAfterUnlockingEverything() {
        val model = model()
        model.collectAllCelebrations()
        listOf(1, 14).forEach { count ->
            start(model, count)
            repeat(count) { answer(model) }
            val presented = model.celebration!!
            assertEquals(CelebrationCategory.OTHER, presented.category)
            model.collectPresentedCelebration(presented)
            assertEquals(Celebration.entries.toSet(), model.pokemons)
            model.done()
        }
    }

    @Test fun eachPresentedCelebrationIsCollectedOnceEvenWithRewardsDisabled() {
        Celebration.entries.forEachIndexed { index, pokemon ->
            val model = model(pokemon)
            model.chooseRewardsEnabled(false)
            start(model)
            repeat(15) { answer(model, correct = it != 0) }
            assertEquals(pokemon, model.celebration)
            assertFalse(pokemon in model.pokemons)
            repeat(3) { model.collectPresentedCelebration(pokemon) }
            assertEquals(index + 1, model.pokemons.size)
            model.dismissCelebration()
            model.done()
            assertEquals(model.pokemons, model().pokemons)
            assertTrue(model.rewardBalances.isEmpty())
        }
        assertEquals(Celebration.entries.toSet(), HistoryStore(file).loadSnapshot().pokemons)
    }

    @Test fun hiddenOrMismatchedCelebrationsAndEarlyFinishesDoNotCollect() {
        val model = model()
        start(model)
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
        repeat(15) { answer(model) }
        assertEquals(Celebration.PIKACHU, model.celebration)
        model.collectPresentedCelebration(Celebration.PARTY)
        assertTrue(model.pokemons.isEmpty())
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
        assertEquals(0, model.rewardBalances.values.sumOf { it.totalFragments })
        assertTrue(model.history.isEmpty())
        val restored = model()
        assertEquals(model.pokemons, restored.pokemons)
        assertEquals(model.rewardBalances, restored.rewardBalances)
    }

    @Test fun failedCollectionIsNotReportedAsCollectedAndCanBeRetried() {
        val model = model()
        start(model)
        repeat(15) { answer(model) }
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

    @Test fun bulkCollectionSerializesWithPendingResultsClaimsAndHistoryClear() {
        val dispatcher = QueuedDispatcher()
        val model = model(dispatcher = dispatcher)
        dispatcher.drain()
        start(model, count = 25)
        repeat(25) { answer(model) }
        model.collectPresentedCelebration(Celebration.PIKACHU)
        model.dismissCelebration()
        model.claimReward()
        repeat(2) { model.collectAllCelebrations() }
        model.clearHistory()
        dispatcher.drain()
        assertEquals(Celebration.entries.toSet(), model.pokemons)
        assertEquals(0, model.rewardBalances.values.sumOf { it.totalFragments })
        assertTrue(model.history.isEmpty())
        assertEquals(model.pokemons, model().pokemons)
    }

    @Test fun failedBulkCollectionPreservesExistingEntriesAndCanBeRetried() {
        HistoryStore(file).collectPokemon(Celebration.PARTY)
        val model = model()
        val before = file.readText()
        assertTrue(blockedWrite.mkdir())
        model.collectAllCelebrations()
        assertEquals(setOf(Celebration.PARTY), model.pokemons)
        assertNotNull(model.message)
        assertEquals(before, file.readText())
        assertTrue(blockedWrite.delete())
        model.collectAllCelebrations()
        assertEquals(Celebration.entries.toSet(), model().pokemons)
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
