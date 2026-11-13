package com.kinly.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kinly.app.ui.theme.Border
import com.kinly.app.ui.theme.Ink600
import com.kinly.app.ui.theme.KinlyTheme
import com.kinly.app.ui.theme.Ocean700
import com.kinly.app.ui.theme.Ocean900
import com.kinly.app.ui.theme.Sky100
import com.kinly.app.ui.theme.Stale100
import com.kinly.app.ui.theme.Stale600

internal enum class HistoryPrivacyPreviewState(val label: String) {
    Loading("Loading"),
    NoHistory("No history"),
    Overview("Overview"),
    PrivacyAccess("Privacy"),
    Offline("Offline"),
    Unavailable("Unavailable"),
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HistoryPrivacyPreviewScreen(
    initialState: HistoryPrivacyPreviewState = HistoryPrivacyPreviewState.Overview,
    onBack: () -> Unit = {},
) {
    var previewState by remember(initialState) { mutableStateOf(initialState) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("History and privacy preview", color = Ocean900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to UI showcase",
                            tint = Ocean700,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = { KinlyNavigationBar(selected = Destination.History, onSelected = {}) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("history_privacy_preview_list")
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { SyntheticHistoryNotice() }
            item {
                HistoryStateSelector(
                    selected = previewState,
                    onSelected = { previewState = it },
                )
            }
            when (previewState) {
                HistoryPrivacyPreviewState.Loading -> item { LoadingHistoryCard() }
                HistoryPrivacyPreviewState.NoHistory -> item { NoHistoryCard() }
                HistoryPrivacyPreviewState.Overview -> {
                    item { HistoryPrivacySectionTitle("Your synthetic history") }
                    item { OwnHistoryBoundaryCard() }
                    item {
                        HistorySummaryCard(
                            title = "Today · synthetic",
                            state = "Approximate and stale",
                            lines = listOf(
                                "Approximate area · exact position hidden",
                                "Updated 35 minutes ago · not live",
                                "2-hour gap · no movement is inferred",
                            ),
                            accent = Stale600,
                            background = Stale100,
                        )
                    }
                    item {
                        HistorySummaryCard(
                            title = "Yesterday · synthetic",
                            state = "Sharing paused",
                            lines = listOf(
                                "No position is shown during the paused period",
                                "A gap is not treated as proof of where someone was",
                            ),
                        )
                    }
                }
                HistoryPrivacyPreviewState.PrivacyAccess -> {
                    item { HistoryPrivacySectionTitle("History access") }
                    item { OwnHistoryAccessCard() }
                    item { NoGrantCard() }
                    item { RevokedHistoryAccessCard() }
                    item { RetentionBoundaryCard() }
                }
                HistoryPrivacyPreviewState.Offline -> item { OfflineHistoryCard() }
                HistoryPrivacyPreviewState.Unavailable -> item { UnavailableHistoryCard() }
            }
            item {
                Text(
                    "Preview only. No history, access grant, export, deletion request, or retention setting is loaded or changed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink600,
                )
            }
        }
    }
}

@Composable
private fun SyntheticHistoryNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth().semantics {
            contentDescription = "Synthetic History and privacy preview. No real movement, location, account, or retention data is used."
        },
        shape = RoundedCornerShape(16.dp),
        color = Sky100,
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Visibility, contentDescription = null, tint = Ocean700)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Synthetic History and privacy preview", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("No real movement, location, account, or retention data is used.", color = Ink600)
            }
        }
    }
}

