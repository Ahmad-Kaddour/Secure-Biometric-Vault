package com.ahmadkaddour.securebiometricvault.core.security.root.rootbeer

import android.content.Context
import android.os.Build
import com.ahmadkaddour.securebiometricvault.core.security.root.RootDetector
import com.scottyab.rootbeer.RootBeer
import java.io.File

/**
 * [RootDetector] implementation backed by the RootBeer library.
 *
 * **Note:** Emulators are also considered as a rooted device.
 *
 * **Limitations:** These checks can be bypassed by advanced tools such as
 * Magisk with DenyList enabled. For stronger guarantees, complement this
 * with a server-side Play Integrity API verification.
 *
 * @param context Application context used to initialize RootBeer.
 */
class RootBeerRootDetector(private val context: Context) : RootDetector {

    private val rootBeer by lazy { RootBeer(context) }

    override fun isRooted(): Boolean = rootBeer.isRooted || isRunningOnEmulator()

    fun isRunningOnEmulator() = isEmulator() || checkHardware() || hasEmulatorFiles()

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.contains("vbox")
                || Build.FINGERPRINT.contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }

    fun checkHardware(): Boolean {
        return Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu")
    }

    fun hasEmulatorFiles(): Boolean {
        val files = arrayOf(
            "/dev/qemu_pipe",
            "/dev/socket/qemud",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace"
        )

        for (file in files) {
            if (File(file).exists()) {
                return true
            }
        }
        return false
    }
}