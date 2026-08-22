package com.example.appcarplay.presenter

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcarplay.data.AppCatalog
import com.example.appcarplay.data.AppPreferences
import com.example.appcarplay.presenter.components.AppSelectionDialog
import com.example.appcarplay.presenter.components.CarMediaAppGrid
import com.example.appcarplay.presenter.components.RacingBackdrop
import com.example.appcarplay.ui.theme.consoleAccent
import com.example.appcarplay.ui.theme.consoleBackground
import com.example.appcarplay.ui.theme.consoleSurface
import com.example.appcarplay.ui.theme.textPrimary
import com.example.appcarplay.ui.theme.textSecondary
import com.example.appcarplay.util.BrakeSettingsManager
import com.example.appcarplay.util.VehicleBrakeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CarMediaApp() {
    val context = LocalContext.current
    val preferences = remember { AppPreferences(context) }

    var selectedPackages by remember { mutableStateOf(preferences.getSelectedPackages()) }
    var showManageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val vehicleBrakeManager = VehicleBrakeManager(context)
            val brakeSettingsManager = BrakeSettingsManager(context)

            // 1. Checa se o freio está ativo via HAL do Android (Android Automotive/CarPropertyManager)
            val isBrakeActiveInCar = vehicleBrakeManager.isParkingBrakeOn()

            // 2. Tenta checar também via chave do sistema (para multimídias 2-Din/chinesas)
            val activeKey = brakeSettingsManager.getActiveBrakeKey()
            val isBrakeActiveInSystem = if (activeKey != null) {
                try {
                    android.provider.Settings.System.getInt(context.contentResolver, activeKey, 0) == 1 ||
                            android.provider.Settings.Secure.getInt(context.contentResolver, activeKey, 0) == 1
                } catch (e: Exception) {
                    false
                }
            } else false

            // Se o freio estiver ativado em qualquer um dos cenários, força a liberação/bypass
            if (isBrakeActiveInCar || isBrakeActiveInSystem) {
                brakeSettingsManager.setBrakeBypass(false)
            }
        }
    }

    fun updateSelection(newSelection: Set<String>) {
        selectedPackages = newSelection
        preferences.setSelectedPackages(newSelection)
    }

    val selectedApps = AppCatalog.all.filter { selectedPackages.contains(it.pack) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = consoleBackground
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Sombra de fundo com tema de corrida — desenhada, não uma foto, pra ficar leve e discreta.
                RacingBackdrop(modifier = Modifier.fillMaxSize())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    consoleBackground.copy(alpha = 0.55f),
                                    consoleSurface.copy(alpha = 0.55f)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DashboardHeader(onManageApps = { showManageDialog = true })

                    Text(
                        text = "Aplicativos",
                        fontSize = 20.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        CarMediaAppGrid(
                            apps = selectedApps,
                            onRemoveApp = { app -> updateSelection(selectedPackages - app.pack) },
                            onAddApp = { showManageDialog = true }
                        )
                    }
                }
            }
        }

        if (showManageDialog) {
            AppSelectionDialog(
                selectedPackages = selectedPackages,
                onToggleApp = { app ->
                    updateSelection(
                        if (selectedPackages.contains(app.pack)) {
                            selectedPackages - app.pack
                        } else {
                            selectedPackages + app.pack
                        }
                    )
                },
                onDismiss = { showManageDialog = false }
            )
        }
    }
}

@Composable
private fun DashboardHeader(onManageApps: () -> Unit) {
    val clock = remember { mutableStateOf(currentTime()) }

    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")
    val statusAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_alpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "AppCarPlay",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = "Espelhamento de mídia",
                fontSize = 13.sp,
                color = textSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Gerenciar apps",
                tint = textSecondary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onManageApps)
            )
            Icon(
                imageVector = Icons.Filled.BluetoothConnected,
                contentDescription = "Bluetooth conectado",
                tint = consoleAccent.copy(alpha = statusAlpha),
                modifier = Modifier.size(20.dp)
            )
            Icon(
                imageVector = Icons.Filled.SignalWifi4Bar,
                contentDescription = "Wi-Fi conectado",
                tint = consoleAccent.copy(alpha = statusAlpha),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = clock.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
        }
    }
}

private fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

@Preview(showBackground = true)
@Composable
fun CarMediaAppPreview() {
    CarMediaApp()
}
