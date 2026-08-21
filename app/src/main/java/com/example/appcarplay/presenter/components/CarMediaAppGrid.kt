package com.example.appcarplay.presenter.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appcarplay.data.DataProvider

@Composable
fun CarMediaAppGrid() {
    val context = LocalContext.current
    val data = DataProvider()
    val apps = data.apps

    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        items(apps.chunked(2)) { columApps ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                columApps.forEach { app ->
                    DashboardButton(app.name, app.icon) {
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


    }


}