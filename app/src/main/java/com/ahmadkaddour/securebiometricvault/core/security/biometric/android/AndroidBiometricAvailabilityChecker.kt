package com.ahmadkaddour.securebiometricvault.core.security.biometric.android

import android.content.Context
import androidx.biometric.BiometricManager
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.core.security.biometric.BiometricAvailabilityChecker

/**
 * Implementation of [BiometricAvailabilityChecker] using Android BiometricManager.
 */
class AndroidBiometricAvailabilityChecker(
    private val context: Context
) : BiometricAvailabilityChecker {

    companion object {
        const val ALLOWED_AUTHENTICATORS = BiometricManager.Authenticators.BIOMETRIC_STRONG
    }

    override fun checkAvailability(): Result<Unit, AuthError> {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(ALLOWED_AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Success(Unit)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Failure(AuthError.BiometricNotEnrolled)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> Failure(AuthError.BiometricHardwareUnavailable)

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> Failure(AuthError.BiometricHardwareUnavailable)

            else -> Failure(AuthError.BiometricHardwareUnavailable)
        }
    }
}