package com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase

import com.ahmadkaddour.securebiometricvault.core.domain.AppError
import com.ahmadkaddour.securebiometricvault.core.domain.EmptyResult
import com.ahmadkaddour.securebiometricvault.core.domain.asEmptyResult
import com.ahmadkaddour.securebiometricvault.core.domain.onSuccess
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LoginUseCase(
    private val repository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(username: String, password: String): EmptyResult<AppError> {
        return withContext(
            ioDispatcher
        ) {
            repository.login(username, password).onSuccess {
                repository.saveAccessToken(it.accessToken)
            }.asEmptyResult()
        }
    }
}