package com.example.batteryhealth.utils

import android.os.BatteryManager


fun getStatusResultString(status: Int): String {
    var statusString = UNKNOWN_VALUE

    when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> statusString = CHARGING
        BatteryManager.BATTERY_STATUS_DISCHARGING -> statusString = DISCHARGING
        BatteryManager.BATTERY_STATUS_FULL -> statusString = FULL
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> statusString = NOT_CHARGING
    }
    return statusString
}

