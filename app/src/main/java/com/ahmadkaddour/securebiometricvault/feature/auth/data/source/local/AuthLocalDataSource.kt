package com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local

interface AuthLocalDataSource {
    suspend fun saveAccessToken(token: String)

    suspend fun getSavedAccessToken(): String?
}