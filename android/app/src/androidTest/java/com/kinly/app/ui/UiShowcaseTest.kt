package com.kinly.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.kinly.app.ui.theme.KinlyTheme
import org.junit.Rule
import org.junit.Test

class UiShowcaseTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showcaseIsClearlySyntheticAndContainsNoSafetyAction() {
        composeRule.setContent {
            KinlyTheme {
                UiShowcaseScreen()
            }
        }

        composeRule.onNodeWithContentDescription(
            "Synthetic preview only. No live product actions.",
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("ui_showcase_list")
            .performScrollToNode(hasText("Unavailable action"))
        composeRule.onNodeWithText("Unavailable action").assertIsNotEnabled()
        composeRule.onNodeWithText("SOS").assertDoesNotExist()
    }

    @Test
    fun showcaseRendersAllServiceStates() {
        composeRule.setContent {
            KinlyTheme {
                UiShowcaseScreen()
            }
        }

        listOf("Checking availability", "Available", "Unavailable").forEach { label ->
            composeRule.onNodeWithTag("ui_showcase_list")
                .performScrollToNode(hasText(label))
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
    }
}
