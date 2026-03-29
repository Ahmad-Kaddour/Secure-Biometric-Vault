package com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase

import com.ahmadkaddour.securebiometricvault.core.domain.Result
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.repository.AuthRepository

class HasValidSessionUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Boolean, Nothing> =
        Success(repository.hasValidSession())
}
