package com.ahmadkaddour.securebiometricvault.core.security.biometric

import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Result

/**
 * Checks whether biometric authentication is available on the current device.
 */
interface BiometricAvailabilityChecker {
    /**
     * Returns [Success] if biometrics are available and enrolled,
     * or an [Failure] with the reason otherwise.
     */
    fun checkAvailability(): Result<Unit, AuthError>
}