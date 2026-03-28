package com.ahmadkaddour.securebiometricvault.core.data.storage.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

/**
 * The `PreferenceDataStoreManager` interface defines methods for accessing and modifying preferences stored in a data store.
 * It uses the `Preferences.Key` class from the `androidx.datastore.preferences.core` package to identify preferences.
 */
interface PreferenceDataStoreManager {
    /**
     * Retrieves a preference as a [Flow], which can be observed for changes.
     *
     * @param key The key of the preference.
     * @param defaultValue The default value of the preference.
     * @return A [Flow] emitting the current value of the preference.
     */
    fun <T> getPreferenceAsFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T>

    /**
     * Retrieves a preference synchronously.
     *
     * @param key The key of the preference.
     * @param defaultValue The default value of the preference.
     * @return The current value of the preference.
     */
    suspend fun <T> getPreference(key: Preferences.Key<out T>, defaultValue: T): T

    /**
     * Stores a preference.
     *
     * @param key The key of the preference.
     * @param value The value of the preference.
     */
    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)

    /**
     * Removes a preference.
     *
     * @param key The key of the preference to remove.
     */
    suspend fun <T> removePreference(key: Preferences.Key<T>)

    /**
     * Clears all preferences.
     */
    suspend fun clearAllPreference()
}