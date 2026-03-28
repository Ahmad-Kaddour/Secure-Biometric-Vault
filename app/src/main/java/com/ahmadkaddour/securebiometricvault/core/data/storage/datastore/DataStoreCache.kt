package com.ahmadkaddour.securebiometricvault.core.data.storage.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ahmadkaddour.securebiometricvault.core.data.storage.AppCache
import com.ahmadkaddour.securebiometricvault.core.data.mapper.JsonMapper
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.reflect.KClass

/**
 * `AppCache` implementation based on DataStore API.
 */
class DataStoreCache (
    private val dataStoreManager: PreferenceDataStoreManager,
    private val jsonMapper: JsonMapper
) : AppCache {
    override suspend fun putInt(key: String, value: Int?) {
        val preferencesKey = intPreferencesKey(key)
        putOrRemovePreference(preferencesKey, value)
        return
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int {
        val preferencesKey = intPreferencesKey(key)
        return dataStoreManager.getPreference(preferencesKey, defaultValue)
    }

    override suspend fun putLong(key: String, value: Long?) {
        val preferencesKey = longPreferencesKey(key)
        putOrRemovePreference(preferencesKey, value)
    }

    override suspend fun getLong(key: String, defaultValue: Long): Long {
        val preferencesKey = longPreferencesKey(key)
        return dataStoreManager.getPreference(preferencesKey, defaultValue)
    }

    override suspend fun putFloat(key: String, value: Float?) {
        val preferencesKey = floatPreferencesKey(key)
        putOrRemovePreference(preferencesKey, value)
    }

    override suspend fun getFloat(key: String, defaultValue: Float): Float {
        val preferencesKey = floatPreferencesKey(key)
        return dataStoreManager.getPreference(preferencesKey, defaultValue)
    }

    override suspend fun putString(key: String, value: String?) {
        val preferencesKey = stringPreferencesKey(key)
        putOrRemovePreference(preferencesKey, value)
    }

    override suspend fun  getString(key: String, defaultValue: String): String {
        val preferencesKey = stringPreferencesKey(key)
        return dataStoreManager.getPreference(preferencesKey, defaultValue)
    }

    override suspend fun putBoolean(key: String, value: Boolean?) {
        val preferencesKey = booleanPreferencesKey(key)
        putOrRemovePreference(preferencesKey, value)
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val preferencesKey = booleanPreferencesKey(key)
        return dataStoreManager.getPreference(preferencesKey, defaultValue)
    }

    override suspend fun <T : Any> putObject(key: String, model: T?, klass: KClass<T>) {
        val preferencesKey = stringPreferencesKey(key)
        val stringValue = model?.let { jsonMapper.toJson(it, klass) }
        putOrRemovePreference(preferencesKey, stringValue)
    }

    override suspend fun <T : Any> getObject(key: String, klass: KClass<T>): T? {
        val preferencesKey = stringPreferencesKey(key)
        val stringValue = dataStoreManager.getPreference(preferencesKey, "")
        return try {
            jsonMapper.fromJson(stringValue, klass)
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
            null
        }
    }

    override suspend fun clearAllValues() {
        dataStoreManager.clearAllPreference()
    }

    private suspend fun <T> putOrRemovePreference(key: Preferences.Key<T>, value: T?) {
        if (value != null) {
            dataStoreManager.putPreference(key, value)
        } else {
            dataStoreManager.removePreference(key)
        }
    }
}