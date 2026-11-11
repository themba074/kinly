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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Visibility
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
import com.kinly.app.ui.theme.Fresh100
import com.kinly.app.ui.theme.Fresh600
import com.kinly.app.ui.theme.Ink600
import com.kinly.app.ui.theme.KinlyTheme
import com.kinly.app.ui.theme.Ocean700
import com.kinly.app.ui.theme.Ocean900
import com.kinly.app.ui.theme.Sky100
import com.kinly.app.ui.theme.Stale100
import com.kinly.app.ui.theme.Stale600

internal enum class SharingPreviewMode(val label: String) {
    Paused("Paused"),
    Approximate("Approximate"),
    Precise("Precise"),
    Timed("Timed"),
}

internal enum class SharingPreviewAvailability(val label: String) {
    Ready("Ready"),
    Unavailable("Unavailable"),
}

private data class SharingModeContent(
    val title: String,
    val description: String,
    val detail: String,
    val icon: ImageVector,
    val accent: Color,
    val background: Color,
)

private fun contentFor(mode: SharingPreviewMode): SharingModeContent = when (mode) {
    SharingPreviewMode.Paused -> SharingModeContent(
        title = "Sharing paused",
        description = "No current or last-known location is shown while sharing is paused.",
        detail = "Visible to: no one",
        icon = Icons.Rounded.LocationOff,
        accent = Stale600,
        background = Stale100,
    )
    SharingPreviewMode.Approximate -> SharingModeContent(
        title = "Approximate area",
        description = "Shares a server-protected area. Your exact position stays hidden.",
        detail = "Audience: Family circle · 3 people",
        icon = Icons.Rounded.Visibility,
        accent = Ocean700,
        background = Sky100,
    )
    SharingPreviewMode.Precise -> SharingModeContent(
        title = "Precise sharing",
        description = "Would share a precise position with visible freshness and accuracy.",
        detail = "Audience: Family circle · 3 people",
        icon = Icons.Rounded.MyLocation,
        accent = Fresh600,
        background = Fresh100,
    )
    SharingPreviewMode.Timed -> SharingModeContent(
        title = "Timed precise sharing",
        description = "Would share precisely only until the stated expiry.",
        detail = "Ends at 18:00 SAST (UTC+2) · synthetic example",
        icon = Icons.Rounded.Schedule,
        accent = Ocean700,
        background = Sky100,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SharingControlsPreviewScreen(
    initialMode: SharingPreviewMode = SharingPreviewMode.Paused,
    initialAvailability: SharingPreviewAvailability = SharingPreviewAvailability.Ready,
    onBack: () -> Unit = {},
) {
    var mode by remember(initialMode) { mutableStateOf(initialMode) }
    var availability by remember(initialAvailability) { mutableStateOf(initialAvailability) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Sharing preview", color = Ocean900, fontWeight = FontWeight.Bold)
                },
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
        bottomBar = {
            KinlyNavigationBar(selected = Destination.Settings, onSelected = {})
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("sharing_preview_list")
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { SyntheticSharingNotice() }
            item {
                AvailabilitySelector(
                    selected = availability,
                    onSelected = { availability = it },
                )
            }
            item {
                ModeSelector(
                    selected = mode,
                    enabled = availability == SharingPreviewAvailability.Ready,
                    onSelected = { mode = it },
                )
            }
            if (availability == SharingPreviewAvailability.Ready) {
                item { SharingModeCard(contentFor(mode)) }
                item { ConsentOwnershipCard() }
            } else {
                item { UnavailableSharingCard() }
            }
            item {
                Text(
                    text = "Preview only. Changes are not saved and no location permission is requested.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink600,
                )
            }
        }
    }
}

@Composable
private fun SyntheticSharingNotice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Synthetic sharing controls preview. No setting is saved."
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
                Text("Synthetic sharing preview", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("No setting is saved and no location data is used.", color = Ink600)
            }
        }
    }
}

@Composable
private fun AvailabilitySelector(
    selected: SharingPreviewAvailability,
    onSelected: (SharingPreviewAvailability) -> Unit,
) {
    SelectorGroup(title = "Preview state") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SharingPreviewAvailability.entries.forEach { state ->
                PreviewFilterChip(
                    label = state.label,
                    selected = selected == state,
                    onClick = { onSelected(state) },
                )
            }
        }
    }
}

@Composable
private fun ModeSelector(
    selected: SharingPreviewMode,
    enabled: Boolean,
    onSelected: (SharingPreviewMode) -> Unit,
) {
    SelectorGroup(title = "Sharing mode") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SharingPreviewMode.entries.chunked(2).forEach { rowModes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowModes.forEach { mode ->
                        PreviewFilterChip(
                            label = mode.label,
                            selected = selected == mode,
                            enabled = enabled,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sharing_mode_${mode.name.lowercase()}"),
                            onClick = { onSelected(mode) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
            color = Ocean900,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun PreviewFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Sky100,
            selectedLabelColor = Ocean900,
        ),
    )
}

@Composable
private fun SharingModeCard(content: SharingModeContent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Selected sharing mode"
                stateDescription = "${content.title}. ${content.description}. ${content.detail}"
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = content.background),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = content.icon,
                contentDescription = null,
                tint = content.accent,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = content.title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                color = Ocean900,
                fontWeight = FontWeight.Bold,
            )
            Text(content.description, color = Ocean900)
            Text(content.detail, color = Ink600)
        }
    }
}

@Composable
private fun ConsentOwnershipCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Consent ownership"
                stateDescription = "Only you can change your sharing choice"
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Rounded.Groups,
                contentDescription = null,
                tint = Ocean700,
                modifier = Modifier.size(32.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your choice", color = Ocean900, fontWeight = FontWeight.Bold)
                Text("Only you can change your sharing mode.", color = Ocean900)
                Text("Circle administrators cannot override an adult member's choice.", color = Ink600)
            }
        }
    }
}

@Composable
private fun UnavailableSharingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Sharing controls unavailable"
                stateDescription = "Controls disabled. No setting changed."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Stale100),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = Stale600,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = "Sharing controls unavailable",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                color = Ocean900,
                fontWeight = FontWeight.Bold,
            )
            Text("The preview cannot show an editable sharing state.", color = Ocean900)
            Text("Controls are disabled and no setting changed.", color = Ink600)
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SharingControlsPreview() {
    KinlyTheme { SharingControlsPreviewScreen() }
}
