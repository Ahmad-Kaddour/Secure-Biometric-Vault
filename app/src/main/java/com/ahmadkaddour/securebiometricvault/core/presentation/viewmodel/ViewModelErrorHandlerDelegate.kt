package com.ahmadkaddour.securebiometricvault.core.presentation.viewmodel

import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.domain.ExceptionError
import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.core.exception.ExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Default implementation of [ViewModelErrorHandler].
 *
 * @param exceptionHandler Used to report uncaught exceptions externally.
 */
class ViewModelErrorHandlerDelegate(
    private val exceptionHandler: ExceptionHandler,
) : ViewModelErrorHandler {

    override fun launch(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        scope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (throwable: Throwable) {
                exceptionHandler.handleException(throwable)
            }
        }
    }

    override suspend fun <D> handleResult(
        execute: suspend () -> Result<D, AppError>,
        onSuccess: suspend (D) -> Unit,
        onError: (suspend (AppError) -> Unit)?,
    ) {
        try {
            when (val result = execute()) {
                is Success -> onSuccess(result.data)
                is Failure -> onError?.invoke(result.error)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (throwable: Throwable) {
            val error = ExceptionError(throwable = throwable)
            onError?.invoke(error)
            exceptionHandler.handleException(throwable)
        }
    }

    override suspend fun safeCall(
        block: suspend () -> Unit,
        onError: ((ExceptionError) -> Unit)?,
    ) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (throwable: Throwable) {
            val error = ExceptionError(throwable = throwable)
            onError?.invoke(error)
            exceptionHandler.handleException(throwable)
        }
    }
}