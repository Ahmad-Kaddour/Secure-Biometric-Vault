package com.ahmadkaddour.securebiometricvault.core.security.cipher.aes

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.ahmadkaddour.securebiometricvault.core.security.cipher.StringCipher
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * [StringCipher] implementation using AES-256-GCM via the Android Keystore.
 *
 * Each [encrypt] call uses a fresh random IV, which is prepended to the
 * ciphertext and Base64-encoded into a single self-contained string.
 *
 * @param keyAlias Keystore alias for the AES key. A new key is generated
 *                 under this alias if one does not already exist.
 * @param keyStore The [KeyStore] instance to use for key storage and retrieval.
 * @param keyGenerator a [KeyGenerator] for key creation.
 */
class AesGcmKeyStoreStringCipher(
    private val keyAlias: String,
    private val keyStore: KeyStore,
    private val keyGenerator: KeyGenerator
) : StringCipher {

    private fun getOrCreateKey(): SecretKey {
        keyStore.getKey(keyAlias, null)?.let { return it as SecretKey }

        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE_BITS)
            .setInvalidatedByBiometricEnrollment(false)
            .build()

        return keyGenerator.apply { init(spec) }.generateKey()
    }

    override fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val blob = cipher.iv + cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(blob, Base64.NO_WRAP)
    }

    override fun decrypt(ciphertext: String): String {
        val raw = Base64.decode(ciphertext, Base64.NO_WRAP)
        val iv = raw.copyOfRange(0, IV_LENGTH_BYTES)
        val cipherBytes = raw.copyOfRange(IV_LENGTH_BYTES, raw.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )
        return String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    }

    companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_SIZE_BITS = 256
        const val IV_LENGTH_BYTES = 12
        const val TAG_LENGTH_BITS = 128
    }
}