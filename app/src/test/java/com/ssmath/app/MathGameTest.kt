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

    @Test fun everyOperationKeepsBothNumbersWithinTheSelectedRange() {
        listOf(1..1, 3..12, 7..9, 10..10, 9_999..MAX_MAXIMUM, MAX_MAXIMUM..MAX_MAXIMUM).forEach { range ->
            Operation.entries.forEach { operation ->
                repeat(100) {
                    val problem = generator.next(operation, range.last, minimum = range.first)
                    assertEquals(operation, problem.operation)
                    assertTrue("$problem", problem.left in range)
                    assertTrue("$problem", problem.right in range)
                    if (operation == Operation.SUBTRACTION) assertTrue(problem.answer >= 0)
                    if (operation == Operation.DIVISION) assertEquals(0, problem.left % problem.right)
                }
            }
        }
    }

    @Test fun minimumMustBePositiveAndNoGreaterThanMaximum() {
        Operation.entries.forEach { operation ->
            listOf(-1, 0, 11, MAX_MAXIMUM + 1).forEach { minimum ->
                assertThrows(IllegalArgumentException::class.java) {
                    generator.next(operation, 10, minimum = minimum)
                }
                assertThrows(IllegalArgumentException::class.java) {
                    GameState.start(operation, 10, generator, minimum = minimum)
                }
            }
        }
    }

    @Test fun selectedRangeIsRetainedThroughoutTheGame() {
        Operation.entries.forEach { operation ->
            var game = GameState.start(operation, 12, generator, minimum = 7)
            repeat(DEFAULT_QUESTION_COUNT) {
                assertEquals(7, game.minimum)
                assertEquals(12, game.maximum)
                assertTrue(game.problem.left in 7..12)
                assertTrue(game.problem.right in 7..12)
                game = game.answer(game.problem.answer, generator)
            }
            assertTrue(game.perfect)
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

    @Test fun divisionCanUseEveryValidPairInTheSelectedRange() {
        val problems = (1..1000).map { generator.next(Operation.DIVISION, 12, minimum = 3) }.toSet()
        val expected = (3..12).flatMap { divisor ->
            (1..12 / divisor).map { quotient -> Problem(divisor * quotient, divisor, Operation.DIVISION) }
        }.toSet()
        assertEquals(expected, problems)
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
        assertEquals(Problem(7, 7, Operation.DIVISION),
            generator.next(Operation.DIVISION, 7, Problem(7, 7, Operation.DIVISION), minimum = 7))
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

    @Test fun questionCountMustBeAWholeNumberInRange() {
        assertEquals(MIN_QUESTION_COUNT, parseQuestionCount("1"))
        assertEquals(12, parseQuestionCount(" 12 "))
        assertEquals(MAX_QUESTION_COUNT, parseQuestionCount(MAX_QUESTION_COUNT.toString()))
        listOf("", "0", "-3", "1.5", "abc", (MAX_QUESTION_COUNT + 1).toString(), "99999999999").forEach {
            assertNull(it, parseQuestionCount(it))
        }
        listOf(0, -1, MAX_QUESTION_COUNT + 1).forEach { count ->
            assertThrows(IllegalArgumentException::class.java) {
                GameState.start(Operation.ADDITION, 10, generator, count)
            }
        }
    }

    @Test fun perfectGameEndsExactlyAtTheQuestionCount() {
        Operation.entries.forEach { operation ->
            listOf(1, 3, DEFAULT_QUESTION_COUNT).forEach { count ->
                var game = GameState.start(operation, 12, generator, count)
                repeat(count) {
                    assertFalse(game.finished)
                    assertFalse(game.perfect)
                    val problem = game.problem
                    game = game.answer(problem.answer, generator)
                    if (it == count - 1) assertEquals(problem, game.problem)
                }
                assertTrue(game.finished)
                assertTrue(game.perfect)
                assertEquals(count, game.correct)
                assertEquals(count, game.attempts.size)
                val finished = game
                assertThrows(IllegalStateException::class.java) { finished.answer(0, generator) }
            }
        }
    }

    @Test fun wrongAnswersAlsoCountTowardsTheQuestionLimit() {
        var game = GameState.start(Operation.ADDITION, 12, generator, 3)
        game = game.answer(game.problem.answer + 1, generator)
        game = game.answer(game.problem.answer, generator)
        assertFalse(game.finished)
        game = game.answer(game.problem.answer + 1, generator)
        assertTrue(game.finished)
        assertFalse(game.perfect)
        assertEquals(1, game.correct)
        assertEquals(2, game.wrong)
        assertEquals(3, game.attempts.size)
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
        assertFalse(game.perfect)
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

    @Test fun largerGamesRoundTheirWrongAnswerLimitUpToTenPercent() {
        mapOf(25 to 5, 26 to 5, 49 to 5, 50 to 5, 51 to 6, 59 to 6, 60 to 6, 100 to 10).forEach { (count, limit) ->
            var game = GameState.start(Operation.ADDITION, 10, generator, count)
            assertEquals(limit, game.maxWrongAnswers)
            repeat(limit - 1) {
                game = game.answer(game.problem.answer + 1, generator)
                assertFalse(game.finished)
            }
            game = game.answer(game.problem.answer + 1, generator)
            assertTrue(game.finished)
            assertEquals(limit, game.wrong)
            assertThrows(IllegalStateException::class.java) { game.answer(0, generator) }
        }
    }
}
