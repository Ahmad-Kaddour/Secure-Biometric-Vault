package com.ahmadkaddour.securebiometricvault.core.data.network.ktor

import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.RemoteError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.reflect.KClass


suspend inline fun <reified T> safeCall(
    noinline execute: () -> HttpResponse
) = safeCall<T>(T::class, execute)

suspend inline fun <T> safeCall(
    clz: KClass<*>,
    execute: () -> HttpResponse
): Result<T, RemoteError> {
    val response = try {
        execute()
    } catch (_: SocketTimeoutException) {
        return Failure(RemoteError.Timeout)
    } catch (_: UnresolvedAddressException) {
        return Failure(RemoteError.NoInternet)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Failure(RemoteError.Unknown())
    }

    return responseToResult(response, clz)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
) = responseToResult<T>(response, T::class)

suspend fun <T> responseToResult(
    response: HttpResponse,
    clz: KClass<*>
): Result<T, RemoteError> {
    return when (response.status.value) {
        in 200..299 -> {
            Success(response.body(TypeInfo(clz)))
        }

        408 -> Failure(RemoteError.Timeout)
        429 -> Failure(RemoteError.TooManyRequests())
        in 500..599 -> Failure(RemoteError.ServerError)
        else -> Failure(RemoteError.HttpError(response.status.value, response.bodyAsText()))
    }
}