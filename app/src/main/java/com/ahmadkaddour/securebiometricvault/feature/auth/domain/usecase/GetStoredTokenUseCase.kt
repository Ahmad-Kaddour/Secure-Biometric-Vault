package com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase

import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.repository.AuthRepository

class GetStoredTokenUseCase constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<String, AuthError> = repository.getStoredToken()
}