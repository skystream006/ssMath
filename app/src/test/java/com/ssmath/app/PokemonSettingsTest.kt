package com.ssmath.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import java.io.File
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import org.junit.After
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
class PokemonSettingsTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val application get() = ApplicationProvider.getApplicationContext<Application>()
    private val file get() = File(application.filesDir, "practice_history.json")
    private val models = ViewModelStore()

    @Before fun setup() {
        application.getSharedPreferences("settings", 0).edit().clear().commit()
        file.delete()
        DebugLog.initialize(application)
    }

    @After fun cleanup() {
        compose.runOnIdle { models.clear() }
        file.delete()
    }

    private fun model(): MathViewModel {
        val seed = (0..1_000).first { Celebration.entries.random(Random(it)) == Celebration.PIKACHU }
        return MathViewModel(application, ioDispatcher = Dispatchers.Main.immediate,
            celebrationRandom = Random(seed)).also { models.put("model", it) }
    }

    @Test fun emptyCollectionIsBelowRewardsWithACountAndReturnsToSettings() {
        val model = model()
        model.openSettings()
        compose.setContent { MathAppContent(model) }
        val rewards = compose.onNodeWithTag("my-rewards").fetchSemanticsNode().boundsInRoot
        val pokemons = compose.onNodeWithTag("my-pokemons").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val history = compose.onNodeWithTag("practice-history").fetchSemanticsNode().boundsInRoot
        assertTrue(rewards.bottom <= pokemons.top)
        assertTrue(pokemons.bottom <= history.top)
        compose.onNodeWithTag("my-pokemons").performClick()
        assertEquals(Overlay.POKEMONS, model.overlay)
        compose.onNodeWithText("My Pokémons").assertIsDisplayed()
        compose.onNodeWithTag("pokemon-count").assertTextEquals("0/13")
        compose.onNodeWithText("Finish practices", substring = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Back").performClick()
        assertEquals(Overlay.SETTINGS, model.overlay)
    }

    @Test fun collectedImageReplaysAndClosesWithoutChangingPracticeOrInventory() {
        val store = HistoryStore(file)
        repeat(2) { store.collectPokemon(Celebration.PALAFIN) }
        val before = store.loadSnapshot()
        val model = model()
        model.chooseRewardsEnabled(false)
        model.openPokemons()
        compose.mainClock.autoAdvance = false
        compose.setContent { MathAppContent(model) }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("pokemon-count").assertTextEquals("1/13")
        compose.onAllNodesWithTag("pokemon-PALAFIN").assertCountEquals(1)
        compose.onNodeWithTag("pokemon-PIKACHU").assertDoesNotExist()
        compose.onNodeWithTag("pokemon-PALAFIN").assert(hasText("Palafin")).performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PALAFIN").assertIsDisplayed()
        compose.onNodeWithText("You answered every question!").assertDoesNotExist()
        compose.onNodeWithTag("close-celebration").performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PALAFIN").assertDoesNotExist()
        compose.onNodeWithTag("pokemon-PALAFIN").performClick()
        compose.mainClock.advanceTimeBy(CELEBRATION_DURATION_MS + 128L)
        compose.onNodeWithTag("celebration-PALAFIN").assertDoesNotExist()
        compose.onNodeWithTag("pokemon-count").assertTextEquals("1/13")
        assertEquals(Overlay.POKEMONS, model.overlay)
        assertEquals(Screen.SETUP, model.screen)
        assertNull(model.celebration)
        assertNull(model.rewardResult)
        assertEquals(before, store.loadSnapshot())
    }

    @Test fun presentingAPokemonAddsItBeforeSkippingTheAnimation() {
        val model = model()
        model.chooseRewardsEnabled(false)
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount("1")
        model.submitSetup()
        model.start()
        compose.mainClock.autoAdvance = false
        compose.setContent { MathAppContent(model) }
        compose.runOnIdle {
            model.updateAnswer(model.game!!.problem.answer.toString())
            model.submitAnswer()
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PIKACHU").assertIsDisplayed()
        assertEquals(setOf(Celebration.PIKACHU), model.pokemons)
        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.runOnIdle {
            model.done()
            model.openSettings()
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("my-pokemons").performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("pokemon-count").assertTextEquals("1/13")
        compose.onNodeWithTag("pokemon-PIKACHU").assert(hasText("Pikachu")).assertIsDisplayed()
        assertEquals(setOf(Celebration.PIKACHU), HistoryStore(file).loadSnapshot().pokemons)
    }

    @Test
    @Config(qualifiers = "w320dp-h800dp")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun allCollectedNamesAreReadableAndScrollableWithLargeText() {
        Celebration.pokemons.forEach { HistoryStore(file).collectPokemon(it) }
        val model = model()
        model.chooseTextSize(200)
        model.openPokemons()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("pokemon-count").assertTextEquals("13/13")
        Celebration.pokemons.forEach { pokemon ->
            compose.onNodeWithTag("pokemon-gallery").performScrollToKey(pokemon.name)
            compose.onNodeWithText(pokemon.label).assertIsDisplayed()
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                    val layouts = mutableListOf<TextLayoutResult>()
                    action(layouts)
                    assertFalse("${pokemon.label} overflows", layouts.single().hasVisualOverflow)
                }
        }
    }
}
