package com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local

import com.ahmadkaddour.securebiometricvault.core.data.storage.AppCache

class DefaultAuthLocalDataSource(
    private val appCache: AppCache
) : AuthLocalDataSource {

    override suspend fun saveAccessToken(token: String) {
        appCache.putString(ACCESS_TOKEN_KEY, token)
    }

    override suspend fun getSavedAccessToken(): String? {
        return appCache
            .getString(ACCESS_TOKEN_KEY, "")
            .takeUnless { it.isEmpty() }
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
    }
}