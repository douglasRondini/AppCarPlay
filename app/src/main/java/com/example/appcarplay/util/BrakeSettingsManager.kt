package com.example.appcarplay.util

import android.content.Context
import android.provider.Settings
import android.util.Log

class BrakeSettingsManager(private val context: Context) {

    companion object {
        private const val TAG = "BrakeSettingsManager"

        // Lista das chaves de sistema mais comuns usadas por marcas como Topway, FYT, Junsun, Eonon
        private val COMMON_BRAKE_KEYS = listOf(
            "brake_enable",
            "brake_switch",
            "drive_video_enable",
            "hand_brake_detect",
            "parking_brake_enable",
            "setting_brake_enable"
        )
    }

    /**
     * Tenta identificar qual é a chave do freio usada pela sua multimídia
     */
    fun getActiveBrakeKey(): String? {
        for (key in COMMON_BRAKE_KEYS) {
            try {
                // Tenta buscar no System primeiro, depois no Secure
                if (Settings.System.getString(context.contentResolver, key) != null) {
                    return key
                }
                if (Settings.Secure.getString(context.contentResolver, key) != null) {
                    return key
                }
            } catch (e: Exception) {
                // Ignora chaves inexistentes
            }
        }
        return null
    }

    /**
     * Altera o estado do freio de mão/bloqueio de vídeo.
     * @param enable true = freio ativo/bloqueio ligado; false = freio ignorado/vídeo liberado
     */
    fun setBrakeBypass(enable: Boolean): Boolean {
        val key = getActiveBrakeKey() ?: "brake_enable" // fallback
        val value = if (enable) 1 else 0

        return try {
            // Tenta salvar em System
            var success = Settings.System.putInt(context.contentResolver, key, value)

            // Se falhar, tenta salvar em Secure/Global
            if (!success) {
                success = Settings.Secure.putInt(context.contentResolver, key, value)
            }

            Log.d(TAG, "Configuração '$key' alterada para $value: Sucesso = $success")
            success
        } catch (e: SecurityException) {
            Log.e(TAG, "Permissão WRITE_SETTINGS ou WRITE_SECURE_SETTINGS ausente!", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao alterar trava do freio de mão", e)
            false
        }
    }
}