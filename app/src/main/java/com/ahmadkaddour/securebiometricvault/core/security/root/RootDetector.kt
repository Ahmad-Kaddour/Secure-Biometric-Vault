package com.ahmadkaddour.securebiometricvault.core.security.root


/**
 * Detects whether the current device is rooted or has been tampered with.
 *
 * Note: Any on-device root detection can potentially be bypassed on a sufficiently
 * compromised device. In Future when backend is available consider migrating to Play Integrity API.
 */
interface RootDetector {
    fun isRooted(): Boolean
}