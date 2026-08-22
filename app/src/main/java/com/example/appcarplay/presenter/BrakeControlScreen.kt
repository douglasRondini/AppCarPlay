package com.example.appcarplay.presenter

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcarplay.util.BrakeSettingsManager
import com.example.appcarplay.util.VehicleBrakeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BrakeControlScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val brakeSettingsManager = remember { BrakeSettingsManager(context) }
    val vehicleBrakeManager = remember { VehicleBrakeManager(context) }

    var isBypassEnabled by remember { mutableStateOf(false) }
    var activeKeyFound by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun checkCurrentBrakeState() {
        scope.launch(Dispatchers.IO) {
            val key = brakeSettingsManager.getActiveBrakeKey()
            activeKeyFound = key

            val systemValue = if (key != null) {
                try {
                    android.provider.Settings.System.getInt(context.contentResolver, key, 1) == 0 ||
                            android.provider.Settings.Secure.getInt(context.contentResolver, key, 1) == 0
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }

            isBypassEnabled = systemValue || !vehicleBrakeManager.isParkingBrakeOn()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        checkCurrentBrakeState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Brake Control Utility",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ferramenta isolada para liberação de vídeo",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ESTADO ATUAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                val statusText = when {
                    isLoading -> "VERIFICANDO..."
                    isBypassEnabled -> "LIBERADO (Bypass Ativo)"
                    else -> "BLOQUEADO (Trava Ativa)"
                }

                val statusColor = when {
                    isLoading -> Color.Gray
                    isBypassEnabled -> Color(0xFF4CAF50)
                    else -> Color(0xFFE53935)
                }

                Text(
                    text = statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColor
                )

                if (activeKeyFound != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chave: $activeKeyFound",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val newBypassState = !isBypassEnabled
                    val success = brakeSettingsManager.setBrakeBypass(!newBypassState)

                    withContext(Dispatchers.Main) {
                        if (success) {
                            isBypassEnabled = newBypassState
                            Toast.makeText(context, "Configuração alterada com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Erro: Conceda WRITE_SECURE_SETTINGS via ADB.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isBypassEnabled) Color(0xFFE53935) else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isBypassEnabled) "BLOQUEAR VÍDEO" else "LIBERAR VÍDEO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}