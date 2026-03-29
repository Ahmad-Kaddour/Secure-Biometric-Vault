package com.ahmadkaddour.securebiometricvault.core.domain

/**
 * Base type for all domain-level errors.
 */
open class AppError(open val message: String)


sealed class RemoteError(override val message: String) : AppError(message) {
    data class HttpError(val code: Int, override val message: String) : RemoteError(message)
    data class TooManyRequests(
        val remainingMillisecond: Long? = null,
        override val message: String = "Too many requests."
    ) : RemoteError(message)
    data object ServerError : RemoteError("Server is currently unavailable.")
    data object NoInternet : RemoteError("No internet connection.")
    data object Timeout : RemoteError("The request timed out.")
    data class Unknown(override val message: String = "An unexpected network error occurred.") :
        RemoteError(message)
}

sealed class LocalError(override val message: String) : AppError(message) {
    data object NotFound : LocalError("The requested value was not found in storage.")
    data class Unknown(override val message: String = "An unexpected storage error occurred.") :
        LocalError(message)
}

// Exception wrapper (for unexpected/unhandled exceptions)
data class ExceptionError(
    val throwable: Throwable,
    override val message: String = throwable.message ?: "An unexpected error occurred.",
) : AppError(message)

sealed class AuthError(override val message: String) : AppError(message) {
    data object InvalidCredentials : AuthError("Invalid username or password.")
    data object BiometricNotEnrolled : AuthError("Please enrol biometrics in device settings.")
    data object BiometricHardwareUnavailable : AuthError("Biometric hardware is not available on this device.")
    data object BiometricLockout : AuthError("Too many attempts. Biometrics are temporarily locked.")
    data object BiometricAuthFailed : AuthError("Biometric authentication failed.")
    data object TokenNotFound : AuthError("No access token found. Please log in.")
    data object SessionExpired : AuthError("Your session has expired. Please log in again.")
    data object RootedDevice : AuthError("This device is rooted. The app cannot run for security reasons.")
}