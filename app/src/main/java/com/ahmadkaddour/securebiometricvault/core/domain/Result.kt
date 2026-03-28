package com.ahmadkaddour.securebiometricvault.core.domain


/**
 * A discriminated union representing either a successful value [D] or a typed error [E].
 */
sealed class Result<out D, out E : AppError>

data class Success<D>(val data:
                      D) : Result<D, Nothing>()
data class Failure<E : AppError>(val error: E) : Result<Nothing, E>()

// Convenience alias for use cases that produce no meaningful value
typealias EmptyResult<E> = Result<Unit, E>


inline fun <D, E : AppError, R> Result<D, E>.fold(
    onSuccess: (D) -> R,
    onFailure: (E) -> R,
): R = when (this) {
    is Success -> onSuccess(data)
    is Failure -> onFailure(error)
}

inline fun <D, E : AppError> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    if (this is Success) action(data)
    return this
}

inline fun <D, E : AppError> Result<D, E>.onFailure(action: (E) -> Unit): Result<D, E> {
    if (this is Failure) action(error)
    return this
}

inline fun <D, E : AppError, R> Result<D, E>.map(transform: (D) -> R): Result<R, E> =
    when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

fun <D, E : AppError> Result<D, E>.asEmptyResult(): EmptyResult<E> = map { }
