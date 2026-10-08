package com.ssmath.app

import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
enum class RewardType(val label: String, val pluralLabel: String, val fragmentLabel: String) {
    LOLLIPOP("Lollipop", "Lollipops", "Lollipop Fragment"),
    ICE_CREAM("Ice Cream Cone", "Ice Cream Cones", "Ice Cream Cone Fragment"),
    GUMMI_BEAR("Gummi Bear", "Gummi Bears", "Gummi Bear Fragment"),
    RAMEN("Ramen", "Ramen", "Ramen Fragment"),
    VIDEO_GAME("Video Game", "Video Games", "Video Game Fragment")
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
}

/** The inventory balance immediately after this result's fragment was claimed. */
@Serializable
data class PrizeAward(val type: RewardType, val balance: RewardBalance)

internal fun qualifiesForReward(questionCount: Int, correct: Int, answered: Int, timedOut: Boolean): Boolean =
    !timedOut && questionCount > 25 && answered == questionCount && correct.toLong() * 100 > questionCount.toLong() * 90

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
    return if (questionCount >= 50) RewardType.VIDEO_GAME
    else RewardType.entries.filter { it != RewardType.VIDEO_GAME }.random(random)
}
