package com.ssmath.app

import kotlin.random.Random
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class RewardsTest {
    private fun prize(count: Int, correct: Int = count, answered: Int = count,
        atStart: Boolean = true, atFinish: Boolean = true, timedOut: Boolean = false, seed: Int = 1) =
        selectPrize(count, correct, answered, atStart, atFinish, timedOut, Random(seed))

    @Test fun questionCountsMapToNamedTiersAtTheBoundaries() {
        listOf(Int.MIN_VALUE, 0, 1, 24).forEach { assertNull(rewardTierForQuestionCount(it)) }
        (25..49).forEach { assertEquals(RewardTier.TIER_1, rewardTierForQuestionCount(it)) }
        listOf(50, 51, 100, 1000, Int.MAX_VALUE).forEach {
            assertEquals(RewardTier.TIER_2, rewardTierForQuestionCount(it))
        }
        assertEquals("Tier 1", RewardTier.TIER_1.label)
        assertEquals("25–49 questions", RewardTier.TIER_1.questionCountLabel)
        assertEquals("Tier 2", RewardTier.TIER_2.label)
        assertEquals("50 or more questions", RewardTier.TIER_2.questionCountLabel)
    }

    @Test fun rewardTypesBelongToTheirAssignedTier() {
        assertEquals(
            setOf(RewardType.LOLLIPOP, RewardType.ICE_CREAM, RewardType.GUMMI_BEAR, RewardType.RAMEN,
                RewardType.BED_TIME),
            RewardType.entries.filter { it.tier == RewardTier.TIER_1 }.toSet()
        )
        assertEquals(setOf(RewardType.VIDEO_GAME, RewardType.RESTAURANT),
            RewardType.entries.filter { it.tier == RewardTier.TIER_2 }.toSet())
    }

    @Test fun tierMetadataDoesNotChangeStoredRewardNames() {
        RewardType.entries.forEach { type ->
            val stored = "\"${type.name}\""
            assertEquals(stored, Json.encodeToString(RewardType.serializer(), type))
            assertEquals(type, Json.decodeFromString(RewardType.serializer(), stored))
        }
    }

    @Test fun countBoundariesSelectTheRightCategory() {
        listOf(1, 24).forEach { assertNull(prize(it)) }
        listOf(25, 26, 49).forEach { count ->
            val selected = (1..100).map { prize(count, seed = it) }.toSet()
            assertEquals(RewardType.entries.filter { it.tier == RewardTier.TIER_1 }.toSet(), selected)
            assertTrue(selected.all { it?.tier == RewardTier.TIER_1 })
        }
        listOf(50, 51, 100, 1000).forEach { count ->
            val selected = (1..100).map { prize(count, seed = it) }.toSet()
            assertEquals(RewardType.entries.filter { it.tier == RewardTier.TIER_2 }.toSet(), selected)
            assertTrue(selected.all { it?.tier == RewardTier.TIER_2 })
        }
    }

    @Test fun accuracyMustBeStrictlyOverNinetyPercentWithoutRounding() {
        assertNull(prize(30, 27))
        assertNull(prize(50, 45))
        assertNull(prize(100, 90))
        assertNotNull(prize(100, 91))
        assertNotNull(prize(25, 23))
        assertNull(prize(25, 22))
        assertNotNull(prize(26, 24))
        assertNull(prize(26, 23))
        assertNotNull(prize(49, 45))
        assertNull(prize(49, 44))
        assertNotNull(prize(50, 46))
    }

    @Test fun rewardsRequireBothSettingsChecksEveryQuestionAndNoTimeout() {
        listOf(25, 26, 50).forEach { count ->
            assertNull(prize(count, atStart = false))
            assertNull(prize(count, atFinish = false))
            assertNull(prize(count, atStart = false, atFinish = false))
            assertNull(prize(count, correct = count - 1, answered = count - 1))
            assertNull(prize(count, correct = count + 1, answered = count + 1))
            assertNull(prize(count, timedOut = true))
        }
    }

    @Test fun everyThirdFragmentBecomesAWholeRewardWithZeroRemainder() {
        var balance = RewardBalance()
        repeat(9) {
            balance = balance.addFragment()
            assertEquals(it + 1, balance.totalFragments)
            assertEquals((it + 1) / 3, balance.whole)
            assertEquals((it + 1) % 3, balance.fragments)
        }
        assertEquals(RewardBalance(3, 0), balance)
    }

    @Test fun invalidBalancesAreRejectedRatherThanLosingInventory() {
        listOf(-1 to 0, 0 to -1, 0 to 3, Int.MAX_VALUE to 0).forEach { (whole, fragments) ->
            assertThrows(IllegalArgumentException::class.java) { RewardBalance(whole, fragments) }
        }
    }

    @Test fun removingFragmentsUnpacksWholeRewardsAndClampsSpentBalancesAtZero() {
        assertEquals(RewardBalance(0, 2), RewardBalance(1, 0).removeFragments(1))
        assertEquals(RewardBalance(1, 1), RewardBalance(2, 2).removeFragments(4))
        assertEquals(RewardBalance(), RewardBalance().removeFragments(1))
        assertEquals(RewardBalance(), RewardBalance(1, 0).removeFragments(Int.MAX_VALUE))
        assertEquals(RewardBalance(2, 2), RewardBalance(2, 2).removeFragments(0))
        assertThrows(IllegalArgumentException::class.java) { RewardBalance().removeFragments(-1) }
    }

    @Test fun rewardTypesHaveSingularPluralAndFragmentLabels() {
        assertEquals("Ice Cream Cone", RewardType.ICE_CREAM.label)
        assertEquals("Ice Cream Cones", RewardType.ICE_CREAM.pluralLabel)
        assertEquals("Ice Cream Cone Fragment", RewardType.ICE_CREAM.fragmentLabel)
        assertEquals("Bed Time", RewardType.BED_TIME.label)
        assertEquals("Restaurant", RewardType.RESTAURANT.label)
        RewardType.entries.forEach {
            assertTrue(it.label.isNotBlank())
            assertTrue(it.pluralLabel.isNotBlank())
            assertEquals("${it.label} Fragment", it.fragmentLabel)
        }
    }
}
