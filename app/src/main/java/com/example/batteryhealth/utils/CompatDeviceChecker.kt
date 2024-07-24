package com.example.batteryhealth.utils

import android.content.Context
import android.os.Build
import java.lang.reflect.Field

class CompatDeviceChecker {
    companion object {
        fun isIncompatible(context: Context): Boolean {
            // Check the manufacturer and use the appropriate function.
            return when (Build.MANUFACTURER) {
                "samsung" -> checkSamsungCompat(context)
                "google" -> checkGoogleCompat()
                // For all other brands, return true.
                else -> true
            }
        }

        private fun checkGoogleCompat(): Boolean {
            // Mark all non-Tensor devices that received Android 14 as incompatible.
            return !(
                    Build.DEVICE == "redfin" || // Pixel 5
                            Build.DEVICE == "barbet" || // Pixel 5a
                            Build.DEVICE == "bramble"    // Pixel 4a (5G)
                    )
        }

        private fun checkSamsungCompat(context: Context): Boolean {
            // One UI 6.0 is known to be incompatible.
            return getOneUiVersion(context) != "6.0"
        }

        @Throws(Exception::class)
        fun getOneUiVersion(context: Context): String {
            if (!isSemAvailable(context)) {
                return "" // was "1.0" originally but probably just a dummy value for one UI devices
            }
            val semPlatformIntField: Field =
                Build.VERSION::class.java.getDeclaredField("SEM_PLATFORM_INT")
            val version: Int = semPlatformIntField.getInt(null) - 90000
            return if (version < 0) {
                // not one ui (could be previous Samsung OS)
                ""
            } else (version / 10000).toString() + "." + version % 10000 / 100
        }

        fun isSemAvailable(context: Context?): Boolean {
            return context != null &&
                    (context.packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile") ||
                            context.packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile_lite"))
        }
    }
}
