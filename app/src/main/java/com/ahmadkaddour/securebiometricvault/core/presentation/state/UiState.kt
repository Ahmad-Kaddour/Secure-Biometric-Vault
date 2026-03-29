package com.ahmadkaddour.securebiometricvault.core.presentation.state

import androidx.compose.runtime.Immutable
import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success


/**
 * Sealed UI state carrier used across all ViewModels.
 */
@Immutable
sealed interface UiState<out T> {
    /** No operation has started yet. Render the idle/default view. */
    data object Initial : UiState<Nothing>

    /** An operation is in progress. Render a loading indicator. */
    data object Loading : UiState<Nothing>

    /** The operation succeeded. [data] holds the result. */
    data class Success<T>(val data: T) : UiState<T>

    /** The operation failed. [error] is the typed domain error. */
    data class Failure(val error: AppError) : UiState<Nothing>
}

val UiState<*>.isLoading get() = this is UiState.Loading
val UiState<*>.isSuccess get() = this is UiState.Success
val UiState<*>.isFailure get() = this is UiState.Failure

inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

inline fun <T> UiState<T>.onFailure(action: (AppError) -> Unit): UiState<T> {
    if (this is UiState.Failure) action(error)
    return this
}

/** Map a [Result] to a [UiState]. */
fun <D, E : AppError> Result<D, E>.toUiState(): UiState<D> =
    when (this) {
        is Success -> UiState.Success(data)
        is Failure -> UiState.Failure(error)
    }
