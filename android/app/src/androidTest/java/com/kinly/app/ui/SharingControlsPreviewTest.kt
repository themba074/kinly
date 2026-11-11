package com.kinly.app.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
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

class SharingControlsPreviewTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pausedModeShowsNoLocationOrAudience() {
        setSharingPreview(SharingPreviewMode.Paused)

        scrollTo("Sharing paused")
        composeRule.onNodeWithText(
            "No current or last-known location is shown while sharing is paused.",
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Visible to: no one").assertIsDisplayed()
        composeRule.onNodeWithText("Shared place", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("Save", substring = true).assertDoesNotExist()
    }

    @Test
    fun approximateModeExplainsServerProtectionAndHiddenPosition() {
        setSharingPreview(SharingPreviewMode.Approximate)

        scrollTo("Approximate area")
        composeRule.onNodeWithText(
            "Shares a server-protected area. Your exact position stays hidden.",
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Audience: Family circle · 3 people").assertIsDisplayed()
    }

    @Test
    fun preciseAndTimedModesStateDisclosureAndExpiry() {
        setSharingPreview(SharingPreviewMode.Precise)
        scrollTo("Precise sharing")
        composeRule.onNodeWithText(
            "Would share a precise position with visible freshness and accuracy.",
        ).assertIsDisplayed()

        composeRule.onNodeWithTag("sharing_mode_timed").performClick()
        scrollTo("Timed precise sharing")
        composeRule.onNodeWithText(
            "Ends at 18:00 SAST (UTC+2) · synthetic example",
        ).assertIsDisplayed()
    }

    @Test
    fun consentOwnershipIsVisibleAndAdultControlled() {
        setSharingPreview(SharingPreviewMode.Approximate)

        scrollTo("Your choice")
        composeRule.onNodeWithContentDescription("Consent ownership").assertIsDisplayed()
        composeRule.onNodeWithText("Only you can change your sharing mode.").assertIsDisplayed()
        scrollTo("Circle administrators cannot override an adult member's choice.")
        composeRule.onNodeWithText(
            "Circle administrators cannot override an adult member's choice.",
        ).assertIsDisplayed()
    }

    @Test
    fun unavailableStateDisablesEveryModeAndChangesNothing() {
        setSharingPreview(
            mode = SharingPreviewMode.Paused,
            availability = SharingPreviewAvailability.Unavailable,
        )

        SharingPreviewMode.entries.forEach { mode ->
            composeRule.onNodeWithTag("sharing_mode_${mode.name.lowercase()}")
                .assertIsNotEnabled()
        }
        scrollTo("Sharing controls unavailable")
        composeRule.onNodeWithText("Controls are disabled and no setting changed.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun modeControlsMeetMinimumTouchTarget() {
        setSharingPreview(SharingPreviewMode.Paused)

        SharingPreviewMode.entries.forEach { mode ->
            composeRule.onNodeWithTag("sharing_mode_${mode.name.lowercase()}")
                .assertHeightIsAtLeast(48.dp)
        }
    }

    @Test
    fun sharingPreviewRemainsReadableAtLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
                KinlyTheme {
                    SharingControlsPreviewScreen(
                        initialMode = SharingPreviewMode.Timed,
                    )
                }
            }
        }

        composeRule.onNodeWithContentDescription(
            "Synthetic sharing controls preview. No setting is saved.",
        ).assertIsDisplayed()
        scrollTo("Ends at 18:00 SAST (UTC+2) · synthetic example")
        composeRule.onNodeWithText(
            "Ends at 18:00 SAST (UTC+2) · synthetic example",
        ).assertIsDisplayed()
    }

    @Test
    fun showcaseOpensSharingPreviewAndCanReturn() {
        composeRule.setContent { KinlyTheme { UiShowcaseScreen() } }

        composeRule.onNodeWithTag("ui_showcase_list")
            .performScrollToNode(hasText("Open sharing controls preview"))
        composeRule.onNodeWithText("Open sharing controls preview").performClick()
        composeRule.onNodeWithText("Sharing preview").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back to UI showcase").performClick()
        composeRule.onNodeWithText("UI showcase").assertIsDisplayed()
    }

    private fun scrollTo(text: String) {
        composeRule.onNodeWithTag("sharing_preview_list")
            .performScrollToNode(hasText(text))
    }

    private fun setSharingPreview(
        mode: SharingPreviewMode,
        availability: SharingPreviewAvailability = SharingPreviewAvailability.Ready,
    ) {
        composeRule.setContent {
            KinlyTheme {
                SharingControlsPreviewScreen(
                    initialMode = mode,
                    initialAvailability = availability,
                )
            }
        }
    }
}
