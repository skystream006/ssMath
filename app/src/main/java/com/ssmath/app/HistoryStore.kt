package com.ssmath.app

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

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
    val minimum: Int = MIN_MAXIMUM,
    val questionCount: Int = attempts.size,
    val timedOut: Boolean = false,
    val prizeType: RewardType? = null,
    val prize: PrizeAward? = null
) {
    val correct: Int get() = attempts.count { it.correct }
    val wrong: Int get() = attempts.count { !it.correct }
    val percentCorrect: Int get() = if (attempts.isEmpty()) 0 else (correct.toLong() * 100 / attempts.size).toInt()
    val percentCorrectText: String
        get() {
            if (attempts.isEmpty()) return "0"
            val numerator = correct.toLong() * 100
            return if (numerator % attempts.size == 0L) (numerator / attempts.size).toString()
            else String.format(Locale.ROOT, "%.2f", numerator.toDouble() / attempts.size).trimEnd('0').trimEnd('.')
        }
}

@Serializable
data class PracticeSnapshot(
    val history: List<PracticeResult> = emptyList(),
    val rewards: Map<RewardType, RewardBalance> = emptyMap(),
    val claimedResultIds: Set<Long> = emptySet(),
    val lastResultId: Long = 0,
    val pokemons: Set<Celebration> = emptySet()
)

data class SavedPractice(val result: PracticeResult, val snapshot: PracticeSnapshot)

fun formatFinishedAt(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withZone(zone).format(Instant.ofEpochMilli(epochMillis))

/** Stores practice results privately as JSON, newest first. Not thread-safe; callers serialize access. */
class HistoryStore(private val file: File) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val serializer = ListSerializer(PracticeResult.serializer())

    fun load(): List<PracticeResult> = loadSnapshot().history

    fun loadSnapshot(): PracticeSnapshot {
        if (!file.exists()) return PracticeSnapshot()
        val text = file.readText(Charsets.UTF_8)
        val snapshot = try {
            val element = json.parseToJsonElement(text)
            if (element is JsonArray) PracticeSnapshot(history = json.decodeFromJsonElement(serializer, element))
            else {
                // A damaged inventory must never be treated as an empty legacy history.
                require(element is JsonObject && "history" in element && "rewards" in element)
                json.decodeFromJsonElement(PracticeSnapshot.serializer(), element)
            }
        } catch (error: Exception) {
            if (text.trimStart().startsWith("{")) throw IOException("Unable to read rewards and practice history.", error)
            return PracticeSnapshot()
        }
        return snapshot.copy(history = snapshot.history.sortedWith(
            compareByDescending<PracticeResult> { it.finishedAt }.thenByDescending { it.id }))
    }

    fun add(result: PracticeResult): List<PracticeResult> {
        val snapshot = loadSnapshot()
        val existing = snapshot.history.find { it.id == result.id }
        if (existing == null && result.id in snapshot.claimedResultIds) return snapshot.history
        return save(withResult(snapshot, existing?.takeIf { it.prize != null } ?: result)).history
    }

    /** Allocate the ID and save in the same caller-held lock, including after a clock rollback. */
    fun addNewResult(result: PracticeResult): SavedPractice {
        val snapshot = loadSnapshot()
        val largestId = maxOf(snapshot.lastResultId, snapshot.history.maxOfOrNull { it.id } ?: 0L,
            snapshot.claimedResultIds.maxOrNull() ?: 0L)
        val savedResult = result.copy(id = maxOf(result.id, Math.addExact(largestId, 1)))
        return SavedPractice(savedResult, save(withResult(snapshot, savedResult).copy(lastResultId = savedResult.id)))
    }

    // Retention follows monotonic IDs so a clock rollback cannot evict the result just saved.
    private fun withResult(snapshot: PracticeSnapshot, result: PracticeResult) = snapshot.copy(
        history = (listOf(result) + snapshot.history.filter { it.id != result.id })
            .sortedByDescending { it.id }
            .take(MAX_HISTORY_RESULTS)
            .sortedWith(compareByDescending<PracticeResult> { it.finishedAt }.thenByDescending { it.id })
    )

    fun claimReward(id: Long): PracticeSnapshot {
        val snapshot = loadSnapshot()
        val result = snapshot.history.find { it.id == id } ?: return snapshot
        val type = result.prizeType ?: return snapshot
        if (result.prize != null || id in snapshot.claimedResultIds ||
            !qualifiesForReward(result.questionCount, result.correct, result.attempts.size, result.timedOut)) return snapshot
        val balance = (snapshot.rewards[type] ?: RewardBalance()).addFragment()
        val claimed = result.copy(prize = PrizeAward(type, balance))
        return save(snapshot.copy(
            history = snapshot.history.map { if (it.id == id) claimed else it },
            rewards = snapshot.rewards + (type to balance),
            claimedResultIds = snapshot.claimedResultIds + id
        ))
    }

    fun useReward(type: RewardType): PracticeSnapshot {
        val snapshot = loadSnapshot()
        val balance = snapshot.rewards[type] ?: return snapshot
        if (balance.whole == 0) return snapshot
        return save(snapshot.copy(
            rewards = snapshot.rewards + (type to balance.copy(whole = balance.whole - 1))
        ))
    }

    fun collectPokemon(celebration: Celebration): PracticeSnapshot {
        val snapshot = loadSnapshot()
        if (celebration.category != CelebrationCategory.POKEMONS || celebration in snapshot.pokemons) return snapshot
        return save(snapshot.copy(pokemons = snapshot.pokemons + celebration))
    }

    fun delete(id: Long): List<PracticeResult> {
        val snapshot = loadSnapshot()
        return save(snapshot.copy(history = snapshot.history.filter { it.id != id })).history
    }

    fun clear(): List<PracticeResult> {
        val snapshot = loadSnapshot()
        if (snapshot.rewards.isNotEmpty() || snapshot.claimedResultIds.isNotEmpty() ||
            snapshot.lastResultId != 0L || snapshot.pokemons.isNotEmpty()) {
            save(snapshot.copy(history = emptyList()))
        } else if (file.exists() && !file.delete()) throw IOException("Unable to clear practice history.")
        return emptyList()
    }

    private fun save(snapshot: PracticeSnapshot): PracticeSnapshot {
        val directory = file.absoluteFile.parentFile ?: throw IOException("Invalid history location.")
        if (!directory.isDirectory && !directory.mkdirs()) throw IOException("Unable to create history folder.")
        val temporary = File(directory, "${file.name}.tmp")
        try {
            temporary.writeText(json.encodeToString(PracticeSnapshot.serializer(), snapshot), Charsets.UTF_8)
            Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } finally {
            if (temporary.isFile) temporary.delete()
        }
        return snapshot
    }
}
