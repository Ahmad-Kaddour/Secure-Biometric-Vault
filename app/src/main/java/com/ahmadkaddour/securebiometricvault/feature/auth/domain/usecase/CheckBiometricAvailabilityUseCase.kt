package com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase

import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.security.biometric.BiometricAvailabilityChecker

class CheckBiometricAvailabilityUseCase(
    private val checker: BiometricAvailabilityChecker,
) {

    operator fun invoke(): Result<Unit, AuthError> = checker.checkAvailability()
}