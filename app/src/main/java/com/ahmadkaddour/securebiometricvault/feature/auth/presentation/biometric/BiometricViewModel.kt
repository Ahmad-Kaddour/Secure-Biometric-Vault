package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.biometric

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmadkaddour.securebiometricvault.R
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.core.security.biometric.android.AndroidBiometricAvailabilityChecker
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState
import com.ahmadkaddour.securebiometricvault.core.presentation.state.toUiState
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.CheckBiometricAvailabilityUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.GetStoredTokenUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BiometricViewModel(
    private val getStoredTokenUseCase: GetStoredTokenUseCase,
    private val checkBiometricAvailabilityUseCase: CheckBiometricAvailabilityUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BiometricUiModel())
    val state: StateFlow<BiometricUiModel> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<BiometricEffect>()
    val effects: SharedFlow<BiometricEffect> = _effects.asSharedFlow()

    init {
        checkBiometricAvailability()
    }

    fun dispatch(intent: BiometricIntent) {
        when (intent) {
            is BiometricIntent.AuthenticateClicked -> showBiometricPrompt(intent.activity)
            is BiometricIntent.ErrorDismissed -> reduce { copy(authState = UiState.Initial) }
        }
    }

    private fun checkBiometricAvailability() {
        viewModelScope.launch {
            reduce { copy(biometricCheckState = UiState.Loading) }
            val result = checkBiometricAvailabilityUseCase()
            reduce { copy(biometricCheckState = result.toUiState()) }
        }
    }

    private fun showBiometricPrompt(activity: FragmentActivity) {
        reduce { copy(authState = UiState.Loading) }

        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                viewModelScope.launch {
                    val tokenResult = getStoredTokenUseCase()
                    reduce { copy(authState = tokenResult.toUiState()) }
                    if (tokenResult is Success) {
                        _effects.emit(BiometricEffect.NavigateToHome)
                    }
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                val error = when (errorCode) {
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> AuthError.BiometricLockout
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_USER_CANCELED -> null
                    else -> AuthError.BiometricAuthFailed
                }
                reduce {
                    copy(authState = if (error != null) UiState.Failure(error) else UiState.Initial)
                }
            }

            override fun onAuthenticationFailed() {
                // Handled internally by BiometricPrompt after all retries
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_verify_identity))
            .setSubtitle(activity.getString(R.string.biometric_description))
            .setNegativeButtonText(activity.getString(R.string.common_cancel))
            .setAllowedAuthenticators(AndroidBiometricAvailabilityChecker.ALLOWED_AUTHENTICATORS)
            .setConfirmationRequired(false)
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun reduce(block: BiometricUiModel.() -> BiometricUiModel) {
        _state.update { it.block() }
    }
}