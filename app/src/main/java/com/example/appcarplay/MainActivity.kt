package com.example.appcarplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.appcarplay.presenter.CarMediaApp

class MainActivity : ComponentActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa ExoPlayer
        player = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
        player.setMediaItem(mediaItem)
        player.prepare()

        // Cria MediaSession vinculada ao ExoPlayer
        mediaSession = MediaSession.Builder(this, player).build()

        setContent {
            CarMediaApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        player.release()
    }
}