@Composable
private fun HistoryStateSelector(
    selected: HistoryPrivacyPreviewState,
    onSelected: (HistoryPrivacyPreviewState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HistoryPrivacySectionTitle("Preview state")
        HistoryPrivacyPreviewState.entries.chunked(3).forEach { states ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                states.forEach { state ->
                    FilterChip(
                        selected = selected == state,
                        onClick = { onSelected(state) },
                        label = { Text(state.label) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("history_privacy_state_${state.name.lowercase()}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Sky100,
                            selectedLabelColor = Ocean900,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingHistoryCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.HourglassEmpty,
    title = "Preparing synthetic history",
    state = "Preview loading only",
    background = Sky100,
) {
    Text("No history request, location lookup, or network call is running.", color = Ink600)
}

@Composable
private fun NoHistoryCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.History,
    title = "No synthetic history",
    state = "No history entries available",
) {
    Text("No history entries are shown in this preview state.", color = Ink600)
    Text("This does not claim that location collection failed.", color = Ocean900, fontWeight = FontWeight.SemiBold)
    Text("No retention policy is configured and no setup action is available.", color = Ink600)
}

@Composable
private fun OwnHistoryBoundaryCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.Lock,
    title = "Private by default",
    state = "Only your own synthetic history is visible",
    background = Sky100,
) {
    Text("Only your own synthetic history is visible in this state.", color = Ocean900)
    Text("Another adult would require a specific active, revocable grant.", color = Ink600)
}

@Composable
private fun HistorySummaryCard(
    title: String,
    state: String,
    lines: List<String>,
    accent: Color = Ocean700,
    background: Color = MaterialTheme.colorScheme.surface,
) = HistoryPrivacyStateCard(
    icon = Icons.Rounded.Schedule,
    title = title,
    state = state,
    accent = accent,
    background = background,
) {
    lines.forEach { Text(it, color = Ink600) }
}

@Composable
private fun OwnHistoryAccessCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.Visibility,
    title = "Your history · synthetic",
    state = "Visible to you only",
    background = Sky100,
) {
    Text("This read-only example belongs to the current synthetic viewer.", color = Ink600)
}

@Composable
private fun NoGrantCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.VisibilityOff,
    title = "Another adult's history",
    state = "No access grant",
) {
    Text("Circle membership or an administrator role does not grant history access.", color = Ocean900, fontWeight = FontWeight.SemiBold)
    Text("No last-known position, summary, or time range is exposed.", color = Ink600)
}

@Composable
private fun RevokedHistoryAccessCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.Lock,
    title = "Revoked access",
    state = "History unavailable immediately",
    background = Stale100,
    accent = Stale600,
) {
    Text("A revoked grant cannot be replaced by cached or previously viewed history.", color = Ocean900)
    Text("No history data is displayed after revocation.", color = Ink600)
}

@Composable
private fun RetentionBoundaryCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.Schedule,
    title = "Retention policy not configured",
    state = "No duration promised",
) {
    Text("Kinly's default retention still requires an approved ADR and legal review.", color = Ink600)
    Text("Export and deletion are future verified workflows, not actions in this preview.", color = Ocean900, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun OfflineHistoryCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.CloudOff,
    title = "History offline",
    state = "Last synthetic summary is stale",
    background = Stale100,
    accent = Stale600,
) {
    Text("The last synthetic summary is stale and is not treated as complete.", color = Ink600)
    Text("Missing periods are not filled in or inferred while offline.", color = Ocean900, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun UnavailableHistoryCard() = HistoryPrivacyStateCard(
    icon = Icons.Rounded.WarningAmber,
    title = "History unavailable",
    state = "No current history information available",
    background = Stale100,
    accent = Stale600,
) {
    Text("Current synthetic history and privacy information cannot be shown.", color = Ink600)
    Text("No request, deletion, export, or access change was attempted.", color = Ocean900, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun HistoryPrivacyStateCard(
    icon: ImageVector,
    title: String,
    state: String,
    accent: Color = Ocean700,
    background: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().semantics {
            contentDescription = title
            stateDescription = state
        },
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(icon, contentDescription = null, tint = accent)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, color = Ocean900, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                content()
            }
        }
    }
}

@Composable
private fun HistoryPrivacySectionTitle(title: String) {
    Text(
        title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.titleLarge,
        color = Ocean900,
        fontWeight = FontWeight.Bold,
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun HistoryPrivacyPreview() {
    KinlyTheme { HistoryPrivacyPreviewScreen() }
}
