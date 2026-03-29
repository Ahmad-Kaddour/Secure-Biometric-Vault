package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.core.exception.ExceptionHandler
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState
import com.ahmadkaddour.securebiometricvault.core.presentation.viewmodel.ViewModelErrorHandler
import com.ahmadkaddour.securebiometricvault.core.presentation.viewmodel.ViewModelErrorHandlerDelegate
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
    private val exceptionHandler: ExceptionHandler,
    private val loginUseCase: LoginUseCase,
    private val hasValidSessionUseCase: HasValidSessionUseCase,
    private val checkBiometricAvailabilityUseCase: CheckBiometricAvailabilityUseCase,
) : ViewModel(), ViewModelErrorHandler by ViewModelErrorHandlerDelegate(exceptionHandler) {

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
            handleResult(
                execute = { checkBiometricAvailabilityUseCase() },
                onSuccess = {
                    reduce { copy(showBiometricDisabledBlock = false, biometricAvailable = true) }
                    emitEffect(LoginEffect.NavigateToBiometric)
                },
            )
        }
    }

    private fun checkInitialState() {
        launch(viewModelScope) {
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
            emitEffect(LoginEffect.ShowError(AuthError.InvalidCredentials))
            return
        }

        viewModelScope.launch {
            reduce { copy(loginState = UiState.Loading) }
            handleResult(
                execute = {
                    loginUseCase(
                        username = current.username,
                        password = current.password
                    )
                },
                onSuccess = {
                    reduce { copy(loginState = UiState.Success(Unit)) }
                    emitEffect(LoginEffect.NavigateToHome)
                },
                onError = { error ->
                    reduce { copy(loginState = UiState.Failure(error)) }
                    emitEffect(LoginEffect.ShowError(error))
                },
            )
        }
    }

    private fun reduce(block: LoginUiModel.() -> LoginUiModel) {
        _state.update { it.block() }
    }

    private fun emitEffect(effect: LoginEffect) {
        launch(viewModelScope) { _effects.emit(effect) }
    }
}