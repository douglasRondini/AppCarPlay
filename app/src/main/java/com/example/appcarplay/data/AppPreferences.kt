package com.example.appcarplay.data

import android.content.Context

/**
 * Guarda quais pacotes (apps) o usuário escolheu manter no grid de acesso rápido.
 * Sem seleção salva ainda (primeiro uso), todo o catálogo aparece por padrão.
 */
class AppPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSelectedPackages(): Set<String> =
        prefs.getStringSet(KEY_SELECTED_PACKAGES, null)
            ?: AppCatalog.all.map { it.pack }.toSet()

    fun setSelectedPackages(packages: Set<String>) {
        prefs.edit().putStringSet(KEY_SELECTED_PACKAGES, packages).apply()
    }

    private companion object {
        const val PREFS_NAME = "app_selection_prefs"
        const val KEY_SELECTED_PACKAGES = "selected_packages"
    }
}
