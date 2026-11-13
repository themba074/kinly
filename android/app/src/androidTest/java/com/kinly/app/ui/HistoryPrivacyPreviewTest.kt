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

class HistoryPrivacyPreviewTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun loadingStateDoesNotClaimHistoryOrNetworkWork() {
        setPreview(HistoryPrivacyPreviewState.Loading)
        scrollTo("Preparing synthetic history")
        composeRule.onNodeWithText("No history request, location lookup, or network call is running.").assertIsDisplayed()
    }

    @Test fun noHistoryStateMakesNoFailureOrRetentionClaim() {
        setPreview(HistoryPrivacyPreviewState.NoHistory)
        scrollTo("No synthetic history")
        composeRule.onNodeWithText("This does not claim that location collection failed.").assertIsDisplayed()
        composeRule.onNodeWithText("No retention policy is configured and no setup action is available.").assertIsDisplayed()
    }

    @Test fun overviewShowsApproximationFreshnessAndGapsTruthfully() {
        setPreview(HistoryPrivacyPreviewState.Overview)
        listOf(
            "Approximate area · exact position hidden",
            "Updated 35 minutes ago · not live",
            "2-hour gap · no movement is inferred",
        ).forEach {
            scrollTo(it)
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test fun overviewExposesNoMapRouteAddressOrCoordinate() {
        setPreview(HistoryPrivacyPreviewState.Overview)
        listOf("View map", "Route", "Street address", "Latitude", "Longitude").forEach {
            composeRule.onNodeWithText(it, substring = true, ignoreCase = true).assertDoesNotExist()
        }
    }

    @Test fun privacyStateRequiresGrantAndRejectsAdministratorOverride() {
        setPreview(HistoryPrivacyPreviewState.PrivacyAccess)
        scrollTo("Circle membership or an administrator role does not grant history access.")
        composeRule.onNodeWithText("Circle membership or an administrator role does not grant history access.").assertIsDisplayed()
        composeRule.onNodeWithText("No last-known position, summary, or time range is exposed.").assertIsDisplayed()
    }

    @Test fun revokedAccessShowsNoCachedHistory() {
        setPreview(HistoryPrivacyPreviewState.PrivacyAccess)
        scrollTo("A revoked grant cannot be replaced by cached or previously viewed history.")
        composeRule.onNodeWithText("A revoked grant cannot be replaced by cached or previously viewed history.").assertIsDisplayed()
        composeRule.onNodeWithText("No history data is displayed after revocation.").assertIsDisplayed()
    }

    @Test fun retentionExportAndDeletionRemainUnconfiguredAndReadOnly() {
        setPreview(HistoryPrivacyPreviewState.PrivacyAccess)
        scrollTo("Retention policy not configured")
        composeRule.onNodeWithText("Kinly's default retention still requires an approved ADR and legal review.").assertIsDisplayed()
        composeRule.onNodeWithText("Export and deletion are future verified workflows, not actions in this preview.").assertIsDisplayed()
        listOf("Export now", "Delete history", "Save retention").forEach {
            composeRule.onNodeWithText(it).assertDoesNotExist()
        }
    }

    @Test fun offlineStateDoesNotFillOrInferMissingPeriods() {
        setPreview(HistoryPrivacyPreviewState.Offline)
        scrollTo("History offline")
        composeRule.onNodeWithText("The last synthetic summary is stale and is not treated as complete.").assertIsDisplayed()
        composeRule.onNodeWithText("Missing periods are not filled in or inferred while offline.").assertIsDisplayed()
    }

    @Test fun unavailableStateDoesNotOfferRetryOrClaimMutation() {
        setPreview(HistoryPrivacyPreviewState.Unavailable)
        scrollTo("History unavailable")
        composeRule.onNodeWithText("No request, deletion, export, or access change was attempted.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test fun selectorsMeetMinimumTouchTarget() {
        setPreview(HistoryPrivacyPreviewState.Overview)
        HistoryPrivacyPreviewState.entries.forEach {
            composeRule.onNodeWithTag("history_privacy_state_${it.name.lowercase()}").assertHeightIsAtLeast(48.dp)
        }
    }

    @Test fun previewRemainsReadableAtLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                KinlyTheme { HistoryPrivacyPreviewScreen(initialState = HistoryPrivacyPreviewState.Overview) }
            }
        }
        composeRule.onNodeWithContentDescription(
            "Synthetic History and privacy preview. No real movement, location, account, or retention data is used.",
        ).assertIsDisplayed()
        scrollTo("Another adult would require a specific active, revocable grant.")
        composeRule.onNodeWithText("Another adult would require a specific active, revocable grant.").assertIsDisplayed()
    }

    @Test fun showcaseOpensPreviewAndCanReturn() {
        composeRule.setContent { KinlyTheme { UiShowcaseScreen() } }
        composeRule.onNodeWithTag("ui_showcase_list").performScrollToNode(hasText("Open History and privacy preview"))
        composeRule.onNodeWithText("Open History and privacy preview").performClick()
        composeRule.onNodeWithText("History and privacy preview").assertIsDisplayed()
        composeRule.onNodeWithText("History").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back to UI showcase").performClick()
        composeRule.onNodeWithText("UI showcase").assertIsDisplayed()
    }

    private fun scrollTo(text: String) {
        composeRule.onNodeWithTag("history_privacy_preview_list").performScrollToNode(hasText(text))
    }

    private fun setPreview(state: HistoryPrivacyPreviewState) {
        composeRule.setContent { KinlyTheme { HistoryPrivacyPreviewScreen(initialState = state) } }
    }
}
