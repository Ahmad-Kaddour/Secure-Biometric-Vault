package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.ahmadkaddour.securebiometricvault.R
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val usernameLabel = context.getString(R.string.login_username_label)
    private val passwordLabel = context.getString(R.string.login_password_label)
    private val loginButtonLabel = context.getString(R.string.login_sign_in)
    private val loadingLabel = context.getString(R.string.common_loading)

    @Test
    fun loginButton_isDisabled_whenFieldsAreEmpty() {
        composeTestRule.setContent {
            LoginContent(
                state = LoginUiModel(username = "", password = ""),
                onIntent = {}
            )
        }

        composeTestRule.onNode(hasText(loginButtonLabel) and hasClickAction()).assertIsNotEnabled()
    }

    @Test
    fun loginButton_isEnabled_whenFieldsAreNotEmpty() {
        composeTestRule.setContent {
            LoginContent(
                state = LoginUiModel(username = "user", password = "password"),
                onIntent = {}
            )
        }

        composeTestRule.onNode(hasText(loginButtonLabel) and hasClickAction()).assertIsEnabled()
    }

    @Test
    fun loadingIndicator_isVisible_duringLoadingState() {
        composeTestRule.setContent {
            LoginContent(
                state = LoginUiModel(
                    username = "user",
                    password = "password",
                    loginState = UiState.Loading
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithContentDescription(loadingLabel).assertExists()
    }

    @Test
    fun typingInFields_triggersIntents() {
        var usernameIntentValue = ""
        var passwordIntentValue = ""

        composeTestRule.setContent {
            LoginContent(
                state = LoginUiModel(),
                onIntent = { intent ->
                    when (intent) {
                        is LoginIntent.UsernameChanged -> usernameIntentValue = intent.value
                        is LoginIntent.PasswordChanged -> passwordIntentValue = intent.value
                        else -> {}
                    }
                }
            )
        }

        composeTestRule.onNodeWithText(usernameLabel).performTextInput("ahmad")
        assert(usernameIntentValue == "ahmad")

        composeTestRule.onNodeWithText(passwordLabel).performTextInput("123456")
        assert(passwordIntentValue == "123456")
    }

    @Test
    fun clickingLogin_triggersLoginIntent() {
        var loginClicked = false

        composeTestRule.setContent {
            LoginContent(
                state = LoginUiModel(username = "user", password = "password"),
                onIntent = { intent ->
                    if (intent is LoginIntent.LoginClicked) loginClicked = true
                }
            )
        }

        composeTestRule.onNode(hasText(loginButtonLabel) and hasClickAction()).performClick()
        assert(loginClicked)
    }
}
