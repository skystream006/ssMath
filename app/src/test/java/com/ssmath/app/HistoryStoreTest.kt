package com.ssmath.app

import java.io.File
import java.nio.file.Files
import java.time.ZoneOffset
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class HistoryStoreTest {
    private val directory: File = Files.createTempDirectory("history").toFile()
    private val file = File(directory, "practice_history.json")
    private val store = HistoryStore(file)

    @After fun cleanup() { directory.deleteRecursively() }

    private fun result(finishedAt: Long, correct: Int = 2) = PracticeResult(
        id = finishedAt, finishedAt = finishedAt, operation = Operation.ADDITION, maximum = 10, durationMs = 61_000,
        attempts = List(correct) { Attempt(Problem(1, it + 1, Operation.ADDITION), it + 2) } +
            List(MAX_WRONG_ANSWERS) { Attempt(Problem(2, 2, Operation.ADDITION), 5) })

    @Test fun savesResultsNewestFirstAndReloadsThem() {
        assertEquals(emptyList<PracticeResult>(), store.load())
        store.add(result(1_000))
        store.add(result(3_000, correct = 4))
        store.add(result(2_000))
        val loaded = HistoryStore(file).load()
        assertEquals(listOf(3_000L, 2_000L, 1_000L), loaded.map { it.finishedAt })
        assertEquals(4, loaded.first().correct)
        assertEquals(MAX_WRONG_ANSWERS, loaded.first().wrong)
        assertTrue(loaded.first().attempts.first().correct)
        assertEquals(result(3_000, correct = 4), loaded.first())
    }

    @Test fun deletesAndClearsResults() {
        store.add(result(1_000))
        store.add(result(2_000))
        assertEquals(listOf(2_000L), store.delete(1_000).map { it.id })
        assertEquals(listOf(2_000L), store.load().map { it.id })
        assertEquals(emptyList<PracticeResult>(), store.clear())
        assertFalse(file.exists())
        assertEquals(emptyList<PracticeResult>(), store.load())
    }

    @Test fun keepsOnlyTheMostRecentResults() {
        repeat(MAX_HISTORY_RESULTS + 5) { store.add(result(it.toLong() + 1)) }
        val loaded = store.load()
        assertEquals(MAX_HISTORY_RESULTS, loaded.size)
        assertEquals((MAX_HISTORY_RESULTS + 5).toLong(), loaded.first().id)
    }

    @Test fun corruptHistoryIsIgnored() {
        file.writeText("not json")
        assertEquals(emptyList<PracticeResult>(), store.load())
        store.add(result(5_000))
        assertEquals(listOf(5_000L), store.load().map { it.id })
    }

    @Test fun formatsDateAndTime() {
        val text = formatFinishedAt(0, ZoneOffset.UTC)
        assertTrue(text, text.contains("1970"))
    }
}
