package com.ssmath.app

import kotlin.random.Random
import kotlinx.serialization.Serializable

const val MIN_MAXIMUM = 1
const val MAX_MAXIMUM = 10_000
const val MAX_WRONG_ANSWERS = 5
private const val MAX_ANSWER_DIGITS = 9

@Serializable
enum class Operation(val label: String, val symbol: String) {
    ADDITION("Addition", "+"),
    SUBTRACTION("Subtraction", "−"),
    MULTIPLICATION("Multiplication", "×"),
    DIVISION("Division", "÷");

    fun apply(left: Int, right: Int): Int = when (this) {
        ADDITION -> left + right
        SUBTRACTION -> left - right
        MULTIPLICATION -> left * right
        DIVISION -> left / right
    }

    companion object {
        fun fromPreference(value: String?): Operation? = entries.find { it.name == value }
    }
}

@Serializable
data class Problem(val left: Int, val right: Int, val operation: Operation) {
    val answer: Int get() = operation.apply(left, right)
    val text: String get() = "$left ${operation.symbol} $right"
}

/** One answered problem. [given] is what the user entered. */
@Serializable
data class Attempt(val problem: Problem, val given: Int) {
    val correct: Boolean get() = given == problem.answer
}

/** Accepts whole numbers from [MIN_MAXIMUM] to [MAX_MAXIMUM]; anything else is invalid. */
fun parseMaximum(text: String): Int? {
    val trimmed = text.trim()
    if (trimmed.isEmpty() || trimmed.length > 6 || !trimmed.all { it in '0'..'9' }) return null
    return trimmed.toInt().takeIf { it in MIN_MAXIMUM..MAX_MAXIMUM }
}

/** Accepts a non-negative whole-number answer. */
fun parseAnswer(text: String): Int? {
    val trimmed = text.trim()
    if (trimmed.isEmpty() || trimmed.length > MAX_ANSWER_DIGITS || !trimmed.all { it in '0'..'9' }) return null
    return trimmed.toIntOrNull()
}

class ProblemGenerator(private val random: Random = Random.Default) {
    /**
     * Creates a random problem whose two numbers are each between 1 and [maximum].
     * Subtraction never goes below zero and division always has a whole-number answer.
     */
    fun next(operation: Operation, maximum: Int, previous: Problem? = null): Problem {
        require(maximum in MIN_MAXIMUM..MAX_MAXIMUM) { "Maximum must be between $MIN_MAXIMUM and $MAX_MAXIMUM." }
        var problem = create(operation, maximum)
        // Avoid showing the identical problem twice in a row whenever another one exists.
        var retries = 0
        while (problem == previous && retries++ < 20) problem = create(operation, maximum)
        return problem
    }

    private fun create(operation: Operation, maximum: Int): Problem {
        fun number() = random.nextInt(1, maximum + 1)
        return when (operation) {
            Operation.ADDITION, Operation.MULTIPLICATION -> Problem(number(), number(), operation)
            Operation.SUBTRACTION -> {
                val a = number()
                val b = number()
                Problem(maxOf(a, b), minOf(a, b), operation)
            }
            Operation.DIVISION -> {
                // Pick uniformly among every (divisor, quotient) pair whose dividend fits the maximum.
                val total = (1..maximum).sumOf { maximum / it }
                var pick = random.nextInt(total)
                var divisor = 1
                while (pick >= maximum / divisor) {
                    pick -= maximum / divisor
                    divisor++
                }
                val quotient = pick + 1
                Problem(divisor * quotient, divisor, operation)
            }
        }
    }
}

data class GameState(
    val operation: Operation,
    val maximum: Int,
    val problem: Problem,
    val attempts: List<Attempt> = emptyList()
) {
    val correct: Int get() = attempts.count { it.correct }
    val wrong: Int get() = attempts.count { !it.correct }
    val finished: Boolean get() = wrong >= MAX_WRONG_ANSWERS

    /** Records an answer and moves on to a new problem of the same type until the game ends. */
    fun answer(value: Int, generator: ProblemGenerator): GameState {
        check(!finished) { "The game is over." }
        val next = copy(attempts = attempts + Attempt(problem, value))
        return if (next.finished) next else next.copy(problem = generator.next(operation, maximum, problem))
    }

    companion object {
        fun start(operation: Operation, maximum: Int, generator: ProblemGenerator) =
            GameState(operation, maximum, generator.next(operation, maximum))
    }
}

fun formatDuration(milliseconds: Long): String {
    val seconds = (milliseconds.coerceAtLeast(0) / 1000)
    val hours = seconds / 3600
    return if (hours > 0) "%d:%02d:%02d".format(hours, seconds / 60 % 60, seconds % 60)
    else "%02d:%02d".format(seconds / 60, seconds % 60)
}
