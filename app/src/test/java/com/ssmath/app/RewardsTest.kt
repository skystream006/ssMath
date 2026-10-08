package com.ssmath.app

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class RewardsTest {
    private fun prize(count: Int, correct: Int = count, answered: Int = count,
        atStart: Boolean = true, atFinish: Boolean = true, timedOut: Boolean = false, seed: Int = 1) =
        selectPrize(count, correct, answered, atStart, atFinish, timedOut, Random(seed))

    @Test fun countBoundariesSelectTheRightCategory() {
        assertNull(prize(25))
        listOf(26, 49).forEach { count ->
            val selected = (1..100).map { prize(count, seed = it) }.toSet()
            assertEquals(RewardType.entries.filter { it != RewardType.VIDEO_GAME }.toSet(), selected)
        }
        listOf(50, 51, 100, 1000).forEach { assertEquals(RewardType.VIDEO_GAME, prize(it)) }
    }

    @Test fun accuracyMustBeStrictlyOverNinetyPercentWithoutRounding() {
        assertNull(prize(30, 27))
        assertNull(prize(50, 45))
        assertNull(prize(100, 90))
        assertNotNull(prize(100, 91))
        assertNotNull(prize(26, 24))
        assertNull(prize(26, 23))
        assertNotNull(prize(49, 45))
        assertNull(prize(49, 44))
    }

    @Test fun rewardsRequireBothSettingsChecksEveryQuestionAndNoTimeout() {
        assertNull(prize(26, atStart = false))
        assertNull(prize(26, atFinish = false))
        assertNull(prize(26, atStart = false, atFinish = false))
        assertNull(prize(26, correct = 25, answered = 25))
        assertNull(prize(26, correct = 27, answered = 27))
        assertNull(prize(26, timedOut = true))
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

    @Test fun rewardTypesHaveSingularPluralAndFragmentLabels() {
        assertEquals("Ice Cream Cone", RewardType.ICE_CREAM.label)
        assertEquals("Ice Cream Cones", RewardType.ICE_CREAM.pluralLabel)
        assertEquals("Ice Cream Cone Fragment", RewardType.ICE_CREAM.fragmentLabel)
        RewardType.entries.forEach {
            assertTrue(it.label.isNotBlank())
            assertTrue(it.pluralLabel.isNotBlank())
            assertEquals("${it.label} Fragment", it.fragmentLabel)
        }
    }
}
