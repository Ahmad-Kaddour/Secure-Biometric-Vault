package com.ahmadkaddour.securebiometricvault.core.data.network.ktor

import com.ahmadkaddour.securebiometricvault.core.data.network.ApiClient
import com.ahmadkaddour.securebiometricvault.core.data.network.QueryParam
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlin.reflect.KClass


/**
 * `KtorApiClient` is an implementation of the `ApiClient` interface that leverages the Ktor HTTP client
 * for making network requests.
 *
 * This class encapsulates the details of interacting with the Ktor HTTP client, handling request building
 * and delegation to the underlying `HttpClient`.
 *
 * @property httpClient The Ktor `HttpClient` instance used for making network requests. This should be
 *                      configured with any necessary interceptors, content negotiation, and other settings.
 *                      It's injected into the class constructor.
 */
class KtorApiClient(private val httpClient: HttpClient) : ApiClient {
    override suspend fun <T> get(
        url: String,
        klass: KClass<*>,
        vararg params: QueryParam
    ) = safeCall<T>(klass) {
        httpClient.get(url) {
            params.forEach { parameter(it.first, it.second) }
        }
    }

    override suspend fun <T> post(
        url: String,
        body: Any,
        klass: KClass<*>
    ) = safeCall<T>(klass) {
        httpClient.post(url) {
            setBody(body)
        }
    }

    override suspend fun <T> put(
        url: String,
        body: Any,
        klass: KClass<*>
    ) = safeCall<T>(klass) {
        httpClient.put(url) {
            setBody(body)
        }
    }

    override suspend fun <T> delete(url: String, klass: KClass<*>) = safeCall<T>(klass) {
        httpClient.delete(url)
    }
}