package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.ExceptionError
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState
import com.ahmadkaddour.securebiometricvault.core.presentation.state.toUiState
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.CheckBiometricAvailabilityUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.HasValidSessionUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val hasValidSessionUseCase: HasValidSessionUseCase,
    private val checkBiometricAvailabilityUseCase: CheckBiometricAvailabilityUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiModel())
    val state: StateFlow<LoginUiModel> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>()
    val effects: SharedFlow<LoginEffect> = _effects.asSharedFlow()

    init {
        checkInitialState()
    }

    fun dispatch(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UsernameChanged -> reduce { copy(username = intent.value) }
            is LoginIntent.PasswordChanged -> reduce { copy(password = intent.value) }
            is LoginIntent.TogglePasswordVisibility -> reduce { copy(passwordVisible = !passwordVisible) }
            is LoginIntent.LoginClicked -> handleLogin()
            is LoginIntent.ForgotPasswordClicked -> emitEffect(LoginEffect.NavigateToForgotPassword)
            is LoginIntent.ErrorDismissed -> reduce { copy(loginState = UiState.Initial) }
            is LoginIntent.BiometricDisabledAcknowledged -> emitEffect(LoginEffect.OpenBiometricSettings)
        }
    }

    fun onResume() {
        if (!_state.value.showBiometricDisabledBlock) return

        viewModelScope.launch {
            val biometricResult = checkBiometricAvailabilityUseCase()
            if (biometricResult is Success) {
                reduce { copy(showBiometricDisabledBlock = false, biometricAvailable = true) }
                emitEffect(LoginEffect.NavigateToBiometric)
            }
        }
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            val hasSession = hasValidSessionUseCase()
            val biometricResult = checkBiometricAvailabilityUseCase()

            val sessionActive = (hasSession as? Success)?.data == true
            val biometricAvailable = biometricResult is Success

            reduce {
                copy(
                    hasExistingSession = sessionActive,
                    biometricAvailable = biometricAvailable,
                    showBiometricDisabledBlock = sessionActive && !biometricAvailable,
                )
            }

            if (sessionActive && biometricAvailable) {
                emitEffect(LoginEffect.NavigateToBiometric)
            }
        }
    }

    private fun handleLogin() {
        val current = _state.value

        if (current.username.isBlank() || current.password.isBlank()) {
            emitEffect(LoginEffect.ShowError(
                AuthError.InvalidCredentials
            ))
            return
        }

        viewModelScope.launch {
            reduce { copy(loginState = UiState.Loading) }
            try {
                val result = loginUseCase(
                    username = current.username,
                    password = current.password
                )
                val newState = result.toUiState()
                reduce { copy(loginState = newState) }
                if (newState is UiState.Success) {
                    emitEffect(LoginEffect.NavigateToHome)
                } else if (newState is UiState.Failure) {
                    emitEffect(LoginEffect.ShowError(newState.error))
                }
            } catch (e: Exception) {
                val error = ExceptionError(e)
                reduce { copy(loginState = UiState.Failure(error)) }
                emitEffect(LoginEffect.ShowError(error))
            }
        }
    }

    private fun reduce(block: LoginUiModel.() -> LoginUiModel) {
        _state.update { it.block() }
    }

    private fun emitEffect(effect: LoginEffect) {
        viewModelScope.launch { _effects.emit(effect) }
    }
}
