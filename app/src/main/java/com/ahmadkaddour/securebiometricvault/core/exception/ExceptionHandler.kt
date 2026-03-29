package com.ahmadkaddour.securebiometricvault.core.exception

/**
 * Central exception handling contract for the application.
 */
interface ExceptionHandler {

    /**
     * Called when an unexpected exception is caught.
     *
     * @param throwable The caught exception.
     */
    suspend fun handleException(throwable: Throwable)
}