package com.ahmadkaddour.securebiometricvault.feature.auth.data.source.remote

import com.ahmadkaddour.securebiometricvault.core.data.network.ApiClient
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.feature.auth.data.model.LoginResultModel
import kotlinx.coroutines.delay

class MockAuthRemoteDataSource(
    private val apiClient: ApiClient
) : AuthRemoteDataSource {

    private val validCredentials = mapOf(
        "ahmad" to "123456",
    )

    override suspend fun login(
        username: String, password: String
    ): Result<LoginResultModel, AuthError> {
//         This is how I would usually do it.
//        return apiClient.post(
//            "/login",
//            mapOf(
//                "username" to username,
//                "password" to password
//            )
//        )

        delay(1_200) // Simulate network latency
        val expected = validCredentials[username.lowercase()]
        return if (expected != null && expected == password) {
            Success(
                LoginResultModel(
                    "mock_jwt_${username}_${System.currentTimeMillis()}"
                )
            )
        } else {
            Failure(
                AuthError.InvalidCredentials
            )
        }
    }
}