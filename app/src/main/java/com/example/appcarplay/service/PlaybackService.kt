package com.example.appcarplay.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.appcarplay.R

/**
 * Mantém a reprodução de áudio viva independente da Activity: sobrevive quando o
 * usuário troca de app no celular e é o que permite o AppCarPlay aparecer nos
 * controles de mídia do Android Auto (o Auto desenha sua própria UI a partir
 * desta MediaSession — vídeo não é suportado pela plataforma nesse contexto).
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    companion object {
        private const val CHANNEL_ID = "appcarplay_playback"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        // Precisa virar foreground em até 5s após startForegroundService(), sem depender
        // do player terminar de bufferizar (que pode ser mais lento que isso).
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        val player = ExoPlayer.Builder(this).build()
        player.setMediaItem(demoMediaItem())
        player.prepare()
        // Não toca sozinho: só inicia reprodução quando algo (UI, Android Auto) mandar.

        val sessionActivityIntent = packageManager
            .getLaunchIntentForPackage(packageName)
            ?.let { launchIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        mediaSession = MediaSession.Builder(this, player)
            .apply { sessionActivityIntent?.let { setSessionActivity(it) } }
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val session = mediaSession ?: return
        if (!session.player.playWhenReady || session.player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Reprodução de mídia",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AppCarPlay")
            .setContentText("Reproduzindo mídia")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

    private fun demoMediaItem(): MediaItem =
        MediaItem.Builder()
            .setUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("AppCarPlay")
                    .setArtist("Faixa de demonstração")
                    .build()
            )
            .build()
}
