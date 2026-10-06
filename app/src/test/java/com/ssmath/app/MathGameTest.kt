package com.ssmath.app

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class MathGameTest {
    private val generator = ProblemGenerator(Random(42))

    @Test fun everyOperationKeepsBothNumbersWithinTheMaximum() {
        listOf(1, 2, 7, 12, 100).forEach { maximum ->
            Operation.entries.forEach { operation ->
                repeat(500) {
                    val problem = generator.next(operation, maximum)
                    assertEquals(operation, problem.operation)
                    assertTrue("$problem", problem.left in 1..maximum)
                    assertTrue("$problem", problem.right in 1..maximum)
                }
            }
        }
    }

    @Test fun answersMatchTheOperation() {
        assertEquals(12, Problem(7, 5, Operation.ADDITION).answer)
        assertEquals(2, Problem(7, 5, Operation.SUBTRACTION).answer)
        assertEquals(35, Problem(7, 5, Operation.MULTIPLICATION).answer)
        assertEquals(4, Problem(12, 3, Operation.DIVISION).answer)
        assertEquals("7 + 5", Problem(7, 5, Operation.ADDITION).text)
    }

    @Test fun subtractionIsNeverNegativeAndDivisionIsAlwaysWhole() {
        repeat(1000) {
            val difference = generator.next(Operation.SUBTRACTION, 50)
            assertTrue("$difference", difference.answer >= 0)
            val quotient = generator.next(Operation.DIVISION, 50)
            assertEquals("$quotient", 0, quotient.left % quotient.right)
            assertTrue("$quotient", quotient.answer >= 1)
        }
    }

    @Test fun divisionUsesVariousDivisors() {
        val divisors = (1..500).map { generator.next(Operation.DIVISION, 20).right }.toSet()
        assertTrue(divisors.size > 5)
    }

    @Test fun avoidsRepeatingTheSameProblemConsecutively() {
        var previous = generator.next(Operation.ADDITION, 3)
        repeat(200) {
            val next = generator.next(Operation.ADDITION, 3, previous)
            assertNotEquals(previous, next)
            previous = next
        }
        // With only one possible problem the generator still returns it.
        assertEquals(Problem(1, 1, Operation.DIVISION), generator.next(Operation.DIVISION, 1, Problem(1, 1, Operation.DIVISION)))
    }

    @Test fun maximumMustBeAWholeNumberInRange() {
        assertEquals(1, parseMaximum("1"))
        assertEquals(12, parseMaximum(" 12 "))
        assertEquals(MAX_MAXIMUM, parseMaximum(MAX_MAXIMUM.toString()))
        listOf("", "0", "-3", "1.5", "abc", (MAX_MAXIMUM + 1).toString(), "99999999999").forEach {
            assertNull(it, parseMaximum(it))
        }
        assertThrows(IllegalArgumentException::class.java) { generator.next(Operation.ADDITION, 0) }
    }

    @Test fun answersMustBeNonNegativeWholeNumbers() {
        assertEquals(0, parseAnswer("0"))
        assertEquals(100000000, parseAnswer("100000000"))
        listOf("", "-1", "2.5", "x", "1234567890").forEach { assertNull(it, parseAnswer(it)) }
    }

    @Test fun correctAnswersScoreAndWrongAnswersAreTalliedUntilFive() {
        var game = GameState.start(Operation.MULTIPLICATION, 12, generator)
        repeat(3) {
            val problem = game.problem
            game = game.answer(problem.answer, generator)
            assertEquals(Operation.MULTIPLICATION, game.problem.operation)
            assertNotEquals(problem, game.problem)
        }
        assertEquals(3, game.correct)
        assertEquals(0, game.wrong)
        repeat(MAX_WRONG_ANSWERS - 1) {
            game = game.answer(game.problem.answer + 1, generator)
            assertFalse(game.finished)
        }
        assertEquals(MAX_WRONG_ANSWERS - 1, game.wrong)
        val last = game.problem
        game = game.answer(last.answer + 1, generator)
        assertTrue(game.finished)
        assertEquals(MAX_WRONG_ANSWERS, game.wrong)
        assertEquals(3, game.correct)
        assertEquals(3 + MAX_WRONG_ANSWERS, game.attempts.size)
        assertEquals(last, game.attempts.last().problem)
        assertFalse(game.attempts.last().correct)
        val finished = game
        assertThrows(IllegalStateException::class.java) { finished.answer(0, generator) }
    }

    @Test fun formatsDurations() {
        assertEquals("00:00", formatDuration(0))
        assertEquals("00:00", formatDuration(-5))
        assertEquals("01:05", formatDuration(65_999))
        assertEquals("1:00:01", formatDuration(3_601_000))
    }
}
