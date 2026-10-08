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

    private fun model(chosen: Celebration = Celebration.PIKACHU, questionCount: Int = 15): MathViewModel {
        val seed = (0..1_000).first { Celebration.select(questionCount, Random(it)) == chosen }
        return MathViewModel(application, ioDispatcher = Dispatchers.Main.immediate,
            celebrationRandom = Random(seed)).also { models.put("model", it) }
    }

    @Test fun emptyCollectionIsBelowRewardsWithSectionCountsAndReturnsToSettings() {
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
        compose.onNodeWithTag("pokemon-count").assertDoesNotExist()
        assertSectionCount(CelebrationCategory.POKEMONS, "0/13")
        assertSectionCount(CelebrationCategory.OTHER, "0/5")
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
        assertSectionCount(CelebrationCategory.POKEMONS, "1/13")
        assertSectionCount(CelebrationCategory.OTHER, "0/5")
        compose.onAllNodesWithTag("pokemon-PALAFIN").assertCountEquals(1)
        compose.onNodeWithTag("pokemon-PIKACHU").assertDoesNotExist()
        compose.onNodeWithTag("pokemon-PALAFIN").assert(hasText("Palafin")).performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PALAFIN").assertIsDisplayed()
        compose.onNodeWithText("You answered every question!").assertDoesNotExist()
        compose.onNodeWithTag("close-celebration").performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PALAFIN").assertDoesNotExist()
        compose.onNodeWithTag("pokemon-PALAFIN").performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PALAFIN").assertIsDisplayed()
        compose.mainClock.advanceTimeBy(CELEBRATION_DURATION_MS.toLong())
        compose.onNodeWithTag("celebration-PALAFIN").assertDoesNotExist()
        assertSectionCount(CelebrationCategory.POKEMONS, "1/13")
        assertSectionCount(CelebrationCategory.OTHER, "0/5")
        assertEquals(Overlay.POKEMONS, model.overlay)
        assertEquals(Screen.SETUP, model.screen)
        assertNull(model.celebration)
        assertNull(model.rewardResult)
        assertEquals(before, store.loadSnapshot())
    }

    @Test fun presentingAPokemonAddsItBeforeSkippingTheAnimation() =
        presentationCollects(Celebration.PIKACHU, questionCount = 15)

    @Test fun presentingAnOtherCelebrationAfterAShortPracticeCollectsIt() =
        presentationCollects(Celebration.PARTY, questionCount = 1)

    private fun presentationCollects(celebration: Celebration, questionCount: Int) {
        val model = model(celebration, questionCount)
        model.chooseRewardsEnabled(false)
        model.selectOperation(Operation.ADDITION)
        model.updateQuestionCount(questionCount.toString())
        model.submitSetup()
        model.start()
        compose.mainClock.autoAdvance = false
        compose.setContent { MathAppContent(model) }
        compose.runOnIdle {
            repeat(questionCount) {
                model.updateAnswer(model.game!!.problem.answer.toString())
                model.submitAnswer()
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-${celebration.name}").assertIsDisplayed()
        assertEquals(setOf(celebration), model.pokemons)
        compose.onNodeWithTag("view-results").performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-${celebration.name}").assertDoesNotExist()
        assertNull(model.celebration)
        assertEquals(Screen.RESULTS, model.screen)
        compose.mainClock.autoAdvance = true
        compose.runOnIdle {
            model.done()
            model.openSettings()
        }
        compose.onNodeWithTag("my-pokemons").performClick()
        assertSectionCount(CelebrationCategory.POKEMONS,
            if (celebration.category == CelebrationCategory.POKEMONS) "1/13" else "0/13")
        assertSectionCount(CelebrationCategory.OTHER,
            if (celebration.category == CelebrationCategory.OTHER) "1/5" else "0/5")
        compose.onNodeWithText(celebration.category.label).assertIsDisplayed()
        compose.onNodeWithTag("pokemon-${celebration.name}").assert(hasText(celebration.label)).assertIsDisplayed()
        assertEquals(setOf(celebration), HistoryStore(file).loadSnapshot().pokemons)
    }

    @Test fun otherCelebrationsHaveTheirOwnCategoryAndCanReplayWithoutChangingInventory() {
        val store = HistoryStore(file)
        store.collectPokemon(Celebration.PIKACHU)
        store.collectPokemon(Celebration.PARTY)
        val before = store.loadSnapshot()
        val model = model()
        model.openPokemons()
        compose.mainClock.autoAdvance = false
        compose.setContent { MathAppContent(model) }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("pokemon-count").assertDoesNotExist()
        assertSectionCount(CelebrationCategory.POKEMONS, "1/13")
        assertSectionCount(CelebrationCategory.OTHER, "1/5")
        compose.onNodeWithTag("pokemon-count-POKEMONS")
            .assertContentDescriptionEquals("1 of 13 collected in Pokémons")
        compose.onNodeWithTag("pokemon-count-OTHER")
            .assertContentDescriptionEquals("1 of 5 collected in Other")
        compose.onNodeWithText("Pokémons").assertIsDisplayed()
        compose.onNodeWithText("Other").assertIsDisplayed()
        compose.onNodeWithTag("pokemon-PARTY").performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PARTY").assertIsDisplayed()
        compose.onNodeWithText("You answered every question!").assertDoesNotExist()
        compose.onNodeWithTag("close-celebration").performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("celebration-PARTY").assertDoesNotExist()
        assertEquals(before, store.loadSnapshot())
        assertEquals(Screen.SETUP, model.screen)
        assertNull(model.celebration)
        assertNull(model.rewardResult)
    }

    @Test fun sevenDescriptionTapsUnlockAllAnimationsWithoutEnablingLoggingOrDuplicatingEntries() {
        val store = HistoryStore(file)
        store.collectPokemon(Celebration.SQUIRTLE)
        val before = store.loadSnapshot()
        val model = model()
        model.chooseRewardsEnabled(false)
        model.openSettings()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithText("Debug logging").performScrollTo().performClick()
        val description = compose.onNodeWithTag("debug-logging-description").performScrollTo()
        repeat(6) { description.performClick() }
        assertEquals(before.pokemons, model.pokemons)
        assertEquals(before, store.loadSnapshot())
        description.performClick()
        val expected = before.copy(pokemons = Celebration.entries.toSet())
        assertEquals(expected.pokemons, model.pokemons)
        assertEquals(expected, store.loadSnapshot())
        repeat(7) { description.performClick() }
        assertEquals(expected, store.loadSnapshot())
        assertFalse(DebugLog.enabled.value)
        compose.onNodeWithContentDescription("Debug logging").performScrollTo().assertIsOff()
        compose.onNodeWithTag("my-pokemons").performScrollTo().performClick()
        assertSectionCount(CelebrationCategory.POKEMONS, "13/13")
        assertSectionCount(CelebrationCategory.OTHER, "5/5")
    }

    @Test
    @Config(qualifiers = "w320dp-h800dp")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun allCollectedNamesAreReadableAndScrollableWithLargeText() {
        Celebration.entries.forEach { HistoryStore(file).collectPokemon(it) }
        val model = model()
        model.chooseTextSize(200)
        model.openPokemons()
        compose.setContent { MathAppContent(model) }
        compose.onNodeWithTag("pokemon-count").assertDoesNotExist()
        assertTextFits("My Pokémons")
        CelebrationCategory.entries.forEach { category ->
            val fraction = if (category == CelebrationCategory.POKEMONS) "13/13" else "5/5"
            assertSectionCount(category, fraction)
            assertTextFits(category.label)
            assertTextFits(fraction)
        }
        Celebration.entries.forEach { pokemon ->
            compose.onNodeWithTag("pokemon-gallery").performScrollToKey(pokemon.name)
            assertTextFits(pokemon.label)
        }
    }

    private fun assertSectionCount(category: CelebrationCategory, fraction: String) {
        compose.onNodeWithTag("pokemon-gallery").performScrollToKey("category-${category.name}")
        val heading = compose.onNodeWithText(category.label).assertIsDisplayed()
            .fetchSemanticsNode().boundsInRoot
        val count = compose.onNodeWithTag("pokemon-count-${category.name}").assertTextEquals(fraction)
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertTrue(heading.right <= count.left)
        assertEquals(heading.center.y, count.center.y, 1f)
    }

    private fun assertTextFits(text: String) {
        compose.onNodeWithText(text, useUnmergedTree = true).assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                val layouts = mutableListOf<TextLayoutResult>()
                action(layouts)
                val layout = layouts.single()
                assertFalse("$text overflows horizontally: ${layout.size.width} < ${layout.multiParagraph.width}",
                    layout.didOverflowWidth)
                assertFalse("$text overflows vertically: ${layout.size.height} < ${layout.multiParagraph.height}",
                    layout.didOverflowHeight)
            }
    }
}
