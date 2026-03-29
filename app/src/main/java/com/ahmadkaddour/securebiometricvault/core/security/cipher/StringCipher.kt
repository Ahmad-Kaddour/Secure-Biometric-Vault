package com.ahmadkaddour.securebiometricvault.core.security.cipher

/**
 * Encrypts and decrypts strings.
 */
interface StringCipher {

    /**
     * Encrypts [plaintext]
     */
    fun encrypt(plaintext: String): String

    /**
     * Decrypts a [ciphertext] produced by [encrypt].
     */
    fun decrypt(ciphertext: String): String
}