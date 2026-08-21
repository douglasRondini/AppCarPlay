package com.example.appcarplay.presenter.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcarplay.ui.theme.white

@Composable
fun DashboardButton(
    label: String,
    iconRes: ImageVector,   // ícone do app
    onclick: () -> Unit
) {
    Button(
        onClick = onclick,
        modifier = Modifier
            .wrapContentSize()
            .height(120.dp), // mais alto para caber ícone + texto
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Icon(
               imageVector = iconRes,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = label,
                fontSize = 16.sp,
                color = white
            )
        }
    }
}
