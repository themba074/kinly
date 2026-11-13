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
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Visibility
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
import com.kinly.app.ui.theme.Fresh100
import com.kinly.app.ui.theme.Fresh600
import com.kinly.app.ui.theme.Ink600
import com.kinly.app.ui.theme.KinlyTheme
import com.kinly.app.ui.theme.Ocean700
import com.kinly.app.ui.theme.Ocean900
import com.kinly.app.ui.theme.Sky100
import com.kinly.app.ui.theme.Stale100
import com.kinly.app.ui.theme.Stale600

internal enum class CareSafetyPreviewState(val label: String) {
    NoActivity("No activity"),
    Overview("Overview"),
    CheckIns("Check-ins"),
    Journeys("Journeys"),
    Offline("Offline"),
    Unavailable("Unavailable"),
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CareSafetyPreviewScreen(
    initialState: CareSafetyPreviewState = CareSafetyPreviewState.Overview,
    onBack: () -> Unit = {},
) {
    var previewState by remember(initialState) { mutableStateOf(initialState) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Care and Safety preview", color = Ocean900, fontWeight = FontWeight.Bold) },
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
        bottomBar = { KinlyNavigationBar(selected = Destination.Safety, onSelected = {}) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("care_safety_preview_list")
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { CareSafetySyntheticNotice() }
            item {
                CareSafetyStateSelector(
                    selected = previewState,
                    onSelected = { previewState = it },
                )
            }
            when (previewState) {
                CareSafetyPreviewState.NoActivity -> item { NoCareActivityCard() }
                CareSafetyPreviewState.Overview -> {
                    item { CareSafetySectionTitle("Care overview") }
                    item { EmergencyBoundaryCard() }
                    item { CheckInOverviewCard() }
                    item { JourneyOverviewCard() }
                }
                CareSafetyPreviewState.CheckIns -> {
                    item { CareSafetySectionTitle("Synthetic check-in lifecycle") }
                    item { CheckInCard("Sent", "Delivery unknown", "No recipient delivery is claimed.", Stale600, Stale100) }
                    item { CheckInCard("Delivered", "Response unknown", "No response is not treated as danger.", Fresh600, Fresh100) }
                    item { CheckInCard("Responded", "I’m okay", "‘Call me’ and ‘Dismiss’ are read-only response examples.", Fresh600, Fresh100) }
                    item { CheckInCard("Expired", "No response", "Expiry does not mean the person is in danger.", Stale600, Stale100) }
                    item { CheckInCard("Failed", "Not delivered", "No person is described as notified.", Stale600, Stale100) }
                }
                CareSafetyPreviewState.Journeys -> {
                    item { CareSafetySectionTitle("Synthetic journey lifecycle") }
                    item {
                        JourneyCard(
                            title = "Active journey · synthetic",
                            status = "Active until 18:30 SAST (16:30 UTC)",
                            details = listOf(
                                "Audience: 2 selected adults",
                                "Maximum duration: 60 minutes",
                                "ETA: about 24 minutes · estimate only",
                                "Updated 3 minutes ago · medium confidence",
                                "No route, map, coordinate, or live location is shown",
                            ),
                            accent = Fresh600,
                            background = Fresh100,
                        )
                    }
                    item {
                        JourneyCard(
                            title = "Ended journey · synthetic",
                            status = "Ended manually at 17:42 SAST",
                            details = listOf("No longer shared", "Ending is not presented as an arrival"),
                            accent = Ocean700,
                            background = Sky100,
                        )
                    }
                    item {
                        JourneyCard(
                            title = "Expired journey · synthetic",
                            status = "Expired at its maximum duration",
                            details = listOf("No longer shared", "Consent was not extended automatically"),
                            accent = Stale600,
                            background = Stale100,
                        )
                    }
                }
                CareSafetyPreviewState.Offline -> item { OfflineCareSafetyCard() }
                CareSafetyPreviewState.Unavailable -> item { UnavailableCareSafetyCard() }
            }
            item {
                Text(
                    "Preview only. No check-in, journey, contact, notification, call, or emergency action is available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink600,
                )
            }
        }
    }
}

@Composable
private fun CareSafetySyntheticNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth().semantics {
            contentDescription = "Synthetic Care and Safety preview. No real contact, message, journey, location, or emergency data is used."
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
                Text("Synthetic Care and Safety preview", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("No real contact, message, journey, location, or emergency data is used.", color = Ink600)
            }
        }
    }
}

