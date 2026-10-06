package com.ssmath.app

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

const val MAX_HISTORY_RESULTS = 500

/** A finished practice session saved with the date and time it ended. */
@Serializable
data class PracticeResult(
    val id: Long,
    val finishedAt: Long,
    val operation: Operation,
    val maximum: Int,
    val durationMs: Long,
    val attempts: List<Attempt>,
    val minimum: Int = MIN_MAXIMUM
) {
    val correct: Int get() = attempts.count { it.correct }
    val wrong: Int get() = attempts.count { !it.correct }
}

fun formatFinishedAt(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withZone(zone).format(Instant.ofEpochMilli(epochMillis))

/** Stores practice results privately as JSON, newest first. Not thread-safe; callers serialize access. */
class HistoryStore(private val file: File) {
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(PracticeResult.serializer())

    fun load(): List<PracticeResult> {
        if (!file.exists()) return emptyList()
        return try {
            json.decodeFromString(serializer, file.readText(Charsets.UTF_8)).sortedByDescending { it.finishedAt }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun add(result: PracticeResult): List<PracticeResult> =
        save((listOf(result) + load().filter { it.id != result.id }).take(MAX_HISTORY_RESULTS))

    fun delete(id: Long): List<PracticeResult> = save(load().filter { it.id != id })

    fun clear(): List<PracticeResult> {
        if (file.exists() && !file.delete()) throw IOException("Unable to clear practice history.")
        return emptyList()
    }

    private fun save(results: List<PracticeResult>): List<PracticeResult> {
        val directory = file.absoluteFile.parentFile ?: throw IOException("Invalid history location.")
        if (!directory.isDirectory && !directory.mkdirs()) throw IOException("Unable to create history folder.")
        val temporary = File(directory, "${file.name}.tmp")
        temporary.writeText(json.encodeToString(serializer, results), Charsets.UTF_8)
        Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        return results
    }
}
