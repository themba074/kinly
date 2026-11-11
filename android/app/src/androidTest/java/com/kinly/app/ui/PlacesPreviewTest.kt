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

class PlacesPreviewTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingStateDoesNotClaimLocationOrNotificationWork() {
        setPlacesPreview(PlacesPreviewState.Loading)

        scrollTo("Loading synthetic places")
        composeRule.onNodeWithText("No location check or notification is running.")
            .assertIsDisplayed()
    }

    @Test
    fun noPlacesStateExplainsConsentWithoutSetupActions() {
        setPlacesPreview(PlacesPreviewState.NoPlaces)

        scrollTo("No places yet")
        composeRule.onNodeWithText(
            "Adding a place would never enable an adult's location sharing automatically.",
        ).assertIsDisplayed()
        listOf("Add place", "Create place", "Set up place").forEach { action ->
            composeRule.onNodeWithText(action).assertDoesNotExist()
        }
    }

    @Test
    fun overviewShowsSyntheticPlacesWithoutMapOrAddress() {
        setPlacesPreview(PlacesPreviewState.Overview)

        listOf(
            "Home · synthetic",
            "250 m radius",
            "Community centre · synthetic",
            "150 m radius",
        ).forEach { label ->
            scrollTo(label)
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
        listOf("View map", "Latitude", "Longitude", "Street address").forEach { label ->
            composeRule.onNodeWithText(label, substring = true, ignoreCase = true)
                .assertDoesNotExist()
        }
    }

    @Test
    fun overviewMakesAdultPlaceAlertConsentExplicit() {
        setPlacesPreview(PlacesPreviewState.Overview)

        scrollTo("Place alerts require consent")
        composeRule.onNodeWithText(
            "Each adult chooses whether their location may power a place alert.",
        ).assertIsDisplayed()
        composeRule.onNodeWithText(
            "Circle membership or an administrator role cannot enable it for them.",
        ).assertIsDisplayed()
    }

    @Test
    fun uncertainStateNeverClaimsArrival() {
        setPlacesPreview(PlacesPreviewState.Uncertain)

        scrollTo("Arrival not confirmed")
        composeRule.onNodeWithText(
            "Kinly would wait for a more accurate update instead of claiming an arrival.",
        ).assertIsDisplayed()
        composeRule.onNodeWithText("No arrival or departure alert has been sent.")
            .assertIsDisplayed()
    }

    @Test
    fun offlineStateMakesFreshnessAndDeliveryLimitsVisible() {
        setPlacesPreview(PlacesPreviewState.Offline)

        scrollTo("Places offline")
        composeRule.onNodeWithText("The last synthetic place state is not treated as current.")
            .assertIsDisplayed()
        composeRule.onNodeWithText(
            "No arrival, departure, or alert delivery is claimed while offline.",
        ).assertIsDisplayed()
    }

    @Test
    fun unavailableStateDoesNotOfferRetryOrClaimMutation() {
        setPlacesPreview(PlacesPreviewState.Unavailable)

        scrollTo("Places unavailable")
        composeRule.onNodeWithText("No place or alert preference was changed.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
        composeRule.onNodeWithText("Save").assertDoesNotExist()
    }

    @Test
    fun previewSelectorsMeetMinimumTouchTarget() {
        setPlacesPreview(PlacesPreviewState.Overview)

        PlacesPreviewState.entries.forEach { state ->
            composeRule.onNodeWithTag("places_state_${state.name.lowercase()}")
                .assertHeightIsAtLeast(48.dp)
        }
    }

    @Test
    fun placesPreviewRemainsReadableAtLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                KinlyTheme {
                    PlacesPreviewScreen(initialState = PlacesPreviewState.Overview)
                }
            }
        }

        composeRule.onNodeWithContentDescription(
            "Synthetic Places preview. No address, coordinate, or alert data is used.",
        ).assertIsDisplayed()
        scrollTo("No location or alert activity")
        composeRule.onNodeWithText("No location or alert activity").assertIsDisplayed()
    }

    @Test
    fun showcaseOpensPlacesPreviewAndCanReturn() {
        composeRule.setContent { KinlyTheme { UiShowcaseScreen() } }

        composeRule.onNodeWithTag("ui_showcase_list")
            .performScrollToNode(hasText("Open Places preview"))
        composeRule.onNodeWithText("Open Places preview").performClick()
        composeRule.onNodeWithText("Places preview").assertIsDisplayed()
        composeRule.onNodeWithText("Places").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back to UI showcase").performClick()
        composeRule.onNodeWithText("UI showcase").assertIsDisplayed()
    }

    private fun scrollTo(text: String) {
        composeRule.onNodeWithTag("places_preview_list")
            .performScrollToNode(hasText(text))
    }

    private fun setPlacesPreview(state: PlacesPreviewState) {
        composeRule.setContent {
            KinlyTheme { PlacesPreviewScreen(initialState = state) }
        }
    }
}
