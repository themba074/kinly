package com.kinly.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
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

internal enum class HomePreviewState(val label: String) {
    NoCircle("No circle"),
    Circle("Circle"),
    Unavailable("Unavailable"),
}

private data class MemberPreview(
    val name: String,
    val status: String,
    val detail: String,
    val freshness: String,
    val uncertainty: String,
    val icon: ImageVector,
    val accent: Color,
    val background: Color,
)

private val memberPreviews = listOf(
    MemberPreview(
        name = "Jordan",
        status = "Fresh · precise",
        detail = "Shared place: Riverside Park",
        freshness = "Updated 2 min ago",
        uncertainty = "Accuracy ±35 m",
        icon = Icons.Rounded.MyLocation,
        accent = Fresh600,
        background = Fresh100,
    ),
    MemberPreview(
        name = "Sam",
        status = "Approximate area",
        detail = "Shared area: North district",
        freshness = "Updated 8 min ago",
        uncertainty = "Exact position is hidden",
        icon = Icons.Rounded.Visibility,
        accent = Ocean700,
        background = Sky100,
    ),
    MemberPreview(
        name = "Avery",
        status = "Stale · offline",
        detail = "No current location available",
        freshness = "Last update 2 hours ago",
        uncertainty = "Offline — do not treat as live",
        icon = Icons.Rounded.CloudOff,
        accent = Stale600,
        background = Stale100,
    ),
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomePreviewScreen(
    initialState: HomePreviewState = HomePreviewState.Circle,
    onBack: () -> Unit = {},
) {
    var previewState by remember(initialState) { mutableStateOf(initialState) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Home preview", color = Ocean900, fontWeight = FontWeight.Bold) },
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
            KinlyNavigationBar(selected = Destination.Home, onSelected = {})
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_preview_list")
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { SyntheticHomeNotice() }
            item { PreviewStateSelector(previewState, onSelected = { previewState = it }) }
            when (previewState) {
                HomePreviewState.NoCircle -> item { NoCirclePreview() }
                HomePreviewState.Circle -> {
                    item { CircleHeader() }
                    item { PausedSharingCard() }
                    item {
                        Text(
                            text = "Member sharing",
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.titleLarge,
                            color = Ocean900,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    memberPreviews.forEach { member -> item { MemberPreviewCard(member) } }
                }
                HomePreviewState.Unavailable -> item { UnavailableHomePreview() }
            }
        }
    }
}

@Composable
private fun SyntheticHomeNotice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Synthetic Home preview. No live circle or location data." },
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
                Text("Synthetic Home preview", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("No live circle or location data is used.", color = Ink600)
            }
        }
    }
}

@Composable
private fun PreviewStateSelector(
    selected: HomePreviewState,
    onSelected: (HomePreviewState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Preview state",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
            color = Ocean900,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HomePreviewState.entries.forEach { state ->
                FilterChip(
                    selected = selected == state,
                    onClick = { onSelected(state) },
                    label = { Text(state.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Sky100,
                        selectedLabelColor = Ocean900,
                    ),
                )
            }
        }
    }
}

@Composable
private fun NoCirclePreview() {
    StateCard(
        icon = Icons.Rounded.Groups,
        iconDescription = "No circle",
        title = "Start with trust",
        state = "No circle",
        background = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            "Create or join a private circle when that feature is available.",
            color = Ink600,
        )
        Text(
            "Joining a circle never enables location sharing automatically. You choose if, when, and how precisely to share.",
            color = Ocean900,
            fontWeight = FontWeight.Medium,
        )
        Text("No setup action is available in this preview.", color = Ink600)
    }
}

@Composable
private fun CircleHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Family circle · synthetic",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineSmall,
            color = Ocean900,
            fontWeight = FontWeight.Bold,
        )
        Text("See sharing choice, freshness, and uncertainty at a glance.", color = Ink600)
    }
}

@Composable
private fun PausedSharingCard() {
    StateCard(
        icon = Icons.Rounded.LocationOff,
        iconDescription = "Location sharing paused",
        title = "Your sharing",
        state = "Sharing paused",
        background = Stale100,
    ) {
        Text("Sharing paused", color = Ocean900, fontWeight = FontWeight.SemiBold)
        Text("No location is visible while sharing is paused.", color = Ocean900)
        Text("Visible to: no one", color = Ink600)
    }
}

@Composable
private fun MemberPreviewCard(member: MemberPreview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${member.name} sharing state"
                stateDescription = listOf(
                    member.status,
                    member.detail,
                    member.freshness,
                    member.uncertainty,
                ).joinToString(". ")
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = member.background),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = member.icon,
                contentDescription = null,
                tint = member.accent,
                modifier = Modifier.size(32.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(member.name, color = Ocean900, fontWeight = FontWeight.Bold)
                Text(member.status, color = member.accent, fontWeight = FontWeight.SemiBold)
                Text(member.detail, color = Ocean900)
                Text(member.freshness, color = Ink600)
                Text(member.uncertainty, color = Ink600)
            }
        }
    }
}

@Composable
private fun UnavailableHomePreview() {
    StateCard(
        icon = Icons.Rounded.CloudOff,
        iconDescription = "Circle unavailable",
        title = "Circle unavailable",
        state = "Unavailable preview state",
        background = Stale100,
    ) {
        Text("Kinly could not load this preview state.", color = Ocean900)
        Text("No live retry is offered because this screen has no service connection.", color = Ink600)
    }
}

@Composable
private fun StateCard(
    icon: ImageVector,
    iconDescription: String,
    title: String,
    state: String,
    background: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = iconDescription
                stateDescription = state
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = Ocean700, modifier = Modifier.size(32.dp))
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                color = Ocean900,
                fontWeight = FontWeight.Bold,
            )
            content()
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun HomePreview() {
    KinlyTheme { HomePreviewScreen() }
}
