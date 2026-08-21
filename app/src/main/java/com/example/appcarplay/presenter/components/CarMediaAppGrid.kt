package com.example.appcarplay.presenter.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appcarplay.data.DataProvider

@Composable
fun CarMediaAppGrid() {
    val context = LocalContext.current
    val data = DataProvider()
    val apps = data.apps

    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(apps) { app ->
            DashboardButton(app.name, app.icon, app.accent) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.url))
                intent.setPackage(app.pack)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(app.url)
                    )
                    context.startActivity(webIntent)
                }
            }
        }
    }
}
