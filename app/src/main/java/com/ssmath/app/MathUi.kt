@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.ssmath.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

internal val CorrectGreen = Color(0xFF2E9E4F)
internal val WrongRed = Color(0xFFD32F2F)

@Composable
fun MathApp(model: MathViewModel) {
    UpdateNotification()
    MathAppContent(model)
}

@Composable
internal fun MathAppContent(model: MathViewModel) {
    MathTheme(model.theme, model.mode, model.waveAppearance, if (model.skinsEnabled) model.skin else null) {
        val snackbar = remember { SnackbarHostState() }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) model.setForeground(true)
                if (event == Lifecycle.Event.ON_PAUSE) model.setForeground(false)
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
        LaunchedEffect(model.message) {
            model.message?.let {
                snackbar.showSnackbar(it)
                model.consumeMessage()
            }
        }
        SkinBackground(Modifier.fillMaxSize()) {
            Scaffold(containerColor = appBackgroundColor(), contentColor = MaterialTheme.colorScheme.onBackground,
                snackbarHost = { SnackbarHost(snackbar) }) { padding ->
                Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()) {
                    when (model.overlay) {
                        Overlay.SETTINGS -> SettingsScreen(model)
                        Overlay.HISTORY -> HistoryScreen(model)
                        null -> {
                            MainContent(model)
                            SettingsButton(model::openSettings, Modifier.align(Alignment.BottomEnd))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SettingsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier.padding(8.dp).alpha(0.5f).testTag("settings-button")) {
        Icon(Icons.Rounded.Settings, contentDescription = "Settings", modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun MainContent(model: MathViewModel) {
    var confirmQuit by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = model.screen != Screen.SETUP) {
        when (model.screen) {
            Screen.READY -> model.backToSetup()
            Screen.PLAYING -> confirmQuit = true
            Screen.RESULTS -> model.done()
            Screen.SETUP -> Unit
        }
    }
    when (model.screen) {
        Screen.SETUP -> SetupDialog(model)
        Screen.READY -> ReadyDialog(model)
        Screen.PLAYING -> PlayingScreen(model)
        Screen.RESULTS -> model.lastResult?.let { result ->
            ResultsContent(result, title = "Results", showCorrectAnswers = model.showCorrectAnswers) {
                Button(onClick = model::done, modifier = Modifier.widthIn(min = 160.dp).testTag("done-button")) { Text("Done") }
            }
            model.celebration?.let { CelebrationDialog(it, onFinished = model::dismissCelebration) }
        }
    }
    if (confirmQuit && model.screen == Screen.PLAYING) {
        AlertDialog(onDismissRequest = { confirmQuit = false },
            title = { Text("Quit this practice?") },
            text = { Text("Results are only saved after answering all ${model.game?.questionCount} questions or " +
                "reaching $MAX_WRONG_ANSWERS wrong answers.") },
            confirmButton = { TextButton(onClick = { confirmQuit = false; model.backToSetup() }) { Text("Quit") } },
            dismissButton = { TextButton(onClick = { confirmQuit = false }) { Text("Keep practicing") } })
    }
}

@Composable
private fun DialogCard(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), contentAlignment = Alignment.Center) {
        Card(Modifier.widthIn(max = 440.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { content() }
        }
    }
}

@Composable
private fun SetupDialog(model: MathViewModel) {
    DialogCard {
        Text("What would you like to practice?", style = MaterialTheme.typography.titleLarge)
        Operation.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { operation ->
                    val selected = model.selectedOperation == operation
                    val modifier = Modifier.weight(1f).height(72.dp).semantics { this.selected = selected }
                        .testTag("operation-${operation.name}")
                    val padding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    val label: @Composable () -> Unit = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(operation.symbol, style = MaterialTheme.typography.titleLarge)
                            Text(operation.label, style = MaterialTheme.typography.labelMedium, maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (selected) Button(onClick = { model.selectOperation(operation) }, modifier, contentPadding = padding) { label() }
                    else OutlinedButton(onClick = { model.selectOperation(operation) }, modifier, contentPadding = padding) { label() }
                }
            }
        }
        Text("What is the maximum number?", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(model.maximumText, model::updateMaximum, singleLine = true,
            label = { Text("Maximum number") },
            supportingText = { Text("Whole number from $MIN_MAXIMUM to ${"%,d".format(MAX_MAXIMUM)}") },
            isError = model.maximumText.isNotEmpty() && model.maximum == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().testTag("maximum-input"))
        OutlinedTextField(model.questionCountText, model::updateQuestionCount, singleLine = true,
            label = { Text("Number of questions") },
            supportingText = { Text("Whole number from $MIN_QUESTION_COUNT to ${"%,d".format(MAX_QUESTION_COUNT)}") },
            isError = model.questionCountText.isNotEmpty() && model.questionCount == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { model.submitSetup() }),
            modifier = Modifier.fillMaxWidth().testTag("question-count-input"))
        Button(onClick = model::submitSetup, enabled = model.canSubmitSetup,
            modifier = Modifier.fillMaxWidth().testTag("submit-setup")) { Text("Submit") }
    }
}

@Composable
private fun ReadyDialog(model: MathViewModel) {
    DialogCard {
        Text("Press Start when Ready", style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text("${model.selectedOperation?.label} · numbers up to ${model.maximum} · ${model.questionCount} questions",
            style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
        Button(onClick = model::start, modifier = Modifier.fillMaxWidth().height(56.dp).testTag("start-button")) {
            Text("Start", style = MaterialTheme.typography.titleMedium)
        }
        TextButton(onClick = model::backToSetup, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Back") }
    }
}

@Composable
private fun PlayingScreen(model: MathViewModel) {
    val game = model.game ?: return
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Points: ${game.correct}", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).testTag("points"))
            if (model.showTimer) TimerText(model)
        }
        Column(Modifier.align(Alignment.Center).widthIn(max = 440.dp).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Question ${game.attempts.size + 1} of ${game.questionCount}",
               style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("question-progress"))
            Text("${game.problem.text} = ?", style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.testTag("problem"))
            OutlinedTextField(model.answerText, model::updateAnswer, singleLine = true,
                label = { Text("Your answer") },
                textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { model.submitAnswer() }),
                modifier = Modifier.fillMaxWidth().focusRequester(focus).testTag("answer-input"))
            Button(onClick = model::submitAnswer, enabled = parseAnswer(model.answerText) != null,
                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("submit-answer")) { Text("Submit") }
            model.feedback?.let {
                Text(it.text, color = if (it.correct) CorrectGreen else WrongRed,
                    style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            }
        }
        Text("Wrong: ${game.wrong}", style = MaterialTheme.typography.titleMedium, color = WrongRed,
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp).testTag("wrong-tally"))
    }
}

@Composable
private fun TimerText(model: MathViewModel) {
    var elapsed by remember { mutableLongStateOf(model.elapsedMs()) }
    LaunchedEffect(model) {
        while (true) {
            elapsed = model.elapsedMs()
            delay(250)
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(4.dp))
        Text(formatDuration(elapsed), style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("timer"))
    }
}

/** Shows a practice result: the score and every answered problem with a check or an X. */
@Composable
internal fun ResultsContent(result: PracticeResult, title: String?, showCorrectAnswers: Boolean, modifier: Modifier = Modifier,
    actions: @Composable () -> Unit) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            item {
                Column(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    title?.let { Text(it, style = MaterialTheme.typography.headlineMedium) }
                    Text("You got ${result.correct} right!", style = MaterialTheme.typography.titleLarge,
                        color = CorrectGreen, modifier = Modifier.testTag("result-correct"))
                    Text("${result.operation.label} · numbers up to ${result.maximum}",
                        style = MaterialTheme.typography.bodyMedium)
                    Text("Wrong: ${result.wrong} · Time: ${formatDuration(result.durationMs)}",
                        style = MaterialTheme.typography.bodyMedium)
                    Text(formatFinishedAt(result.finishedAt), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            itemsIndexed(result.attempts) { index, attempt -> AttemptRow(index + 1, attempt, showCorrectAnswers) }
        }
        Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) { actions() }
    }
}

@Composable
private fun AttemptRow(number: Int, attempt: Attempt, showCorrectAnswers: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (attempt.correct) Icon(Icons.Rounded.CheckCircle, contentDescription = "Correct", tint = CorrectGreen)
            else Icon(Icons.Rounded.Cancel, contentDescription = "Wrong", tint = WrongRed)
            Text("$number.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(Modifier.weight(1f)) {
                Text("${attempt.problem.text} = ${attempt.given}", style = MaterialTheme.typography.titleMedium)
                if (!attempt.correct && showCorrectAnswers) Text("Correct answer: ${attempt.problem.answer}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
