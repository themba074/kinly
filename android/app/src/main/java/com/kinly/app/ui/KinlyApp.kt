package com.kinly.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinly.app.presentation.BackendStatusUiState
import com.kinly.app.presentation.BackendStatusViewModel
import com.kinly.app.ui.theme.Border
import com.kinly.app.ui.theme.Danger100
import com.kinly.app.ui.theme.Danger600
import com.kinly.app.ui.theme.Fresh100
import com.kinly.app.ui.theme.Fresh600
import com.kinly.app.ui.theme.Ink600
import com.kinly.app.ui.theme.Ocean700
import com.kinly.app.ui.theme.Ocean900
import com.kinly.app.ui.theme.Sky100

internal enum class Destination(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Rounded.Home),
    Places("Places", Icons.Rounded.LocationOn),
    Safety("Safety", Icons.Rounded.Security),
    History("History", Icons.Rounded.History),
    Settings("Settings", Icons.Rounded.Settings),
}

@Composable
fun KinlyApp(viewModel: BackendStatusViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    KinlyShell(state = state, onRetry = viewModel::refresh)
}

@Composable
fun KinlyShell(
    state: BackendStatusUiState,
    onRetry: () -> Unit,
) {
    var destination by remember { mutableStateOf(Destination.Home) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { KinlyTopBar() },
        bottomBar = {
            KinlyNavigationBar(
                selected = destination,
                onSelected = { destination = it },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (destination == Destination.Home) {
                HomeContent(state = state, onRetry = onRetry)
            } else {
                UnavailableDestination(destination = destination)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun KinlyTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = Ocean700,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Kinly",
                    color = Ocean900,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun HomeContent(
    state: BackendStatusUiState,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Care starts with connection",
                style = MaterialTheme.typography.headlineMedium,
                color = Ocean900,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "This early build checks only whether Kinly's development service is ready.",
                style = MaterialTheme.typography.bodyLarge,
                color = Ink600,
            )
        }

        BackendStatusCard(state = state, onRetry = onRetry)

        HorizontalDivider(color = Border)

        Text(
            text = "No account, member, location, notification, or safety data is used in this build.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink600,
        )
    }
}

@Composable
internal fun BackendStatusCard(
    state: BackendStatusUiState,
    onRetry: () -> Unit,
) {
    val statusDescription = when (state) {
        BackendStatusUiState.Checking -> "Checking backend availability"
        BackendStatusUiState.Available -> "Backend available"
        BackendStatusUiState.Unavailable -> "Backend unavailable"
    }
    val containerColor = when (state) {
        BackendStatusUiState.Available -> Fresh100
        BackendStatusUiState.Unavailable -> Danger100
        BackendStatusUiState.Checking -> Sky100
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Kinly service status"
                stateDescription = statusDescription
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Development service",
                style = MaterialTheme.typography.titleLarge,
                color = Ocean900,
                fontWeight = FontWeight.SemiBold,
            )

            when (state) {
                BackendStatusUiState.Checking -> CheckingStatus()
                BackendStatusUiState.Available -> AvailableStatus()
                BackendStatusUiState.Unavailable -> UnavailableStatus(onRetry)
            }
        }
    }
}

@Composable
private fun CheckingStatus() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(28.dp)
                .semantics { contentDescription = "Checking service" },
            color = Ocean700,
            strokeWidth = 3.dp,
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Checking availability", fontWeight = FontWeight.Medium, color = Ocean900)
            Text("This usually takes only a moment.", color = Ink600)
        }
    }
}

@Composable
private fun AvailableStatus() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = Fresh600,
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Available", fontWeight = FontWeight.SemiBold, color = Ocean900)
            Text("The development backend reports that it is ready.", color = Ink600)
        }
    }
}

@Composable
private fun UnavailableStatus(onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = Danger600,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Unavailable", fontWeight = FontWeight.SemiBold, color = Ocean900)
                Text(
                    "Kinly could not confirm that the development backend is ready.",
                    color = Ink600,
                )
            }
        }
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
internal fun UnavailableDestination(destination: Destination) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null,
            tint = Ocean700,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = destination.label,
            style = MaterialTheme.typography.headlineSmall,
            color = Ocean900,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Not available in this build. This area will be added through a separately approved feature.",
            style = MaterialTheme.typography.bodyLarge,
            color = Ink600,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun KinlyNavigationBar(
    selected: Destination,
    onSelected: (Destination) -> Unit,
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        Destination.entries.forEach { item ->
            NavigationBarItem(
                selected = selected == item,
                onClick = { onSelected(item) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Ocean700,
                    indicatorColor = Ocean700,
                    unselectedIconColor = Ocean900,
                    unselectedTextColor = Ocean900,
                ),
            )
        }
    }
}