@Composable
private fun CareSafetyStateSelector(
    selected: CareSafetyPreviewState,
    onSelected: (CareSafetyPreviewState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CareSafetySectionTitle("Preview state")
        CareSafetyPreviewState.entries.chunked(3).forEach { states ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                states.forEach { state ->
                    FilterChip(
                        selected = selected == state,
                        onClick = { onSelected(state) },
                        label = { Text(state.label) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                            .testTag("care_safety_state_${state.name.lowercase()}"),
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
private fun NoCareActivityCard() = CareSafetyStateCard(
    icon = Icons.Rounded.HourglassEmpty,
    title = "No care activity",
    state = "No check-in or journey is active",
) {
    Text("Check-ins and journeys will appear here when those product features exist.", color = Ink600)
    Text("No send or start action is available in this preview.", color = Ocean900, fontWeight = FontWeight.SemiBold)
    Text("Emergency tools are not configured.", color = Ink600)
}

@Composable
private fun EmergencyBoundaryCard() = CareSafetyStateCard(
    icon = Icons.Rounded.VerifiedUser,
    title = "Emergency tools not configured",
    state = "No emergency recipient or channel configured",
    background = Sky100,
) {
    Text("No circle contact, phone number, emergency service, or responder is notified or called.", color = Ocean900)
    Text("No help or dispatch claim is made.", color = Ink600, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun CheckInOverviewCard() = CareSafetyStateCard(
    icon = Icons.Rounded.CheckCircle,
    title = "Latest check-in · synthetic",
    state = "Delivered · response unknown",
    background = Fresh100,
) {
    Text("Sent at 17:20 SAST · delivery confirmed", color = Ink600)
    Text("No response is not treated as danger.", color = Ocean900, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun JourneyOverviewCard() = CareSafetyStateCard(
    icon = Icons.AutoMirrored.Rounded.DirectionsWalk,
    title = "Journey · synthetic",
    state = "Active · medium confidence",
    background = Fresh100,
) {
    Text("Audience: 2 selected adults · expires 18:30 SAST", color = Ink600)
    Text("ETA about 24 minutes · updated 3 minutes ago · estimate only", color = Ocean900)
}

@Composable
private fun CheckInCard(
    title: String,
    status: String,
    detail: String,
    accent: Color,
    background: Color,
) = CareSafetyStateCard(
    icon = Icons.Rounded.CheckCircle,
    title = "$title check-in · synthetic",
    state = status,
    accent = accent,
    background = background,
) {
    Text(status, color = Ocean900, fontWeight = FontWeight.SemiBold)
    Text(detail, color = Ink600)
}

@Composable
private fun JourneyCard(
    title: String,
    status: String,
    details: List<String>,
    accent: Color,
    background: Color,
) = CareSafetyStateCard(
    icon = Icons.Rounded.Schedule,
    title = title,
    state = status,
    accent = accent,
    background = background,
) {
    Text(status, color = Ocean900, fontWeight = FontWeight.SemiBold)
    details.forEach { Text(it, color = Ink600) }
}

@Composable
private fun OfflineCareSafetyCard() = CareSafetyStateCard(
    icon = Icons.Rounded.CloudOff,
    title = "Care status offline",
    state = "Check-in delivery unknown and journey estimate stale",
    accent = Stale600,
    background = Stale100,
) {
    Text("Check-in delivery is unknown while offline.", color = Ocean900)
    Text("Journey ETA is stale · last updated 28 minutes ago.", color = Ink600)
    Text("No response, safety, or arrival is inferred.", color = Ocean900, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun UnavailableCareSafetyCard() = CareSafetyStateCard(
    icon = Icons.Rounded.WarningAmber,
    title = "Care and Safety unavailable",
    state = "No current care activity available",
    accent = Stale600,
    background = Stale100,
) {
    Text("Current synthetic check-in and journey information cannot be shown.", color = Ink600)
    Text("No action was attempted and no notification was sent.", color = Ocean900, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun CareSafetyStateCard(
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
private fun CareSafetySectionTitle(title: String) {
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
private fun CareSafetyPreview() {
    KinlyTheme { CareSafetyPreviewScreen() }
}
