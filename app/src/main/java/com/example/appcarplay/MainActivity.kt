package com.example.appcarplay

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.appcarplay.presenter.CarMediaApp
import com.example.appcarplay.service.ScreenMirrorService
import com.example.appcarplay.util.getLocalIpAddress
import com.example.appcarplay.util.isIgnoringBatteryOptimizations
import com.example.appcarplay.util.isTouchAccessibilityServiceEnabled

class MainActivity : ComponentActivity() {

    private val mirrorPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val serviceIntent = Intent(this, ScreenMirrorService::class.java).apply {
                    putExtra(ScreenMirrorService.EXTRA_RESULT_CODE, result.resultCode)
                    putExtra(ScreenMirrorService.EXTRA_RESULT_DATA, result.data)
                }
                startForegroundService(serviceIntent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CarMediaApp(
                isMirroring = { ScreenMirrorService.isRunning },
                onStartMirroring = { requestScreenMirroring() },
                onStopMirroring = { stopScreenMirroring() },
                mirrorAddress = { getLocalIpAddress()?.let { "http://$it:${ScreenMirrorService.HTTP_PORT}" } },
                isTouchControlEnabled = { isTouchAccessibilityServiceEnabled(this) },
                onEnableTouchControl = { openAccessibilitySettings() },
                isBackgroundSafe = { isIgnoringBatteryOptimizations(this) },
                onEnableBackgroundSafe = { requestIgnoreBatteryOptimizations() }
            )
        }
    }

    private fun requestScreenMirroring() {
        val projectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mirrorPermissionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    private fun stopScreenMirroring() {
        val stopIntent = Intent(this, ScreenMirrorService::class.java).apply {
            action = ScreenMirrorService.ACTION_STOP
        }
        startService(stopIntent)
    }

    private fun openAccessibilitySettings() {
        // Tenta abrir direto a tela de detalhe do serviço (evita o usuário ter que
        // procurar "AppCarPlay" dentro de "Serviços instalados"). Esse truque usa
        // chaves internas do Settings do Android e funciona na maioria dos aparelhos,
        // mas nem todo fabricante respeita — por isso caímos para a tela genérica.
        val componentName = android.content.ComponentName(
            this, com.example.appcarplay.service.TouchAccessibilityService::class.java
        ).flattenToString()

        val direct = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            putExtra(":settings:fragment_args_key", componentName)
            putExtra(
                ":settings:show_fragment_args",
                Bundle().apply { putString(":settings:fragment_args_key", componentName) }
            )
        }

        try {
            startActivity(direct)
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun requestIgnoreBatteryOptimizations() {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }
}
