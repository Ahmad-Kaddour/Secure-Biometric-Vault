package com.ahmadkaddour.securebiometricvault.core.data.storage.secure

import com.ahmadkaddour.securebiometricvault.core.data.mapper.JsonMapper
import com.ahmadkaddour.securebiometricvault.core.data.storage.AppCache
import com.ahmadkaddour.securebiometricvault.core.security.cipher.StringCipher
import kotlin.reflect.KClass

/**
 * [AppCache] implementation that transparently encrypts all values before
 * persisting them to [rawCache] and decrypts them on retrieval.
 *
 * @param rawCache The backing [AppCache] that physically stores the encrypted values.
 * @param cipher Used to encrypt and decrypt each stored value.
 * @param jsonMapper Used to serialize and deserialize objects to JSON before encryption.
 */
class SecureAppCache(
    private val rawCache: AppCache,
    private val cipher: StringCipher,
    private val jsonMapper: JsonMapper,
) : AppCache {

    private suspend fun putEncrypted(key: String, value: String?) =
        rawCache.putString(key, value?.let { cipher.encrypt(it) })

    private suspend fun getDecrypted(key: String): String? {
        val blob = rawCache.getString(key, "").takeIf { it.isNotEmpty() } ?: return null
        return runCatching { cipher.decrypt(blob) }.getOrNull()
    }

    override suspend fun putInt(key: String, value: Int?) =
        putEncrypted(key, value?.toString())

    override suspend fun getInt(key: String, defaultValue: Int): Int =
        getDecrypted(key)?.toIntOrNull() ?: defaultValue

    override suspend fun putLong(key: String, value: Long?) =
        putEncrypted(key, value?.toString())

    override suspend fun getLong(key: String, defaultValue: Long): Long =
        getDecrypted(key)?.toLongOrNull() ?: defaultValue

    override suspend fun putFloat(key: String, value: Float?) =
        putEncrypted(key, value?.toString())

    override suspend fun getFloat(key: String, defaultValue: Float): Float =
        getDecrypted(key)?.toFloatOrNull() ?: defaultValue

    override suspend fun putString(key: String, value: String?) =
        putEncrypted(key, value)

    override suspend fun getString(key: String, defaultValue: String): String =
        getDecrypted(key) ?: defaultValue

    override suspend fun putBoolean(key: String, value: Boolean?) =
        putEncrypted(key, value?.toString())

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        getDecrypted(key)?.toBooleanStrictOrNull() ?: defaultValue

    override suspend fun <T : Any> putObject(key: String, model: T?, klass: KClass<T>) =
        putEncrypted(key, model?.let { jsonMapper.toJson(it, klass) })

    override suspend fun <T : Any> getObject(key: String, klass: KClass<T>): T? =
        getDecrypted(key)?.let { jsonMapper.fromJson(it, klass) }

    override suspend fun clearAllValues() = rawCache.clearAllValues()
}