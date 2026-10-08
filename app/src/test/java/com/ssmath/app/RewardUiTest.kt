package com.ssmath.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp")
class RewardUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Before fun controlAnimationClock() {
        compose.mainClock.autoAdvance = false
    }

    @Test fun allTenIllustrationsExposeAccurateImageDescriptions() {
        compose.setContent {
            MathTheme {
                Column {
                    RewardType.entries.forEach { type ->
                        Row {
                            RewardImage(type, false, Modifier.size(90.dp))
                            RewardImage(type, true, Modifier.size(90.dp))
                        }
                    }
                }
            }
        }
        compose.mainClock.advanceTimeByFrame()
        RewardType.entries.forEach { type ->
            compose.onNodeWithContentDescription(type.label).assertIsDisplayed()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            compose.onNodeWithContentDescription(rewardImageDescription(type, true)).assertIsDisplayed()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
        }
        compose.onNodeWithContentDescription("Ice Cream Cone").assertExists()
        compose.onNodeWithContentDescription("One third of an ice cream cone").assertExists()
        compose.onNodeWithContentDescription("One third of a video game controller").assertExists()
    }

    @Test fun giftIsAnAccessibleButtonAndRapidTapsOnlyOpenOnce() {
        var opens = 0
        compose.setContent {
            MathTheme { RewardGiftDialog(result(), false, null, { opens++ }, {}) }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText("Congratulations! You've answered 96.67% correct! Tap to get a prize!")
            .assertIsDisplayed()
        val gift = compose.onNodeWithTag("gift-box")
        gift.assertContentDescriptionEquals("Open gift box")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertHasClickAction()
        gift.performClick()
        gift.performClick()
        compose.mainClock.advanceTimeBy(REWARD_OPEN_DURATION_MS.toLong() + 64)
        gift.assertIsNotEnabled()
        compose.onNodeWithText("Saving your prize…").assertIsDisplayed()
        compose.onNodeWithTag("awarded-fragment").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, opens) }
    }

    @Test fun anEligibleFractionalScoreIsNotTruncatedToNinetyPercent() =
        assertScoreText(901, 1_000, "90.1")

    @Test fun anEligibleScoreJustAboveNinetyKeepsTwoDecimalPlaces() =
        assertScoreText(892, 991, "90.01")

    @Test fun claimingDisablesTheBoxWithoutPrematurelyRevealingAnAward() {
        var opens = 0
        compose.setContent {
            MathTheme { RewardGiftDialog(result(), true, null, { opens++ }, {}) }
        }
        compose.mainClock.advanceTimeBy(REWARD_OPEN_DURATION_MS.toLong() + 64)
        compose.onNodeWithTag("gift-box").assertIsNotEnabled().performClick()
        compose.onNodeWithTag("awarded-fragment").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, opens) }
    }

    @Test fun successfulPersistenceRevealsTheFragmentAndUpdatedCarryBalancePermanently() {
        var saved by mutableStateOf(result())
        var claiming by mutableStateOf(false)
        var opens = 0
        var dismissed = false
        compose.setContent {
            MathTheme {
                RewardGiftDialog(saved, claiming, null, { opens++; claiming = true }, { dismissed = true })
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("gift-box").performClick()
        compose.runOnIdle {
            saved = saved.copy(prize = PrizeAward(RewardType.LOLLIPOP, RewardBalance(2, 0)))
            claiming = false
        }
        compose.mainClock.advanceTimeBy(REWARD_OPEN_DURATION_MS.toLong() + 64)
        compose.onNodeWithTag("awarded-fragment")
            .assertContentDescriptionEquals("One third of a lollipop").assertIsDisplayed()
        compose.onNodeWithText("Lollipop Fragment").assertIsDisplayed()
        compose.onNodeWithTag("awarded-balance").assertTextEquals("Whole: 2 · Fragments: 0/3")
        compose.onNodeWithTag("gift-box").assertHasNoClickAction()
        compose.mainClock.advanceTimeBy(12_000)
        compose.onNodeWithTag("awarded-fragment").assertIsDisplayed()
        compose.onNodeWithText("View results").performClick()
        compose.runOnIdle {
            assertEquals(1, opens)
            assertTrue(dismissed)
        }
    }

    @Test fun aPersistenceErrorOffersOneRetryAndCanStillBeDismissed() {
        var claiming by mutableStateOf(false)
        var error by mutableStateOf<String?>(null)
        var opens = 0
        var dismissed = false
        compose.setContent {
            MathTheme {
                RewardGiftDialog(result(), claiming, error,
                    { opens++; claiming = true; error = null }, { dismissed = true })
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("gift-box").performClick()
        compose.runOnIdle {
            claiming = false
            error = "Couldn't save your prize. Please try again."
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText(error!!).assertIsDisplayed()
        compose.onNodeWithTag("awarded-fragment").assertDoesNotExist()
        compose.onNodeWithText("Retry").performClick()
        compose.onNodeWithTag("gift-box").performClick()
        compose.runOnIdle { assertEquals(2, opens) }
        compose.onNodeWithText("Not now").performClick()
        compose.runOnIdle { assertTrue(dismissed) }
    }

    @Test fun repeatedImmediateFailuresWithTheSameMessageRemainRetryable() {
        var claiming by mutableStateOf(false)
        var error by mutableStateOf<String?>(null)
        var opens = 0
        val failure = "Couldn't save your prize. Please try again."
        compose.setContent {
            MathTheme {
                RewardGiftDialog(result(), claiming, error, {
                    opens++
                    claiming = true
                    error = null
                    claiming = false
                    error = failure
                }, {})
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("gift-box").performClick()
        compose.mainClock.advanceTimeBy(64)
        val retry = compose.onNodeWithText("Retry").assertIsEnabled()
            .fetchSemanticsNode().config[SemanticsActions.OnClick].action!!
        compose.runOnIdle {
            retry()
            retry()
            assertEquals("Rapid callbacks must not duplicate the second attempt", 2, opens)
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText(failure).assertIsDisplayed()
        compose.onNodeWithText("Retry").assertIsEnabled().performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText("Retry").assertIsEnabled()
        compose.onNodeWithTag("awarded-fragment").assertDoesNotExist()
        compose.runOnIdle { assertEquals(3, opens) }
    }

    @Test fun anUnclaimedGiftCanBeDismissedWithoutRequestingAPrize() {
        var opens = 0
        var dismissed by mutableStateOf(false)
        compose.setContent {
            MathTheme {
                if (dismissed) Text("Results")
                else RewardGiftDialog(result(), false, null, { opens++ }, { dismissed = true })
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText("Not now").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithText("Results").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, opens) }
    }

    @Test fun openingWaitsInTheBackgroundAndResumesWithoutLosingTheAward() {
        var saved by mutableStateOf(result())
        compose.setContent {
            MathTheme { RewardGiftDialog(saved, false, null, {}, {}) }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.runOnIdle {
            saved = saved.copy(prize = PrizeAward(RewardType.LOLLIPOP, RewardBalance(0, 1)))
        }
        compose.mainClock.advanceTimeBy(200)
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.mainClock.advanceTimeBy(4_000)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("awarded-fragment").assertDoesNotExist()
        compose.mainClock.advanceTimeBy(REWARD_OPEN_DURATION_MS.toLong())
        compose.onNodeWithTag("awarded-fragment").assertIsDisplayed()
    }

    @Test fun inventoryScrollsThroughAllWholeAndFragmentCountsAtDoubleTextSize() {
        val balances = RewardType.entries.mapIndexed { index, type ->
            type to RewardBalance(index + 1, index % 3)
        }.toMap()
        compose.setContent {
            MathTheme {
                CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 2f)) {
                    RewardInventory(balances)
                }
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText("3 fragments = 1 reward").assertIsDisplayed()
        RewardType.entries.forEach { type ->
            val balance = balances.getValue(type)
            compose.onNodeWithTag("reward-inventory").performScrollToKey(type.name)
            compose.mainClock.advanceTimeByFrame()
            compose.onNodeWithTag("inventory-whole-${type.name}").onChildren()
                .filterToOne(hasText("Whole: ${balance.whole}")).assertIsDisplayed()
            compose.onNodeWithTag("inventory-fragments-${type.name}").onChildren()
                .filterToOne(hasText("Fragments: ${balance.fragments}/3")).assertIsDisplayed()
            compose.onNodeWithContentDescription(type.label).assertIsDisplayed()
            compose.onNodeWithContentDescription(rewardImageDescription(type, true)).assertIsDisplayed()
        }
    }

    @Test
    @Config(qualifiers = "w320dp-h480dp")
    fun giftRemainsReachableOnASmallScreenAtDoubleTextSize() {
        var opens = 0
        var dismissed = false
        compose.setContent {
            MathTheme {
                CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 2f)) {
                    RewardGiftDialog(result(), false, null, { opens++ }, { dismissed = true })
                }
            }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("gift-box").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Not now").assertIsDisplayed().performClick()
        compose.runOnIdle {
            assertEquals(1, opens)
            assertTrue(dismissed)
        }
    }

    @Test fun emptyInventoryStillShowsEveryRewardWithZeroCounts() {
        compose.setContent { MathTheme { RewardInventory(emptyMap()) } }
        compose.mainClock.advanceTimeBy(64)
        RewardType.entries.forEach { type ->
            compose.onNodeWithTag("reward-inventory").performScrollToKey(type.name)
            compose.mainClock.advanceTimeByFrame()
            compose.onNodeWithTag("inventory-whole-${type.name}").onChildren()
                .filterToOne(hasText("Whole: 0")).assertIsDisplayed()
            compose.onNodeWithTag("inventory-fragments-${type.name}").onChildren()
                .filterToOne(hasText("Fragments: 0/3")).assertIsDisplayed()
        }
    }

    private fun assertScoreText(correct: Int, count: Int, expected: String) {
        val fractional = result().copy(
            attempts = List(count) { Attempt(Problem(1, 1, Operation.ADDITION), if (it < correct) 2 else 3) },
            questionCount = count,
            prizeType = RewardType.VIDEO_GAME
        )
        compose.setContent {
            MathTheme { RewardGiftDialog(fractional, false, null, {}, {}) }
        }
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithText("Congratulations! You've answered $expected% correct! Tap to get a prize!")
            .assertIsDisplayed()
    }

    private fun result() = PracticeResult(
        id = 7, finishedAt = 7, operation = Operation.ADDITION, maximum = 10, durationMs = 1_000,
        attempts = List(30) { Attempt(Problem(1, 1, Operation.ADDITION), if (it == 0) 3 else 2) },
        prizeType = RewardType.LOLLIPOP
    )
}
