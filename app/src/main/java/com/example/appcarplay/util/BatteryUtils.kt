package com.example.appcarplay.util

import android.content.Context
import android.os.PowerManager

/** Indica se o app já está liberado da otimização de bateria do Android. */
fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}
