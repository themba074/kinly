package com.kinly.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.kinly.app.presentation.BackendStatusUiState
import com.kinly.app.presentation.AuthUiState
import com.kinly.app.ui.theme.KinlyTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class KinlyShellTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun availableStateIsNamedAndVisible() {
        composeRule.setContent {
            KinlyTheme {
                KinlyShell(BackendStatusUiState.Available, onRetry = {})
            }
        }

        composeRule.onNodeWithContentDescription("Kinly service status").assertIsDisplayed()
        composeRule.onNodeWithText("Available").assertIsDisplayed()
        composeRule.onNodeWithText("Signing in does not join a circle or start location sharing. Member, location, notification, and safety features are not available in this build.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun unavailableStateOffersRetry() {
        var retried = false
        composeRule.setContent {
            KinlyTheme {
                KinlyShell(BackendStatusUiState.Unavailable, onRetry = { retried = true })
            }
        }

        composeRule.onNodeWithText("Retry").assertIsEnabled().performClick()
        assertTrue(retried)
    }

    @Test
    fun safetyTabCannotTriggerAnIncident() {
        composeRule.setContent {
            KinlyTheme {
                KinlyShell(BackendStatusUiState.Available, onRetry = {})
            }
        }

        composeRule.onNodeWithText("Safety").performClick()
        composeRule.onNodeWithText("Not available in this build. This area will be added through a separately approved feature.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("SOS").assertDoesNotExist()
    }

    @Test
    fun unconfiguredSignInIsLabeledWithoutAnAction() {
        composeRule.setContent {
            KinlyTheme {
                AuthStatusCard(
                    state = AuthUiState.NotConfigured,
                    onSignIn = {},
                    onSignOut = {},
                    onRetry = {},
                )
            }
        }
        composeRule.onNodeWithContentDescription("Kinly sign-in status").assertIsDisplayed()
        composeRule.onNodeWithText("Sign-in not available in this build").assertIsDisplayed()
        composeRule.onNodeWithText("Sign in").assertDoesNotExist()
    }

    @Test
    fun signedOutAndSignedInActionsAreClear() {
        var started = false
        composeRule.setContent {
            KinlyTheme {
                AuthStatusCard(
                    state = AuthUiState.SignedOut,
                    onSignIn = { started = true },
                    onSignOut = {},
                    onRetry = {},
                )
            }
        }
        composeRule.onNodeWithText("Sign in").assertIsEnabled().performClick()
        assertTrue(started)
    }
}
