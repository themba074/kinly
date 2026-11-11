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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.GroupOff
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.VerifiedUser
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

internal enum class CirclePreviewState(val label: String) {
    NoCircle("No circle"),
    Overview("Overview"),
    Unavailable("Unavailable"),
}

private data class CircleMemberPreview(
    val initials: String,
    val name: String,
    val role: String,
    val membership: String,
)

private val circleMembers = listOf(
    CircleMemberPreview("AN", "Anele", "Owner", "Membership active"),
    CircleMemberPreview("NA", "Nandi", "Administrator", "Membership active"),
    CircleMemberPreview("LW", "Lwazi", "Member", "Membership active"),
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CircleMembershipPreviewScreen(
    initialState: CirclePreviewState = CirclePreviewState.Overview,
    onBack: () -> Unit = {},
) {
    var previewState by remember(initialState) { mutableStateOf(initialState) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Circle preview", color = Ocean900, fontWeight = FontWeight.Bold)
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
                .testTag("circle_preview_list")
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { SyntheticCircleNotice() }
            item {
                CircleStateSelector(
                    selected = previewState,
                    onSelected = { previewState = it },
                )
            }
            when (previewState) {
                CirclePreviewState.NoCircle -> item { NoCircleCard() }
                CirclePreviewState.Overview -> {
                    item { CircleOverviewHeader() }
                    item { ConsentSeparationCard() }
                    item { SectionTitle("Adult members") }
                    circleMembers.forEach { member -> item { MemberCard(member) } }
                    item { SectionTitle("Invitations") }
                    item {
                        InvitationCard(
                            title = "Pending invitation",
                            recipient = "Invited adult · synthetic",
                            timing = "Expires in 2 hours",
                            detail = "Not yet a circle member",
                            icon = Icons.Rounded.MailOutline,
                            accent = Ocean700,
                            background = Sky100,
                        )
                    }
                    item {
                        InvitationCard(
                            title = "Expired invitation",
                            recipient = "Invited adult · synthetic",
                            timing = "Expired 1 hour ago",
                            detail = "Cannot be accepted",
                            icon = Icons.Rounded.Schedule,
                            accent = Stale600,
                            background = Stale100,
                        )
                    }
                }
                CirclePreviewState.Unavailable -> item { UnavailableCircleCard() }
            }
            item {
                Text(
                    text = "Preview only. No circle, invitation, membership, or role is changed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink600,
                )
            }
        }
    }
}

@Composable
private fun SyntheticCircleNotice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Synthetic circle preview. No member or invitation data is used."
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
            Icon(Icons.Rounded.Groups, contentDescription = null, tint = Ocean700)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Synthetic circle preview", color = Ocean900, fontWeight = FontWeight.SemiBold)
                Text("No real member, invitation, or circle data is used.", color = Ink600)
            }
        }
    }
}

@Composable
private fun CircleStateSelector(
    selected: CirclePreviewState,
    onSelected: (CirclePreviewState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Preview state")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CirclePreviewState.entries.forEach { state ->
                FilterChip(
                    selected = selected == state,
                    onClick = { onSelected(state) },
                    label = { Text(state.label) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("circle_state_${state.name.lowercase()}"),
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
private fun NoCircleCard() {
    CircleStateCard(
        icon = Icons.Rounded.GroupOff,
        title = "No circle yet",
        stateDescription = "No circle membership",
        background = MaterialTheme.colorScheme.surface,
    ) {
        Text("A private circle can be created or joined when that feature exists.", color = Ink600)
        Text(
            "Joining a circle never enables location sharing automatically.",
            color = Ocean900,
            fontWeight = FontWeight.SemiBold,
        )
        Text("No create or join action is available in this preview.", color = Ink600)
    }
}

@Composable
private fun CircleOverviewHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Family circle · synthetic",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineSmall,
            color = Ocean900,
            fontWeight = FontWeight.Bold,
        )
        Text("3 adult members · roles and membership only", color = Ink600)
    }
}

@Composable
private fun ConsentSeparationCard() {
    CircleStateCard(
        icon = Icons.Rounded.VerifiedUser,
        title = "Membership is not consent",
        stateDescription = "Location sharing remains controlled by each adult",
        background = Sky100,
    ) {
        Text("Each adult chooses whether and how to share location.", color = Ocean900)
        Text("Owners and administrators cannot override that choice.", color = Ink600)
    }
}

@Composable
private fun MemberCard(member: CircleMemberPreview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${member.name} membership"
                stateDescription = "${member.role}. ${member.membership}. Adult member."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = Sky100,
                modifier = Modifier.size(48.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(member.initials, color = Ocean700, fontWeight = FontWeight.Bold)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(member.name, color = Ocean900, fontWeight = FontWeight.Bold)
                Text(member.role, color = Ocean700, fontWeight = FontWeight.SemiBold)
                Text(member.membership, color = Fresh600)
                Text("Adult member · sharing controlled separately", color = Ink600)
            }
        }
    }
}

@Composable
private fun InvitationCard(
    title: String,
    recipient: String,
    timing: String,
    detail: String,
    icon: ImageVector,
    accent: Color,
    background: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = title
                stateDescription = "$recipient. $timing. $detail."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(32.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(title, color = Ocean900, fontWeight = FontWeight.Bold)
                Text(recipient, color = Ocean900)
                Text(timing, color = accent, fontWeight = FontWeight.SemiBold)
                Text(detail, color = Ink600)
            }
        }
    }
}

@Composable
private fun UnavailableCircleCard() {
    CircleStateCard(
        icon = Icons.Rounded.CloudOff,
        title = "Circle unavailable",
        stateDescription = "No membership state available and no change made",
        background = Stale100,
    ) {
        Text("Kinly could not show this synthetic circle state.", color = Ocean900)
        Text("No membership or invitation change was attempted.", color = Ink600)
    }
}

@Composable
private fun CircleStateCard(
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
private fun SectionTitle(title: String) {
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
private fun CircleMembershipPreview() {
    KinlyTheme { CircleMembershipPreviewScreen() }
}
