package com.ahmadkaddour.securebiometricvault.feature.auth.data.repository

import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.core.domain.map
import com.ahmadkaddour.securebiometricvault.feature.auth.data.model.LoginResultModel
import com.ahmadkaddour.securebiometricvault.feature.auth.data.model.toLoginResultEntity
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local.AuthLocalDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.remote.AuthRemoteDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.entity.LoginResultEntity
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.repository.AuthRepository

class DefaultAuthRepository(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {
    override suspend fun login(
        username: String, password: String
    ): Result<LoginResultEntity, AppError> {
        return remoteDataSource.login(username, password).map(
            LoginResultModel::toLoginResultEntity
        )
    }

    override suspend fun saveAccessToken(token: String) {
        localDataSource.saveAccessToken(token)
    }

    override suspend fun getStoredToken(): Result<String, AuthError> {
        val token = localDataSource.getSavedAccessToken()
        return if (!token.isNullOrEmpty()) {
            Success(token)
        } else {
            Failure(AuthError.TokenNotFound)
        }
    }

    override suspend fun hasValidSession() = !localDataSource.getSavedAccessToken().isNullOrEmpty()
}