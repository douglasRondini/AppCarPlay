package com.example.appcarplay.presenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcarplay.data.AppCatalog
import com.example.appcarplay.domain.model.AppItem
import com.example.appcarplay.ui.theme.consoleAccent
import com.example.appcarplay.ui.theme.consoleBorder
import com.example.appcarplay.ui.theme.consoleSurface
import com.example.appcarplay.ui.theme.consoleSurfaceElevated
import com.example.appcarplay.ui.theme.textPrimary

@Composable
fun AppSelectionDialog(
    selectedPackages: Set<String>,
    onToggleApp: (AppItem) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = consoleSurface,
        title = {
            Text(
                text = "Gerenciar apps",
                color = textPrimary,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AppCatalog.all, key = { it.pack }) { app ->
                    val isSelected = selectedPackages.contains(app.pack)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(consoleSurfaceElevated)
                            .clickable { onToggleApp(app) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = app.icon,
                            contentDescription = app.name,
                            tint = app.accent,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = app.name,
                            color = textPrimary,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) consoleAccent else consoleBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = consoleSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Concluído", color = consoleAccent)
            }
        }
    )
}
