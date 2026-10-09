package com.ssmath.app

import kotlin.random.Random
import kotlinx.serialization.Serializable

enum class RewardTier(val label: String, val questionCountLabel: String) {
    TIER_1("Tier 1", "25–49 questions"),
    TIER_2("Tier 2", "50 or more questions")
}

internal fun rewardTierForQuestionCount(questionCount: Int): RewardTier? = when {
    questionCount >= 50 -> RewardTier.TIER_2
    questionCount >= 25 -> RewardTier.TIER_1
    else -> null
}

@Serializable
enum class RewardType(val label: String, val pluralLabel: String, val fragmentLabel: String, val tier: RewardTier) {
    LOLLIPOP("Lollipop", "Lollipops", "Lollipop Fragment", RewardTier.TIER_1),
    ICE_CREAM("Ice Cream Cone", "Ice Cream Cones", "Ice Cream Cone Fragment", RewardTier.TIER_1),
    GUMMI_BEAR("Gummi Bear", "Gummi Bears", "Gummi Bear Fragment", RewardTier.TIER_1),
    RAMEN("Ramen", "Ramen", "Ramen Fragment", RewardTier.TIER_1),
    VIDEO_GAME("Video Game", "Video Games", "Video Game Fragment", RewardTier.TIER_2),
    BED_TIME("Bed Time", "Bed Time", "Bed Time Fragment", RewardTier.TIER_1),
    RESTAURANT("Restaurant", "Restaurants", "Restaurant Fragment", RewardTier.TIER_2)
}

@Serializable
data class RewardBalance(val whole: Int = 0, val fragments: Int = 0) {
    init {
        require(whole in 0..(Int.MAX_VALUE - 2) / 3 && fragments in 0..2)
    }

    val totalFragments: Int get() = whole * 3 + fragments

    fun addFragment(): RewardBalance {
        val total = Math.addExact(totalFragments, 1)
        return RewardBalance(total / 3, total % 3)
    }

    fun removeFragments(count: Int): RewardBalance {
        require(count >= 0)
        val total = (totalFragments - count).coerceAtLeast(0)
        return RewardBalance(total / 3, total % 3)
    }
}

/** The inventory balance immediately after this result's fragment was claimed. */
@Serializable
data class PrizeAward(val type: RewardType, val balance: RewardBalance)

internal fun qualifiesForReward(questionCount: Int, correct: Int, answered: Int, timedOut: Boolean): Boolean =
    !timedOut && rewardTierForQuestionCount(questionCount) != null &&
        answered == questionCount && correct.toLong() * 100 > questionCount.toLong() * 90

fun selectPrize(
    questionCount: Int,
    correct: Int,
    answered: Int,
    enabledAtStart: Boolean,
    enabledAtFinish: Boolean,
    timedOut: Boolean = false,
    random: Random = Random.Default
): RewardType? {
    if (!enabledAtStart || !enabledAtFinish || !qualifiesForReward(questionCount, correct, answered, timedOut)) return null
    val tier = rewardTierForQuestionCount(questionCount) ?: return null
    val prizes = RewardType.entries.filter { it.tier == tier }
    return prizes.singleOrNull() ?: prizes.random(random)
}
