package com.example.appcarplay.presenter

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.appcarplay.R
import com.example.appcarplay.presenter.components.CarMediaAppGrid
import com.example.appcarplay.presenter.components.DashboardButton
import com.example.appcarplay.ui.theme.black
import com.example.appcarplay.ui.theme.white
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Youtube
import compose.icons.simpleicons.Youtubemusic

@Composable
fun CarMediaApp() {

    val context = LocalContext.current

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Car Media",
                    fontSize = 32.sp,
                    color = white,
                    fontWeight = FontWeight.Bold
                )

                CarMediaAppGrid()


//                // Botão para abrir o youtube instalado
//                DashboardButton(
//                    "YouTube", SimpleIcons.Youtube
//                ) {
//                    val intent = Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://www.youtube.com/")
//                    )
//                    intent.setPackage("com.google.android.youtube") // força abrir no app YouTube
//                    try {
//                        context.startActivity(intent)
//                    } catch (e: Exception) {
//                        // fallback para navegador
//                        val webIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
//                        )
//                        context.startActivity(webIntent)
//                    }
//                }

//                DashboardButton(
//                    "YT Music", SimpleIcons.Youtubemusic
//                ) {
//                    val intent = Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://music.youtube.com/") // URL genérica do YouTube Music
//                    )
//                    intent.setPackage("com.google.android.apps.youtube.music") // força abrir no app YouTube Music
//
//                    try {
//                        context.startActivity(intent)
//                    } catch (e: Exception) {
//                        // fallback para navegador se o app não estiver instalado
//                        val webIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse("https://music.youtube.com/")
//                        )
//                        context.startActivity(webIntent)
//                    }
//                }


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarMediaAppPreview() {
    val context = LocalContext.current
    CarMediaApp()
}