package com.kinly.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kinly.app.presentation.BackendStatusUiState
import com.kinly.app.ui.theme.Border
import com.kinly.app.ui.theme.Danger100
import com.kinly.app.ui.theme.Danger600
import com.kinly.app.ui.theme.Fresh100
import com.kinly.app.ui.theme.Fresh600
import com.kinly.app.ui.theme.Ink600
import com.kinly.app.ui.theme.KinlyTheme
import com.kinly.app.ui.theme.Ocean700
import com.kinly.app.ui.theme.Ocean900
import com.kinly.app.ui.theme.Sky100
import com.kinly.app.ui.theme.Sky500
import com.kinly.app.ui.theme.Stale100
import com.kinly.app.ui.theme.Stale600

class UiShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KinlyTheme {
                UiShowcaseScreen()
            }
        }
    }
}

private data class ColorRole(
    val label: String,
    val meaning: String,
    val foreground: Color,
    val background: Color,
)

private val colorRoles = listOf(
    ColorRole("Information", "Selected and explanatory UI", Ocean700, Sky100),
    ColorRole("Fresh", "Available, current, delivered, or active", Fresh600, Fresh100),
    ColorRole("Attention", "Delayed, stale, or needs review", Stale600, Stale100),
    ColorRole("Danger", "Errors, destructive actions, and genuine emergencies", Danger600, Danger100),
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun UiShowcaseScreen() {
    var destination by remember { mutableStateOf(Destination.Home) }
    var showHomePreview by remember { mutableStateOf(false) }
    var showSharingPreview by remember { mutableStateOf(false) }
    var showCirclePreview by remember { mutableStateOf(false) }

    if (showHomePreview) {
        HomePreviewScreen(onBack = { showHomePreview = false })
        return
    }
    if (showSharingPreview) {
        SharingControlsPreviewScreen(onBack = { showSharingPreview = false })
        return
    }
    if (showCirclePreview) {
        CircleMembershipPreviewScreen(onBack = { showCirclePreview = false })
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Palette, contentDescription = null, tint = Ocean700)
                        Text("UI showcase", color = Ocean900, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            KinlyNavigationBar(
                selected = destination,
                onSelected = { destination = it },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ui_showcase_list")
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { PreviewNotice() }

            item {
                ShowcaseSectionHeader(
                    title = "Screen previews",
                    description = "Explore consent and freshness states without live data or product actions.",
                )
            }
            item {
                Button(
                    onClick = { showHomePreview = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Open Home preview")
                }
            }
            item {
                Button(
                    onClick = { showSharingPreview = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Open sharing controls preview")
                }
            }
            item {
                Button(
                    onClick = { showCirclePreview = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Open circle and membership preview")
                }
            }

            item {
                ShowcaseSectionHeader(
                    title = "Typography",
                    description = "Ocean Sky uses native type that remains readable at larger text sizes.",
                )
            }
            item { TypographySamples() }

            item {
                ShowcaseSectionHeader(
                    title = "Actions",
                    description = "These generic controls do not perform a product action.",
                )
            }
            item { ActionSamples() }

            item {
                ShowcaseSectionHeader(
                    title = "Service states",
                    description = "The real status component rendered with synthetic preview states.",
                )
            }
            item { BackendStatusCard(BackendStatusUiState.Checking, onRetry = {}) }
            item { BackendStatusCard(BackendStatusUiState.Available, onRetry = {}) }
            item { BackendStatusCard(BackendStatusUiState.Unavailable, onRetry = {}) }

            item {
                ShowcaseSectionHeader(
                    title = "Semantic colors",
                    description = "Color always appears with a plain-language meaning.",
                )
            }
            items(colorRoles) { role -> ColorRoleCard(role) }

            item {
                Text(
                    text = "Preview data only. No account, member, location, notification, or safety data is loaded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink600,
                )
            }
        }
    }
}

@Composable
private fun PreviewNotice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Synthetic preview only. No live product actions."
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
                Text("Synthetic preview only", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("Nothing on this screen performs a live Kinly action.", color = Ink600)
            }
        }
    }
}

@Composable
private fun ShowcaseSectionHeader(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineSmall,
            color = Ocean900,
            fontWeight = FontWeight.Bold,
        )
        Text(description, style = MaterialTheme.typography.bodyMedium, color = Ink600)
    }
}

@Composable
private fun TypographySamples() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Screen title", style = MaterialTheme.typography.headlineMedium, color = Ocean900, fontWeight = FontWeight.Bold)
            Text("Section title", style = MaterialTheme.typography.titleLarge, color = Ocean900, fontWeight = FontWeight.SemiBold)
            Text("Body text explains state in plain language.", style = MaterialTheme.typography.bodyLarge)
            Text("Secondary metadata remains readable.", style = MaterialTheme.typography.bodyMedium, color = Ink600)
        }
    }
}

@Composable
private fun ActionSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Primary action preview")
        }
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Secondary action preview")
        }
        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Unavailable action")
        }
    }
}

@Composable
private fun ColorRoleCard(role: ColorRole) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = role.background),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(role.foreground, CircleShape),
            )
            Column {
                Text(role.label, color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text(role.meaning, color = Ink600)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun UiShowcasePreview() {
    KinlyTheme {
        UiShowcaseScreen()
    }
}
