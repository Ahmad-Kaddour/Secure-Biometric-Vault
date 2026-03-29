package com.ahmadkaddour.securebiometricvault.core.exception

/**
 * Mock [ExceptionHandler] for demonstration purposes.
 *
 * **Note:** In real applications this should be replaced with CrashlyticsExceptionHandler.
 *
 */
class MockExceptionHandler : ExceptionHandler {

    override suspend fun handleException(throwable: Throwable) {
        println("MockExceptionHandler: ${throwable.message}\n${throwable.stackTraceToString()}")
    }
}