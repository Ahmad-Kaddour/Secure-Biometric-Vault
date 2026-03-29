package com.ahmadkaddour.securebiometricvault.core.presentation.viewmodel

import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.domain.ExceptionError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import kotlinx.coroutines.CoroutineScope

/**
 * Provides unified exception handling for ViewModels via delegation.
 */
interface ViewModelErrorHandler {

    /**
     * Launches a coroutine in the given [scope] with top-level exception handling.
     * Any uncaught [Throwable] is forwarded to the underlying [ExceptionHandler].
     *
     * @param scope The [CoroutineScope] to launch in, typically [ViewModel.viewModelScope].
     * @param block The suspending work to execute.
     */
    fun launch(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit,
    )

    /**
     * Executes a use case and handles the [Result] declaratively.
     *
     * @param execute Lambda returning a [Result] from a use case.
     * @param onSuccess Called with the data when result is [Success].
     * @param onError Optional custom handler for [AppError].
     */
    suspend fun <D> handleResult(
        execute: suspend () -> Result<D, AppError>,
        onSuccess: suspend (D) -> Unit,
        onError: (suspend (AppError) -> Unit)? = null,
    )

    /**
     * Executes a suspending block safely, catching and reporting any
     * uncaught exceptions without exposing a [Result] type.
     *
     * @param block The suspending work to execute.
     * @param onError Optional handler invoked with the wrapped [ExceptionError].
     */
    suspend fun safeCall(
        block: suspend () -> Unit,
        onError: ((ExceptionError) -> Unit)? = null,
    )
}
