package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login

import androidx.compose.runtime.Immutable
import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState

sealed interface LoginIntent {
    data class UsernameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object LoginClicked : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
    data object ErrorDismissed : LoginIntent
    data object BiometricDisabledAcknowledged : LoginIntent
}

@Immutable
data class LoginUiModel(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,

    val loginState: UiState<Unit> = UiState.Initial,

    val biometricAvailable: Boolean = false,
    val hasExistingSession: Boolean = false,
    val showBiometricDisabledBlock: Boolean = false,
)

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data object NavigateToBiometric : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data object OpenBiometricSettings : LoginEffect
    data class ShowError(val error: AppError) : LoginEffect
}
