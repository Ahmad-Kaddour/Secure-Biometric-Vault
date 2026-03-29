package com.ahmadkaddour.securebiometricvault.feature.auth.data.model

import com.ahmadkaddour.securebiometricvault.feature.auth.domain.entity.LoginResultEntity
import kotlinx.serialization.Serializable

@Serializable
data class LoginResultModel(
    val accessToken: String
)

fun LoginResultModel.toLoginResultEntity() = LoginResultEntity(accessToken)