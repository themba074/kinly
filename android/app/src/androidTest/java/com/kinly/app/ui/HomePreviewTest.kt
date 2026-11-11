package com.kinly.app.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import com.kinly.app.ui.theme.KinlyTheme
import org.junit.Rule
import org.junit.Test

class HomePreviewTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pausedSharingHidesAnyLastKnownLocation() {
        setHomePreview(HomePreviewState.Circle)

        composeRule.onNodeWithText("Sharing paused").assertIsDisplayed()
        composeRule.onNodeWithText("No location is visible while sharing is paused.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Visible to: no one").assertIsDisplayed()
        composeRule.onNodeWithText("Last known location", substring = true).assertDoesNotExist()
    }

    @Test
    fun circleMakesFreshnessPrecisionAndOfflineUncertaintyExplicit() {
        setHomePreview(HomePreviewState.Circle)

        listOf(
            "Fresh · precise",
            "Accuracy ±35 m",
            "Approximate area",
            "Exact position is hidden",
            "Stale · offline",
            "Last update 2 hours ago",
            "Offline — do not treat as live",
        ).forEach { label ->
            composeRule.onNodeWithTag("home_preview_list")
                .performScrollToNode(hasText(label))
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }

        composeRule.onNodeWithContentDescription("Avery sharing state")
            .assertIsDisplayed()
    }

    @Test
    fun noCircleExplainsThatMembershipDoesNotEnableSharing() {
        setHomePreview(HomePreviewState.NoCircle)

        composeRule.onNodeWithText("Start with trust").assertIsDisplayed()
        composeRule.onNodeWithText(
            "Joining a circle never enables location sharing automatically. You choose if, when, and how precisely to share.",
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Create circle").assertDoesNotExist()
    }

    @Test
    fun unavailablePreviewDoesNotOfferAConvincingLiveRetry() {
        setHomePreview(HomePreviewState.Unavailable)

        composeRule.onNodeWithText("Circle unavailable").assertIsDisplayed()
        composeRule.onNodeWithText("Kinly could not load this preview state.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun homePreviewRemainsReadableAtLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                KinlyTheme { HomePreviewScreen(initialState = HomePreviewState.Circle) }
            }
        }

        composeRule.onNodeWithContentDescription(
            "Synthetic Home preview. No live circle or location data.",
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("home_preview_list")
            .performScrollToNode(hasText("Offline — do not treat as live"))
        composeRule.onNodeWithText("Offline — do not treat as live").assertIsDisplayed()
    }

    @Test
    fun showcaseOpensHomePreviewAndCanReturn() {
        composeRule.setContent { KinlyTheme { UiShowcaseScreen() } }

        composeRule.onNodeWithText("Open Home preview").performClick()
        composeRule.onNodeWithText("Home preview").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back to UI showcase").performClick()
        composeRule.onNodeWithText("UI showcase").assertIsDisplayed()
    }

    private fun setHomePreview(state: HomePreviewState) {
        composeRule.setContent {
            KinlyTheme { HomePreviewScreen(initialState = state) }
        }
    }
}
