package com.example.appcarplay.presenter

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcarplay.presenter.components.CarMediaAppGrid
import com.example.appcarplay.ui.theme.consoleAccent
import com.example.appcarplay.ui.theme.consoleBackground
import com.example.appcarplay.ui.theme.consoleSurface
import com.example.appcarplay.ui.theme.textPrimary
import com.example.appcarplay.ui.theme.textSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CarMediaApp() {

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = consoleBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(consoleBackground, consoleSurface)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DashboardHeader()

                Text(
                    text = "Aplicativos",
                    fontSize = 20.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Box(modifier = Modifier.weight(1f)) {
                    CarMediaAppGrid()
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    val clock = remember { mutableStateOf(currentTime()) }

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
                imageVector = Icons.Filled.BluetoothConnected,
                contentDescription = "Bluetooth conectado",
                tint = consoleAccent,
                modifier = Modifier.size(20.dp)
            )
            Icon(
                imageVector = Icons.Filled.SignalWifi4Bar,
                contentDescription = "Wi-Fi conectado",
                tint = consoleAccent,
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
