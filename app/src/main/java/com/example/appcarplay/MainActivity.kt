package com.example.appcarplay

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.appcarplay.presenter.CarMediaApp
import com.example.appcarplay.service.ScreenMirrorService
import com.example.appcarplay.util.getLocalIpAddress

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
                mirrorAddress = { getLocalIpAddress()?.let { "http://$it:${ScreenMirrorService.HTTP_PORT}" } }
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
}
