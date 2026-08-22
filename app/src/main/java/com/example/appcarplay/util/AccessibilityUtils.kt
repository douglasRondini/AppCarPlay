package com.example.appcarplay.util

import android.content.Context
import android.provider.Settings
import com.example.appcarplay.service.TouchAccessibilityService

/** Verifica se o usuário já ativou o serviço de acessibilidade usado para o controle por toque. */
fun isTouchAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedComponent = "${context.packageName}/${TouchAccessibilityService::class.java.canonicalName}"
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    return enabledServices.split(':').any { it.equals(expectedComponent, ignoreCase = true) }
}
