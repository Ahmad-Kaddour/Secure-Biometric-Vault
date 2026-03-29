package com.ahmadkaddour.securebiometricvault.feature.auth.domain.repository

import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.entity.LoginResultEntity


interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResultEntity, AppError>

    suspend fun saveAccessToken(token: String)

    suspend fun getStoredToken(): Result<String, AuthError>

    suspend fun hasValidSession(): Boolean
}
