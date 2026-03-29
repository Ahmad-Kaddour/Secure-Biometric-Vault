package com.ahmadkaddour.securebiometricvault.feature.auth.data.source.remote

import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.feature.auth.data.model.LoginResultModel

interface AuthRemoteDataSource {
    suspend fun login(
        username: String, password: String
    ): Result<LoginResultModel, AuthError>
}