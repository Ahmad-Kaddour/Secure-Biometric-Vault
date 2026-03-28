package com.ahmadkaddour.securebiometricvault.core.data.network

import com.ahmadkaddour.securebiometricvault.core.domain.RemoteError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import kotlin.reflect.KClass


typealias QueryParam = Pair<String, Any?>


/**
 * Interface defining a generic API client for making network requests.
 *
 * This interface provides methods for common HTTP operations (GET, POST, PUT, DELETE)
 * and handles the serialization/deserialization of data. It leverages Kotlin's
 * `Result` type to represent success or failure outcomes and uses custom
 * `RemoteError` for remote-related errors.
 */
interface ApiClient {
    /**
     * Performs a GET request to the specified URL and attempts to deserialize the response body
     * into an object of the provided class type.
     *
     * @param url The URL to send the GET request to.
     * @param klass The KClass representing the type to which the response body should be deserialized.
     * @param params Optional query parameters to be appended to the URL. Each parameter is represented as a [QueryParam].
     * @return A [Result] containing either:
     *          - A successful [T] object if the request was successful and the response could be deserialized.
     *          - A [RemoteError] object indicating the error encountered during the request or deserialization.
     */
    suspend fun <T> get(
        url: String,
        klass: KClass<*>,
        vararg params: QueryParam
    ): Result<T, RemoteError>

    /**
     * Sends a POST request to the specified URL with the given body and attempts to deserialize the response into an object of the specified class.
     *
     * @param url The URL to send the POST request to.
     * @param body The request body to be sent. Can be any object that can be serialized to JSON.
     * @param klass The KClass representing the type into which the response body should be deserialized.
     * @return A [Result] object.
     *   - If the request is successful and the response can be deserialized, it contains a [T] object of the specified type.
     *   - If the request fails or an error occurs, it contains a [RemoteError] object representing the error.
     * @param T The type into which the response body should be deserialized.
     */
    suspend fun <T> post(
        url: String,
        body: Any,
        klass: KClass<*>
    ): Result<T, RemoteError>

    /**
     * Performs an HTTP PUT request to the specified URL with the given body and attempts to deserialize the response
     * into an object of the specified class.
     *
     * @param url The URL to send the PUT request to.
     * @param body The request body to send, can be any serializable object.
     * @param klass The KClass representing the type to deserialize the response into.
     *            If the response body is empty or cannot be deserialized, the Result will be a success containing `null` if T is nullable, or an error if T is not nullable.
     * @return A [Result] object containing either:
     *         - Success: The deserialized object of type T if the request was successful and the response could be deserialized.
     *           If the response is empty and T is nullable it will return `null` in the Success, if T is not nullable it will return a `DataError.Remote.ResponseEmpty` in the Failure.
     *         - Failure: A [RemoteError] indicating the type of error that occurred during the request.
     * @param T The type of object to deserialize the response into.
     */
    suspend fun <T> put(
        url: String,
        body: Any,
        klass: KClass<*>
    ): Result<T, RemoteError>

    /**
     * Sends a DELETE request to the specified URL and attempts to parse the response body
     * into an object of the provided class type.
     *
     * @param T The type of the object to which the response body should be deserialized.
     * @param url The URL to which the DELETE request will be sent.
     * @param klass The KClass representing the expected type of the response body.
     *           This is used for deserializing the response.
     * @return A [Result] object encapsulating either:
     *          - A successful result with the deserialized object of type [T].
     *          - A [RemoteError] error indicating a failure in the network request or response parsing.
     */
    suspend fun <T> delete(
        url: String,
        klass: KClass<*>
    ): Result<T, RemoteError>
}

suspend inline fun <reified T> ApiClient.get(
    url: String,
    vararg params: QueryParam
) = get<T>(url, T::class, *params)

suspend inline fun <reified T> ApiClient.post(
    url: String,
    body: Any
) = post<T>(url, body, T::class)

suspend inline fun <reified T> ApiClient.put(
    url: String,
    body: Any
) = put<T>(url, body, T::class)

suspend inline fun <reified T> ApiClient.delete(
    url: String,
) = delete<T>(url, T::class)