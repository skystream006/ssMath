@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.ssmath.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

@Composable
private fun ScreenScaffold(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back") }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            windowInsets = WindowInsets(0))
        content()
    }
}

@Composable
fun SettingsScreen(model: MathViewModel) {
    ScreenScaffold("Settings", model::closeOverlay) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ListItem(headlineContent = { Text("Practice History") },
                supportingContent = { Text("Review your previous results") },
                leadingContent = { Icon(Icons.Rounded.History, null) },
                trailingContent = { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable(onClick = model::openHistory).testTag("practice-history"))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Show timer")
                    Text("Display the elapsed time while practicing", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(model.showTimer, model::chooseShowTimer,
                    modifier = Modifier.semantics { contentDescription = "Show timer" })
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Show correct answers")
                    Text("Reveal correct answers after mistakes, including in results and history",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(model.showCorrectAnswers, model::chooseShowCorrectAnswers,
                    modifier = Modifier.semantics { contentDescription = "Show correct answers" })
            }
            HorizontalDivider()
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                listOf("Blue Wave", "Color theme").forEachIndexed { index, label ->
                    SegmentedButton(selected = model.waveAppearance == (index == 0),
                        onClick = { model.chooseWaveAppearance(index == 0) },
                        shape = SegmentedButtonDefaults.itemShape(index, 2)) { Text(label) }
                }
            }
            if (!model.waveAppearance) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    COLOR_THEMES.forEach { (id, color) ->
                        Box(Modifier.size(48.dp).semantics { contentDescription = "$id theme"; selected = model.theme == id }
                            .clickable { model.chooseTheme(id) }.padding(6.dp)
                            .border(if (model.theme == id) 3.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            .padding(5.dp).background(Color(color), CircleShape))
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark appearance", Modifier.weight(1f))
                    Switch(isDarkTheme(model.theme, model.mode), model::chooseDark)
                }
            }
            SkinSetting(model.skinsEnabled, model.skin, model::chooseSkin, model::chooseSkins)
            HorizontalDivider()
            UpdateSettings()
            HorizontalDivider()
            DebugLogSettings()
            Text("ssMath ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 20.dp))
        }
    }
}

@Composable
fun HistoryScreen(model: MathViewModel) {
    var confirmClear by rememberSaveable { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val detail = model.historyDetail
    if (detail != null) {
        ScreenScaffold("Practice result", model::closeOverlay, actions = {
            IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Rounded.Delete, contentDescription = "Delete result") }
        }) {
            ResultsContent(detail, title = null, showCorrectAnswers = model.showCorrectAnswers) {
                OutlinedButton(onClick = model::closeOverlay, modifier = Modifier.widthIn(min = 160.dp)) { Text("Back") }
            }
        }
    } else {
        ScreenScaffold("Practice History", model::closeOverlay, actions = {
            if (model.history.isNotEmpty()) IconButton(onClick = { confirmClear = true }) {
                Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear history")
            }
        }) {
            if (model.history.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No practice results yet. Finish a practice to see it here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().testTag("history-list"), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(model.history, key = { it.id }) { result ->
                        ListItem(
                            headlineContent = { Text("${result.operation.label} · ${result.minimum} to ${result.maximum}") },
                            supportingContent = {
                                Text("${formatFinishedAt(result.finishedAt)}\n${result.correct} right · ${result.wrong} wrong · ${formatDuration(result.durationMs)}")
                            },
                            leadingContent = {
                                Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center) {
                                    Text(result.operation.symbol, style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            },
                            trailingContent = { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable { model.showHistoryDetail(result) })
                    }
                }
            }
        }
    }
    if (confirmClear) {
        AlertDialog(onDismissRequest = { confirmClear = false },
            title = { Text("Clear practice history?") },
            text = { Text("All saved results will be permanently deleted.") },
            confirmButton = { TextButton(onClick = { confirmClear = false; model.clearHistory() }) { Text("Clear") } },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } })
    }
    if (confirmDelete && detail != null) {
        AlertDialog(onDismissRequest = { confirmDelete = false },
            title = { Text("Delete this result?") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; model.deleteResult(detail.id) }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } })
    }
}

@Composable
internal fun SkinSetting(
    enabled: Boolean,
    skin: AppSkin,
    onSkinChange: (AppSkin) -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    var showSkinPicker by rememberSaveable(enabled) { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f).heightIn(min = 48.dp).clip(MaterialTheme.shapes.small)
            .clickable(enabled = enabled, role = Role.Button, onClickLabel = "Choose skin") { showSkinPicker = true }
            .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Skins")
                Text(skin.label, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (enabled) Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null)
        }
        Switch(enabled, onEnabledChange, modifier = Modifier.semantics { contentDescription = "Skins" })
    }
    if (enabled && showSkinPicker) {
        SkinPickerDialog(skin, onSkinChange = {
            onSkinChange(it)
            showSkinPicker = false
        }, dismiss = { showSkinPicker = false })
    }
}

@Composable
private fun SkinPickerDialog(skin: AppSkin, onSkinChange: (AppSkin) -> Unit, dismiss: () -> Unit) {
    AlertDialog(onDismissRequest = dismiss, title = { Text("Choose skin") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Background images • Your color theme stays the same", style = MaterialTheme.typography.bodySmall)
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppSkin.entries.forEach { option ->
                    val selected = skin == option
                    Row(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .border(if (selected) 2.dp else 1.dp,
                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            MaterialTheme.shapes.medium)
                        .selectable(selected, role = Role.RadioButton, onClick = { onSkinChange(option) })
                        .padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Image(painterResource(option.drawable), contentDescription = null,
                            modifier = Modifier.size(width = 64.dp, height = 112.dp).clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop)
                        Column(Modifier.weight(1f)) {
                            Text(option.label, style = MaterialTheme.typography.titleSmall)
                            Text(option.description, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        RadioButton(selected, onClick = null)
                    }
                }
            }
        }
    }, confirmButton = { TextButton(onClick = dismiss) { Text("Close") } })
}

@Composable
internal fun CollapsibleSettingsSection(
    title: String,
    defaultExpanded: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    var manualExpansion by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val expanded = manualExpansion ?: defaultExpanded
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" }
                .clickable(role = Role.Button, onClickLabel = if (expanded) "Collapse $title" else "Expand $title") {
                    manualExpansion = !expanded
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, null)
        }
        if (expanded) content()
    }
}
