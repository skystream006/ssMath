@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.ssmath.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

internal val CorrectGreen = Color(0xFF2E9E4F)
internal val WrongRed = Color(0xFFD32F2F)

@Composable
fun MathApp(model: MathViewModel) {
    UpdateNotification()
    MathAppContent(model)
}

@Composable
internal fun MathAppContent(model: MathViewModel) {
    MathTheme(model.theme, model.mode, model.waveAppearance, if (model.skinsEnabled) model.skin else null,
        textSizePercent = model.textSizePercent) {
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
                        Overlay.REWARDS -> RewardsScreen(model)
                        Overlay.POKEMONS -> PokemonsScreen(model)
                        null -> {
                            MainContent(model)
                            SettingsButton(model::openSettings, Modifier.align(Alignment.BottomEnd))
                        }
                    }
                    model.rewardResult?.let { result ->
                        if (model.rewardDialogVisible) RewardGiftDialog(result, model.claimingReward, model.rewardError,
                            onOpen = model::claimReward, onDismiss = model::dismissReward)
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
        Screen.SETUP -> HomeScreen(model)
        Screen.READY -> ReadyDialog(model)
        Screen.PLAYING -> PlayingScreen(model)
        Screen.RESULTS -> model.lastResult?.let { result ->
            ResultsContent(result, title = "Results", showCorrectAnswers = model.showCorrectAnswers) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (result.prizeType != null && result.prize == null && model.celebration == null) {
                        Button(onClick = { model.showRewardForResult(result) }) { Text("Open gift box") }
                    }
                    Button(onClick = model::done, modifier = Modifier.widthIn(min = 160.dp).testTag("done-button")) { Text("Done") }
                }
            }
            model.celebration?.let { celebration ->
                CelebrationDialog(celebration, onFinished = model::dismissCelebration,
                    onPresented = { model.collectPresentedCelebration(celebration) })
            }
            model.earlyFinishMessage?.let { message ->
                AlertDialog(onDismissRequest = model::dismissEarlyFinishDialog,
                    text = { Text(message) },
                    confirmButton = {
                        TextButton(onClick = model::dismissEarlyFinishDialog,
                            modifier = Modifier.testTag("view-results")) { Text("View results") }
                    })
            }
        }
    }
    if (confirmQuit && model.screen == Screen.PLAYING) {
        AlertDialog(onDismissRequest = { confirmQuit = false },
            title = { Text("Quit this practice?") },
            text = { Text("Results are only saved after answering all ${model.game?.questionCount} questions or " +
                "reaching ${model.game?.maxWrongAnswers} wrong answers" +
                if (model.activeTimeLimitMs != null) ", or when time runs out." else ".") },
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
private fun HomeScreen(model: MathViewModel) {
    Column(Modifier.fillMaxSize().testTag("home-screen"), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.widthIn(max = 440.dp).fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = model::openRewards,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("home-rewards"),
                contentPadding = PaddingValues(8.dp)) {
                Text("Rewards", textAlign = TextAlign.Center)
            }
            Button(onClick = model::openPokemons,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("home-pokemons"),
                contentPadding = PaddingValues(8.dp)) {
                Text("Pokémon", textAlign = TextAlign.Center)
            }
        }
        SetupForm(model, Modifier.weight(1f).widthIn(max = 440.dp).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 72.dp))
    }
}

