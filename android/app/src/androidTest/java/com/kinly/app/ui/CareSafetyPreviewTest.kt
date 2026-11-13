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

class CareSafetyPreviewTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun noActivityHasNoProductActions() {
        setPreview(CareSafetyPreviewState.NoActivity)
        scrollTo("No care activity")
        composeRule.onNodeWithText("No send or start action is available in this preview.").assertIsDisplayed()
        listOf("Send check-in", "Start journey", "SOS", "Call now").forEach {
            composeRule.onNodeWithText(it).assertDoesNotExist()
        }
    }

    @Test fun overviewStatesEmergencyBoundaryTruthfully() {
        setPreview(CareSafetyPreviewState.Overview)
        scrollTo("Emergency tools not configured")
        composeRule.onNodeWithText("No circle contact, phone number, emergency service, or responder is notified or called.").assertIsDisplayed()
        composeRule.onNodeWithText("No help or dispatch claim is made.").assertIsDisplayed()
    }

    @Test fun checkInsDistinguishLifecycleAndDelivery() {
        setPreview(CareSafetyPreviewState.CheckIns)
        listOf("Sent check-in · synthetic", "Delivered check-in · synthetic", "Responded check-in · synthetic", "Expired check-in · synthetic", "Failed check-in · synthetic").forEach {
            scrollTo(it)
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test fun checkInResponsesAreReadOnlyAndNonResponseIsNotDanger() {
        setPreview(CareSafetyPreviewState.CheckIns)
        scrollTo("‘Call me’ and ‘Dismiss’ are read-only response examples.")
        composeRule.onNodeWithText("‘Call me’ and ‘Dismiss’ are read-only response examples.").assertIsDisplayed()
        scrollTo("Expiry does not mean the person is in danger.")
        composeRule.onNodeWithText("Expiry does not mean the person is in danger.").assertIsDisplayed()
    }

    @Test fun journeysShowAudienceExpiryEstimateAndFreshness() {
        setPreview(CareSafetyPreviewState.Journeys)
        listOf("Audience: 2 selected adults", "Maximum duration: 60 minutes", "ETA: about 24 minutes · estimate only", "Updated 3 minutes ago · medium confidence").forEach {
            scrollTo(it)
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
        listOf("View map", "Latitude", "Longitude").forEach {
            composeRule.onNodeWithText(it, substring = true, ignoreCase = true).assertDoesNotExist()
        }
    }

    @Test fun endedAndExpiredJourneysDoNotExtendConsentOrClaimArrival() {
        setPreview(CareSafetyPreviewState.Journeys)
        scrollTo("Ending is not presented as an arrival")
        composeRule.onNodeWithText("Ending is not presented as an arrival").assertIsDisplayed()
        scrollTo("Consent was not extended automatically")
        composeRule.onNodeWithText("Consent was not extended automatically").assertIsDisplayed()
    }

    @Test fun offlineStateShowsUnknownAndStaleWithoutInference() {
        setPreview(CareSafetyPreviewState.Offline)
        scrollTo("Care status offline")
        composeRule.onNodeWithText("Check-in delivery is unknown while offline.").assertIsDisplayed()
        composeRule.onNodeWithText("Journey ETA is stale · last updated 28 minutes ago.").assertIsDisplayed()
        composeRule.onNodeWithText("No response, safety, or arrival is inferred.").assertIsDisplayed()
    }

    @Test fun unavailableStateDoesNotOfferRetryOrClaimNotification() {
        setPreview(CareSafetyPreviewState.Unavailable)
        scrollTo("Care and Safety unavailable")
        composeRule.onNodeWithText("No action was attempted and no notification was sent.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test fun selectorsMeetMinimumTouchTarget() {
        setPreview(CareSafetyPreviewState.Overview)
        CareSafetyPreviewState.entries.forEach {
            composeRule.onNodeWithTag("care_safety_state_${it.name.lowercase()}").assertHeightIsAtLeast(48.dp)
        }
    }

    @Test fun previewRemainsReadableAtLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                KinlyTheme { CareSafetyPreviewScreen(initialState = CareSafetyPreviewState.Overview) }
            }
        }
        composeRule.onNodeWithContentDescription("Synthetic Care and Safety preview. No real contact, message, journey, location, or emergency data is used.").assertIsDisplayed()
        scrollTo("No help or dispatch claim is made.")
        composeRule.onNodeWithText("No help or dispatch claim is made.").assertIsDisplayed()
    }

    @Test fun showcaseOpensPreviewAndCanReturn() {
        composeRule.setContent { KinlyTheme { UiShowcaseScreen() } }
        composeRule.onNodeWithTag("ui_showcase_list").performScrollToNode(hasText("Open Care and Safety preview"))
        composeRule.onNodeWithText("Open Care and Safety preview").performClick()
        composeRule.onNodeWithText("Care and Safety preview").assertIsDisplayed()
        composeRule.onNodeWithText("Safety").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back to UI showcase").performClick()
        composeRule.onNodeWithText("UI showcase").assertIsDisplayed()
    }

    private fun scrollTo(text: String) {
        composeRule.onNodeWithTag("care_safety_preview_list").performScrollToNode(hasText(text))
    }

    private fun setPreview(state: CareSafetyPreviewState) {
        composeRule.setContent { KinlyTheme { CareSafetyPreviewScreen(initialState = state) } }
    }
}
