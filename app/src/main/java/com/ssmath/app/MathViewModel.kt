package com.ssmath.app

import android.app.Application
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

enum class Screen { SETUP, READY, PLAYING, RESULTS }

enum class Overlay { SETTINGS, HISTORY }

data class Feedback(val text: String, val correct: Boolean)

class MathViewModel(
    application: Application,
    private val generator: ProblemGenerator = ProblemGenerator(),
    private val clock: () -> Long = SystemClock::elapsedRealtime,
    private val wallClock: () -> Long = System::currentTimeMillis
) : AndroidViewModel(application) {
    constructor(application: Application) : this(application, ProblemGenerator())

    private val settings = application.getSharedPreferences("settings", 0)
    private val store = HistoryStore(File(application.filesDir, "practice_history.json"))
    private val storeLock = Mutex()

    var screen by mutableStateOf(Screen.SETUP)
        private set
    var overlay by mutableStateOf<Overlay?>(null)
        private set
    var selectedOperation by mutableStateOf(Operation.fromPreference(settings.getString("operation", null)))
        private set
    var maximumText by mutableStateOf(settings.getInt("maximum", 10).toString())
        private set
    var game by mutableStateOf<GameState?>(null)
        private set
    var answerText by mutableStateOf("")
        private set
    var feedback by mutableStateOf<Feedback?>(null)
        private set
    var lastResult by mutableStateOf<PracticeResult?>(null)
        private set
    var history by mutableStateOf<List<PracticeResult>>(emptyList())
        private set
    var historyDetail by mutableStateOf<PracticeResult?>(null)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    var showTimer by mutableStateOf(settings.getBoolean("show_timer", true))
        private set
    var waveAppearance by mutableStateOf(settings.getBoolean("wave_appearance", true))
        private set
    var theme by mutableStateOf(colorThemeFromPreference(settings.getString("theme", null)))
        private set
    var mode by mutableStateOf(settings.getString("mode", null))
        private set
    var skinsEnabled by mutableStateOf(settings.getBoolean("skins_enabled", false))
        private set
    var skin by mutableStateOf(AppSkin.fromPreference(settings.getString("skin", null)))
        private set

    private var elapsedBefore = 0L
    private var runningSince: Long? = null
    private var foreground = true

    val maximum: Int? get() = parseMaximum(maximumText)
    val canSubmitSetup: Boolean get() = selectedOperation != null && maximum != null

    init {
        viewModelScope.launch { reloadHistory() }
    }

    fun selectOperation(operation: Operation) { selectedOperation = operation }

    fun updateMaximum(text: String) { maximumText = text.filter { it in '0'..'9' }.take(6) }

    fun submitSetup() {
        val operation = selectedOperation ?: return
        val maximum = maximum ?: return
        settings.edit().putString("operation", operation.name).putInt("maximum", maximum).apply()
        screen = Screen.READY
    }

    fun backToSetup() {
        if (screen == Screen.PLAYING) DebugLog.event(DebugEvent.GAME_ABANDONED)
        game = null
        answerText = ""
        feedback = null
        resetTimer()
        screen = Screen.SETUP
    }

    fun start() {
        val operation = selectedOperation ?: return
        val maximum = maximum ?: return
        game = GameState.start(operation, maximum, generator)
        answerText = ""
        feedback = null
        resetTimer()
        screen = Screen.PLAYING
        updateTimer()
        DebugLog.event(DebugEvent.GAME_STARTED)
    }

    fun updateAnswer(text: String) { answerText = text.filter { it in '0'..'9' }.take(9) }

    fun submitAnswer() {
        val current = game ?: return
        if (screen != Screen.PLAYING || current.finished) return
        val value = parseAnswer(answerText) ?: return
        val problem = current.problem
        val next = current.answer(value, generator)
        game = next
        answerText = ""
        feedback = if (value == problem.answer) Feedback("Correct! +1 point", true)
            else Feedback("Not quite: ${problem.text} = ${problem.answer}", false)
        if (next.finished) finish(next)
    }

    private fun finish(state: GameState) {
        val duration = elapsedMs()
        resetTimer()
        val finishedAt = wallClock()
        val result = PracticeResult(finishedAt, finishedAt, state.operation, state.maximum, duration, state.attempts)
        lastResult = result
        screen = Screen.RESULTS
        DebugLog.event(DebugEvent.GAME_FINISHED)
        viewModelScope.launch {
            try {
                history = storeLock.withLock { withContext(Dispatchers.IO) { store.add(result) } }
                DebugLog.event(DebugEvent.HISTORY_SAVED)
            } catch (error: Exception) {
                DebugLog.event(DebugEvent.HISTORY_FAILURE, error = error)
                message = "Unable to save these results. Free device storage and try again."
            }
        }
    }

    fun done() {
        lastResult = null
        game = null
        feedback = null
        screen = Screen.SETUP
    }

    fun elapsedMs(): Long = elapsedBefore + (runningSince?.let { clock() - it } ?: 0L)

    private fun resetTimer() {
        elapsedBefore = 0
        runningSince = null
    }

    /** The timer only runs while a game is visible: it pauses in settings, history, or the background. */
    private fun updateTimer() {
        val shouldRun = screen == Screen.PLAYING && overlay == null && foreground
        val since = runningSince
        if (shouldRun && since == null) runningSince = clock()
        else if (!shouldRun && since != null) {
            elapsedBefore += clock() - since
            runningSince = null
        }
    }

    fun setForeground(value: Boolean) {
        foreground = value
        updateTimer()
    }

    fun openSettings() {
        overlay = Overlay.SETTINGS
        updateTimer()
    }

    fun openHistory() {
        historyDetail = null
        overlay = Overlay.HISTORY
        updateTimer()
        viewModelScope.launch { reloadHistory() }
    }

    fun closeOverlay() {
        if (overlay == Overlay.HISTORY && historyDetail != null) {
            historyDetail = null
            return
        }
        overlay = if (overlay == Overlay.HISTORY) Overlay.SETTINGS else null
        updateTimer()
    }

    fun showHistoryDetail(result: PracticeResult?) { historyDetail = result }

    fun deleteResult(id: Long) {
        historyDetail = null
        viewModelScope.launch {
            try {
                history = storeLock.withLock { withContext(Dispatchers.IO) { store.delete(id) } }
            } catch (error: Exception) {
                DebugLog.event(DebugEvent.HISTORY_FAILURE, error = error)
                message = "Unable to delete this result."
            }
        }
    }

    fun clearHistory() {
        historyDetail = null
        viewModelScope.launch {
            try {
                history = storeLock.withLock { withContext(Dispatchers.IO) { store.clear() } }
            } catch (error: Exception) {
                DebugLog.event(DebugEvent.HISTORY_FAILURE, error = error)
                message = "Unable to clear practice history."
            }
        }
    }

    private suspend fun reloadHistory() {
        history = storeLock.withLock { withContext(Dispatchers.IO) { store.load() } }
    }

    fun consumeMessage() { message = null }

    fun chooseShowTimer(value: Boolean) {
        showTimer = value
        settings.edit().putBoolean("show_timer", value).apply()
    }

    fun chooseWaveAppearance(enabled: Boolean) {
        waveAppearance = enabled
        settings.edit().putBoolean("wave_appearance", enabled).apply()
    }

    fun chooseTheme(value: String) {
        theme = colorThemeFromPreference(value)
        settings.edit().putString("theme", theme).apply()
    }

    fun chooseDark(dark: Boolean) {
        mode = if (dark) "dark" else "light"
        settings.edit().putString("mode", mode).apply()
    }

    fun chooseSkins(enabled: Boolean) {
        skinsEnabled = enabled
        settings.edit().putBoolean("skins_enabled", enabled).apply()
    }

    fun chooseSkin(value: AppSkin) {
        skin = value
        settings.edit().putString("skin", value.name).apply()
    }
}
