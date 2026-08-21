package com.example.appcarplay.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class AppItem(
    val name: String,
    val icon: ImageVector,
    val url: String?,
    val pack: String
)
