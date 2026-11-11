package com.kinly.app.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.kinly.app.ui.theme.KinlyTheme
import org.junit.Rule
import org.junit.Test

class CircleMembershipPreviewTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun noCircleSeparatesMembershipFromLocationSharing() {
        setCirclePreview(CirclePreviewState.NoCircle)

        scrollTo("No circle yet")
        composeRule.onNodeWithText(
            "Joining a circle never enables location sharing automatically.",
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Create circle").assertDoesNotExist()
        composeRule.onNodeWithText("Join circle").assertDoesNotExist()
    }

    @Test
    fun overviewShowsAdultRolesAndMembershipState() {
        setCirclePreview(CirclePreviewState.Overview)

        listOf(
            "Anele" to "Owner",
            "Nandi" to "Administrator",
            "Lwazi" to "Member",
        ).forEach { (name, role) ->
            scrollTo(name)
            composeRule.onNodeWithContentDescription("$name membership").assertIsDisplayed()
            composeRule.onNodeWithText(role).assertIsDisplayed()
        }
    }

    @Test
    fun overviewStatesThatAdministratorsCannotOverrideConsent() {
        setCirclePreview(CirclePreviewState.Overview)

        scrollTo("Membership is not consent")
        composeRule.onNodeWithText("Each adult chooses whether and how to share location.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Owners and administrators cannot override that choice.")
            .assertIsDisplayed()
    }

    @Test
    fun invitationsDistinguishPendingFromExpiredWithoutSecrets() {
        setCirclePreview(CirclePreviewState.Overview)

        listOf(
            "Pending invitation",
            "Expires in 2 hours",
            "Not yet a circle member",
            "Expired invitation",
            "Expired 1 hour ago",
            "Cannot be accepted",
        ).forEach { label ->
            scrollTo(label)
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
        composeRule.onNodeWithText("token", substring = true, ignoreCase = true)
            .assertDoesNotExist()
        composeRule.onNodeWithText("Accept").assertDoesNotExist()
    }

    @Test
    fun unavailableStateDoesNotClaimAMembershipMutation() {
        setCirclePreview(CirclePreviewState.Unavailable)

        scrollTo("Circle unavailable")
        composeRule.onNodeWithText("No membership or invitation change was attempted.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
        composeRule.onNodeWithText("Remove member").assertDoesNotExist()
    }

    @Test
    fun previewSelectorsMeetMinimumTouchTarget() {
        setCirclePreview(CirclePreviewState.Overview)

        CirclePreviewState.entries.forEach { state ->
            composeRule.onNodeWithTag("circle_state_${state.name.lowercase()}")
                .assertHeightIsAtLeast(48.dp)
        }
    }

    @Test
    fun circlePreviewRemainsReadableAtLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                KinlyTheme {
                    CircleMembershipPreviewScreen(initialState = CirclePreviewState.Overview)
                }
            }
        }

        composeRule.onNodeWithContentDescription(
            "Synthetic circle preview. No member or invitation data is used.",
        ).assertIsDisplayed()
        scrollTo("Cannot be accepted")
        composeRule.onNodeWithText("Cannot be accepted").assertIsDisplayed()
    }

    @Test
    fun showcaseOpensCirclePreviewAndCanReturn() {
        composeRule.setContent { KinlyTheme { UiShowcaseScreen() } }

        composeRule.onNodeWithTag("ui_showcase_list")
            .performScrollToNode(hasText("Open circle and membership preview"))
        composeRule.onNodeWithText("Open circle and membership preview").performClick()
        composeRule.onNodeWithText("Circle preview").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back to UI showcase").performClick()
        composeRule.onNodeWithText("UI showcase").assertIsDisplayed()
    }

    private fun scrollTo(text: String) {
        composeRule.onNodeWithTag("circle_preview_list")
            .performScrollToNode(hasText(text))
    }

    private fun setCirclePreview(state: CirclePreviewState) {
        composeRule.setContent {
            KinlyTheme { CircleMembershipPreviewScreen(initialState = state) }
        }
    }
}
