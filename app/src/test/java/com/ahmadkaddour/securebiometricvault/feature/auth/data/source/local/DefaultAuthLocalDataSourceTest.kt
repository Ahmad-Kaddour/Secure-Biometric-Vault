package com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local

import com.ahmadkaddour.securebiometricvault.core.data.storage.AppCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultAuthLocalDataSourceTest {

    private val appCache: AppCache = mockk()
    private val dataSource = DefaultAuthLocalDataSource(appCache)

    @Test
    fun `saveAccessToken calls appCache putString`() = runBlocking {
        val token = "test_token"
        coEvery { appCache.putString(any(), any()) } returns Unit

        dataSource.saveAccessToken(token)

        coVerify { appCache.putString("access_token", token) }
    }

    @Test
    fun `getSavedAccessToken returns token when present`() = runBlocking {
        val token = "test_token"
        coEvery { appCache.getString("access_token", "") } returns token

        val result = dataSource.getSavedAccessToken()

        assertEquals(token, result)
    }

    @Test
    fun `getSavedAccessToken returns null when token is empty`() = runBlocking {
        coEvery { appCache.getString("access_token", "") } returns ""

        val result = dataSource.getSavedAccessToken()

        assertNull(result)
    }
}
