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
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WifiOff
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

internal enum class PlacesPreviewState(val label: String) {
    Loading("Loading"),
    NoPlaces("No places"),
    Overview("Overview"),
    Uncertain("Uncertain"),
    Offline("Offline"),
    Unavailable("Unavailable"),
}

private data class SyntheticPlacePreview(
    val name: String,
    val radius: String,
    val alertSummary: String,
    val detail: String,
    val accent: Color,
    val background: Color,
)

private val syntheticPlaces = listOf(
    SyntheticPlacePreview(
        name = "Home · synthetic",
        radius = "250 m radius",
        alertSummary = "1 adult opted in · 1 adult paused",
        detail = "No arrival or departure alert has been sent",
        accent = Fresh600,
        background = Fresh100,
    ),
    SyntheticPlacePreview(
        name = "Community centre · synthetic",
        radius = "150 m radius",
        alertSummary = "No adults opted in",
        detail = "No location or alert activity",
        accent = Ocean700,
        background = Sky100,
    ),
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun PlacesPreviewScreen(
    initialState: PlacesPreviewState = PlacesPreviewState.Overview,
    onBack: () -> Unit = {},
) {
    var previewState by remember(initialState) { mutableStateOf(initialState) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Places preview", color = Ocean900, fontWeight = FontWeight.Bold) },
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
            KinlyNavigationBar(selected = Destination.Places, onSelected = {})
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("places_preview_list")
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { SyntheticPlacesNotice() }
            item {
                PlacesStateSelector(
                    selected = previewState,
                    onSelected = { previewState = it },
                )
            }
            when (previewState) {
                PlacesPreviewState.Loading -> item { LoadingPlacesCard() }
                PlacesPreviewState.NoPlaces -> item { NoPlacesCard() }
                PlacesPreviewState.Overview -> {
                    item { PlacesOverviewHeader() }
                    item { PlaceConsentCard() }
                    syntheticPlaces.forEach { place -> item { SyntheticPlaceCard(place) } }
                }
                PlacesPreviewState.Uncertain -> item { UncertainPlaceCard() }
                PlacesPreviewState.Offline -> item { OfflinePlacesCard() }
                PlacesPreviewState.Unavailable -> item { UnavailablePlacesCard() }
            }
            item {
                Text(
                    text = "Preview only. No place, alert preference, location, or notification is changed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink600,
                )
            }
        }
    }
}

@Composable
private fun SyntheticPlacesNotice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Synthetic Places preview. No address, coordinate, or alert data is used."
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
                Text("Synthetic Places preview", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("No real address, coordinate, member, or alert data is used.", color = Ink600)
            }
        }
    }
}

@Composable
private fun PlacesStateSelector(
    selected: PlacesPreviewState,
    onSelected: (PlacesPreviewState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PlacesSectionTitle("Preview state")
        PlacesPreviewState.entries.chunked(3).forEach { rowStates ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowStates.forEach { state ->
                    FilterChip(
                        selected = selected == state,
                        onClick = { onSelected(state) },
                        label = { Text(state.label) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("places_state_${state.name.lowercase()}"),
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
private fun LoadingPlacesCard() {
    PlacesStateCard(
        icon = Icons.Rounded.HourglassEmpty,
        title = "Loading synthetic places",
        stateDescription = "Preview loading only. No location check or notification is running.",
        background = Sky100,
    ) {
        Text("Kinly is preparing this synthetic preview state.", color = Ocean900)
        Text("No location check or notification is running.", color = Ink600)
    }
}

@Composable
private fun NoPlacesCard() {
    PlacesStateCard(
        icon = Icons.Rounded.LocationOff,
        title = "No places yet",
        stateDescription = "No saved places and no setup action available",
        background = MaterialTheme.colorScheme.surface,
    ) {
        Text("Saved places will appear here when that product feature exists.", color = Ink600)
        Text(
            "Adding a place would never enable an adult's location sharing automatically.",
            color = Ocean900,
            fontWeight = FontWeight.SemiBold,
        )
        Text("No create or setup action is available in this preview.", color = Ink600)
    }
}

@Composable
private fun PlacesOverviewHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Family circle places · synthetic",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineSmall,
            color = Ocean900,
            fontWeight = FontWeight.Bold,
        )
        Text("2 saved place examples · no live location", color = Ink600)
    }
}

@Composable
private fun PlaceConsentCard() {
    PlacesStateCard(
        icon = Icons.Rounded.VerifiedUser,
        title = "Place alerts require consent",
        stateDescription = "Each adult controls whether their location may power a place alert",
        background = Sky100,
    ) {
        Text("Each adult chooses whether their location may power a place alert.", color = Ocean900)
        Text("Circle membership or an administrator role cannot enable it for them.", color = Ink600)
    }
}

@Composable
private fun SyntheticPlaceCard(place: SyntheticPlacePreview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = place.name
                stateDescription = "${place.radius}. ${place.alertSummary}. ${place.detail}."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = place.background),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                Icons.Rounded.Place,
                contentDescription = null,
                tint = place.accent,
                modifier = Modifier.size(32.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(place.name, color = Ocean900, fontWeight = FontWeight.Bold)
                Text(place.radius, color = place.accent, fontWeight = FontWeight.SemiBold)
                Text(place.alertSummary, color = Ocean900)
                Text(place.detail, color = Ink600)
            }
        }
    }
}

@Composable
private fun UncertainPlaceCard() {
    PlacesStateCard(
        icon = Icons.Rounded.Schedule,
        title = "Arrival not confirmed",
        stateDescription = "Location uncertainty overlaps the synthetic place boundary",
        background = Stale100,
    ) {
        Text("Location uncertainty overlaps the synthetic place boundary.", color = Ocean900)
        Text("Kinly would wait for a more accurate update instead of claiming an arrival.", color = Ink600)
        Text("No arrival or departure alert has been sent.", color = Stale600, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OfflinePlacesCard() {
    PlacesStateCard(
        icon = Icons.Rounded.WifiOff,
        title = "Places offline",
        stateDescription = "No current place state or alert delivery available",
        background = Stale100,
    ) {
        Text("The last synthetic place state is not treated as current.", color = Ocean900)
        Text("No arrival, departure, or alert delivery is claimed while offline.", color = Ink600)
    }
}

@Composable
private fun UnavailablePlacesCard() {
    PlacesStateCard(
        icon = Icons.Rounded.CloudOff,
        title = "Places unavailable",
        stateDescription = "No place or alert setting available and no change made",
        background = Stale100,
    ) {
        Text("Kinly could not show this synthetic Places state.", color = Ocean900)
        Text("No place or alert preference was changed.", color = Ink600)
    }
}

@Composable
private fun PlacesStateCard(
    icon: ImageVector,
    title: String,
    stateDescription: String,
    background: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = title
                this.stateDescription = stateDescription
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

@Composable
private fun PlacesSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.titleLarge,
        color = Ocean900,
        fontWeight = FontWeight.Bold,
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PlacesPreview() {
    KinlyTheme { PlacesPreviewScreen() }
}