@Composable
private fun SetupForm(model: MathViewModel, modifier: Modifier) {
    val division = model.selectedOperation == Operation.DIVISION
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("What would you like to practice?", style = MaterialTheme.typography.titleLarge)
        Operation.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { operation ->
                    val selected = model.selectedOperation == operation
                    val modifier = Modifier.weight(1f).heightIn(min = 72.dp).semantics { this.selected = selected }
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
        Text(if (division) "What is the Maximum First number?" else "What is the minimum number?",
            style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(if (division) model.divisionMaximumFirstText else model.minimumText,
            if (division) model::updateDivisionMaximumFirst else model::updateMinimum, singleLine = true,
            label = { Text(if (division) "Maximum First number" else "Minimum number") },
            supportingText = { Text(if (division) "Whole number from $MIN_MAXIMUM to ${"%,d".format(MAX_MAXIMUM)}"
                else "Whole number from $MIN_MAXIMUM to the maximum number") },
            isError = if (division) model.divisionMaximumFirstText.isNotEmpty() && model.divisionMaximumFirst == null
                else model.minimumText.isNotEmpty() && model.minimum == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().testTag("minimum-input"))
        Text(if (division) "What is the Maximum Second number?" else "What is the maximum number?",
            style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(if (division) model.divisionMaximumSecondText else model.maximumText,
            if (division) model::updateDivisionMaximumSecond else model::updateMaximum, singleLine = true,
            label = { Text(if (division) "Maximum Second number" else "Maximum number") },
            supportingText = { Text("Whole number from $MIN_MAXIMUM to ${"%,d".format(MAX_MAXIMUM)}") },
            isError = if (division) model.divisionMaximumSecondText.isNotEmpty() && model.divisionMaximumSecond == null
                else model.maximumText.isNotEmpty() && model.maximum == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().testTag("maximum-input"))
        Text("How many questions would you like?", style = MaterialTheme.typography.titleLarge)
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
    val numbers = if (model.selectedOperation == Operation.DIVISION)
        "first up to ${model.divisionMaximumFirst} · second up to ${model.divisionMaximumSecond}"
        else "numbers ${model.minimum} to ${model.maximum}"
    DialogCard {
        Text("Press Start when Ready", style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text("${model.selectedOperation?.label} · $numbers · ${model.questionCount} questions",
            style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
        if (model.rewardsEnabled && model.showTimer && model.timeLimitMinutes > 0) {
            Text("Time limit: ${model.timeLimitMinutes} minutes", modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
        Button(onClick = model::start, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("start-button")) {
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
            if (model.showTimer || model.activeTimeLimitMs != null) TimerText(model)
        }
        Column(Modifier.align(Alignment.Center).widthIn(max = 440.dp).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Question ${game.attempts.size + 1} of ${game.questionCount}",
               style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("question-progress"))
            GameProgress(game.attempts.size, game.questionCount)
            Text("${game.problem.text} = ?", style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.testTag("problem"))
            OutlinedTextField(model.answerText, model::updateAnswer, singleLine = true,
                label = { Text("Your answer") },
                textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { model.submitAnswer() }),
                modifier = Modifier.fillMaxWidth().focusRequester(focus).testTag("answer-input"))
            Button(onClick = model::submitAnswer, enabled = parseAnswer(model.answerText) != null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("submit-answer")) { Text("Submit") }
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
internal fun GameProgress(completed: Int, total: Int, modifier: Modifier = Modifier) {
    val position = remember(total) { Animatable(completed.toFloat()) }
    LaunchedEffect(completed, total) {
        position.animateTo(completed.toFloat(), animationSpec = tween(500))
    }
    BoxWithConstraints(modifier.fillMaxWidth().height(64.dp).testTag("game-progress").semantics {
        contentDescription = "Practice progress"
        stateDescription = "$completed of $total questions answered"
        progressBarRangeInfo = ProgressBarRangeInfo(completed.toFloat() / total, 0f..1f, total - 1)
    }) {
        val travel = (maxWidth - 32.dp).coerceAtLeast(0.dp)
        LinearProgressIndicator(progress = { position.value / total },
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp).height(8.dp).clearAndSetSemantics {},
            drawStopIndicator = {})
        Icon(Icons.Rounded.RocketLaunch, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.offset {
                val hop = sin(PI * (position.value % 1f)).toFloat()
                IntOffset((travel.toPx() * position.value / total).roundToInt(), (24.dp.toPx() - 20.dp.toPx() * hop).roundToInt())
            }.size(32.dp).testTag("progress-rocket"))
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
        val limit = model.activeTimeLimitMs
        Text(if (limit == null) formatDuration(elapsed) else "Left: ${formatDuration(limit - elapsed)}",
            style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("timer"))
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
                    Text("${result.operation.label} · ${result.numberDescription}",
                        style = MaterialTheme.typography.bodyMedium)
                    Text("Wrong: ${result.wrong} · Time: ${formatDuration(result.durationMs)}",
                        style = MaterialTheme.typography.bodyMedium)
                    if (result.timedOut) Text("Time's up!", color = WrongRed)
                    result.prize?.let { prize ->
                        RewardImage(prize.type, fragment = true, modifier = Modifier.size(88.dp))
                        Text("Prize: ${prize.type.fragmentLabel}", style = MaterialTheme.typography.titleMedium)
                        Text("Total collected: ${prize.balance.totalFragments} fragments",
                            style = MaterialTheme.typography.bodyMedium)
                        Text("${prize.balance.whole} ${prize.type.pluralLabel} · ${prize.balance.fragments}/3 fragments",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    if (result.prizeType != null && result.prize == null) {
                        Text("Your prize is waiting!", style = MaterialTheme.typography.titleMedium)
                    }
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
