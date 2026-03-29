package com.ahmadkaddour.securebiometricvault.feature.auth.data.repository

import com.ahmadkaddour.securebiometricvault.core.domain.AuthError
import com.ahmadkaddour.securebiometricvault.core.domain.Failure
import com.ahmadkaddour.securebiometricvault.core.domain.Success
import com.ahmadkaddour.securebiometricvault.feature.auth.data.model.LoginResultModel
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local.AuthLocalDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.remote.AuthRemoteDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.entity.LoginResultEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAuthRepositoryTest {

    private val remoteDataSource: AuthRemoteDataSource = mockk()
    private val localDataSource: AuthLocalDataSource = mockk()
    private val repository = DefaultAuthRepository(remoteDataSource, localDataSource)

    @Test
    fun `login returns success`() = runBlocking {
        val username = "user"
        val password = "password"
        val token = "token"
        coEvery { remoteDataSource.login(username, password) } returns Success(LoginResultModel(token))

        val result = repository.login(username, password)

        assertTrue(result is Success)
        assertEquals(LoginResultEntity(token), (result as Success).data)
    }

    @Test
    fun `login returns failure`() = runBlocking {
        val username = "user"
        val password = "password"
        val error = AuthError.InvalidCredentials
        coEvery { remoteDataSource.login(username, password) } returns Failure(error)

        val result = repository.login(username, password)

        assertTrue(result is Failure)
        assertEquals(error, (result as Failure).error)
    }

    @Test
    fun `saveAccessToken calls localDataSource`() = runBlocking {
        val token = "token"
        coEvery { localDataSource.saveAccessToken(token) } returns Unit

        repository.saveAccessToken(token)

        coVerify { localDataSource.saveAccessToken(token) }
    }

    @Test
    fun `getStoredToken returns token when exists`() = runBlocking {
        val token = "token"
        coEvery { localDataSource.getSavedAccessToken() } returns token

        val result = repository.getStoredToken()

        assertTrue(result is Success)
        assertEquals(token, (result as Success).data)
    }

    @Test
    fun `getStoredToken returns error when saved token is null`() = runBlocking {
        coEvery { localDataSource.getSavedAccessToken() } returns null

        val result = repository.getStoredToken()

        assertTrue(result is Failure)
        assertEquals(AuthError.TokenNotFound, (result as Failure).error)
    }

    @Test
    fun `hasValidSession returns true when token exists`() = runBlocking {
        coEvery { localDataSource.getSavedAccessToken() } returns "token"

        val result = repository.hasValidSession()

        assertTrue(result)
    }

    @Test
    fun `hasValidSession returns false when token is null`() = runBlocking {
        coEvery { localDataSource.getSavedAccessToken() } returns null

        val result = repository.hasValidSession()

        assertFalse(result)
    }
}
