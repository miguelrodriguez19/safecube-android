package com.miguelrodriguez19.safecube

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test

class MainActivityComposeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @SdkSuppress(minSdkVersion = 34)
    @Test
    fun greeting_isVisible() {
        composeRule.setContent {
            Greeting(name = "Android")
        }
        composeRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }
}
