package com.ahmadkaddour.securebiometricvault.core.data.storage

import kotlin.reflect.KClass

/**
 * The `AppCache` interface defines methods for storing and retrieving data in the app cache.
 * Implementations of this interface can be used to cache various types of data, such as integers, longs, floats, strings, booleans, and objects.
 * The cache can be used to store data temporarily for quick access and retrieval.
 */
interface AppCache {
    /**
     * Stores an integer value in the cache with the specified key.
     *
     * @param key The key to store the integer value under.
     * @param value The integer value to store.
     */
    suspend fun putInt(key: String, value: Int?)

    /**
     * Retrieves an integer value from the cache with the specified key.
     *
     * @param key The key to retrieve the integer value from.
     * @param defaultValue The default value to return if the integer value is not found in the cache.
     * @return The retrieved integer value, or the default value if the value is not found in the cache.
     */
    suspend fun getInt(key: String, defaultValue: Int): Int

    /**
     * Stores a long value in the cache with the specified key.
     *
     * @param key The key to store the long value under.
     * @param value The long value to store.
     */
    suspend fun putLong(key: String, value: Long?)

    /**
     * Retrieves a long value from the cache with the specified key.
     *
     * @param key The key to retrieve the long value from.
     * @param defaultValue The default value to return if the long value is not found in the cache.
     * @return The retrieved long value, or the default value if the value is not found in the cache.
     */
    suspend fun getLong(key: String, defaultValue: Long): Long

    /**
     * Stores a float value in the cache with the specified key.
     *
     * @param key The key to store the float value under.
     * @param value The float value to store.
     */
    suspend fun putFloat(key: String, value: Float?)

    /**
     * Retrieves a float value from the cache with the specified key.
     *
     * @param key The key to retrieve the float value from.
     * @param defaultValue The default value to return if the float value is not found in the cache.
     * @return The retrieved float value, or the default value if the value is not found in the cache.
     */
    suspend fun getFloat(key: String, defaultValue: Float): Float

    /**
     * Stores a string value in the cache with the specified key.
     *
     * @param key The key to store the string value under.
     * @param value The string value to store.
     */
    suspend fun putString(key: String, value: String?)

    /**
     * Retrieves a string value from the cache with the specified key.
     *
     * @param key The key to retrieve the string value from.
     * @param defaultValue The default value to return if the string value is not found in the cache.
     * @return The retrieved string value, or the default value if the value is not found in the cache.
     */
    suspend fun getString(key: String, defaultValue: String): String

    /**
     * Stores a boolean value in the cache with the specified key.
     *
     * @param key The key to store the boolean value under.
     * @param value The boolean value to store.
     */
    suspend fun putBoolean(key: String, value: Boolean?)

    /**
     * Retrieves a boolean value from the cache with the specified key.
     *
     * @param key The key to retrieve the boolean value from.
     * @param defaultValue The default value to return if the boolean value is not found in the cache.
     * @return The retrieved boolean value, or the default value if the value is not found in the cache.
     */
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Stores an object in the cache with the specified key.
     *
     * @param key The key to store the object under.
     * @param model The object to store.
     * @param klass The class of the object.
     */
    suspend fun <T : Any> putObject(key: String, model: T?, klass: KClass<T>)

    /**
     * Retrieves an object from the cache with the specified key and class.
     *
     * @param key The key to retrieve the object from.
     * @param klass The class of the object.
     * @return The retrieved object, or `null` if the object is not found in the cache.
     */
    suspend fun <T : Any> getObject(key: String, klass: KClass<T>): T?

    /**
     * Clears all values stored in the cache.
     */
    suspend fun clearAllValues()
}

suspend inline fun <reified T : Any> AppCache.putObject(key: String, model: T?) {
    putObject(key, model, T::class)
}

suspend inline fun <reified T : Any> AppCache.getObject(key: String): T? {
    return getObject(key, T::class)
}