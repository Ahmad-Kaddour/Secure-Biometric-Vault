package com.ahmadkaddour.securebiometricvault.core.data.storage.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ahmadkaddour.securebiometricvault.core.data.mapper.JsonMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DataStoreCacheTest {

    private val dataStoreManager: PreferenceDataStoreManager = mockk(relaxed = true)
    private val jsonMapper: JsonMapper = mockk()
    private val dataStoreCache = DataStoreCache(dataStoreManager, jsonMapper)

    @Test
    fun `putInt with non null value`() = runBlocking {
        val key = "test_key"
        val value = 10
        val prefKey = intPreferencesKey(key)

        dataStoreCache.putInt(key, value)

        coVerify { dataStoreManager.putPreference(prefKey, value) }
    }

    @Test
    fun `putInt with null value`() = runBlocking {
        val key = "test_key"
        val prefKey = intPreferencesKey(key)

        dataStoreCache.putInt(key, null)

        coVerify { dataStoreManager.removePreference(prefKey) }
    }

    @Test
    fun `getInt returns saved value`() = runBlocking {
        val key = "test_key"
        val defaultValue = 0
        val expectedValue = 10
        val prefKey = intPreferencesKey(key)

        coEvery { dataStoreManager.getPreference(prefKey, defaultValue) } returns expectedValue

        val actualValue = dataStoreCache.getInt(key, defaultValue)

        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `putString with non null value`() = runBlocking {
        val key = "test_key"
        val value = "test_value"
        val prefKey = stringPreferencesKey(key)

        dataStoreCache.putString(key, value)

        coVerify { dataStoreManager.putPreference(prefKey, value) }
    }

    @Test
    fun `getString returns saved value`() = runBlocking {
        val key = "test_key"
        val defaultValue = "default"
        val expectedValue = "stored"
        val prefKey = stringPreferencesKey(key)

        coEvery { dataStoreManager.getPreference(prefKey, defaultValue) } returns expectedValue

        val actualValue = dataStoreCache.getString(key, defaultValue)

        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `putObject serializes saves value`() = runBlocking {
        val key = "object_key"
        val model = TestData("test")
        val json = """{"name":"test"}"""
        val prefKey = stringPreferencesKey(key)

        every { jsonMapper.toJson(model, TestData::class) } returns json

        dataStoreCache.putObject(key, model, TestData::class)

        coVerify { dataStoreManager.putPreference(prefKey, json) }
    }

    @Test
    fun `getObject retrieves string and deserializes it`() = runBlocking {
        val key = "object_key"
        val json = """{"name":"test"}"""
        val model = TestData("test")
        val prefKey = stringPreferencesKey(key)

        coEvery { dataStoreManager.getPreference(prefKey, "") } returns json
        every { jsonMapper.fromJson(json, TestData::class) } returns model

        val actualValue = dataStoreCache.getObject(key, TestData::class)

        assertEquals(model, actualValue)
    }

    @Test
    fun `getObject returns null when deserialization fails`() = runBlocking {
        val key = "object_key"
        val json = "invalid_json"
        val prefKey = stringPreferencesKey(key)

        coEvery { dataStoreManager.getPreference(prefKey, "") } returns json
        every { jsonMapper.fromJson(json, TestData::class) } throws RuntimeException("Failed")

        val actualValue = dataStoreCache.getObject(key, TestData::class)

        assertNull(actualValue)
    }

    @Test
    fun `clearAllValues calls dataStoreManager clearAllPreference`() = runBlocking {
        dataStoreCache.clearAllValues()
        coVerify { dataStoreManager.clearAllPreference() }
    }

    data class TestData(val name: String)
}
