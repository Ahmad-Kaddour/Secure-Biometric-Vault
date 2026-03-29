package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.biometric

import androidx.compose.runtime.Immutable
import androidx.fragment.app.FragmentActivity
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState

sealed interface BiometricIntent {
    data class AuthenticateClicked(val activity: FragmentActivity) : BiometricIntent
    data object ErrorDismissed : BiometricIntent
}

@Immutable
data class BiometricUiModel(
    val authState: UiState<String> = UiState.Initial,
    val biometricCheckState: UiState<Unit> = UiState.Initial,
)

sealed interface BiometricEffect {
    data object NavigateToHome : BiometricEffect
}