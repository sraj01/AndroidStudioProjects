package com.example.syncmusic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()
    private val CHANNEL_ID = "MUSIC_CHANNEL"

    override fun onBind(intent: Intent?): IBinder = binder

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // ✅ Load the music file from res/raw/my_music.mp3
        mediaPlayer = MediaPlayer.create(this, R.raw.my_music)
        mediaPlayer?.isLooping = true
    }

    // ✅ Create Notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // ✅ Play Music
    fun playMusic() {
        mediaPlayer?.start()
        showNotification("Playing Music")
    }

    // ✅ Pause Music
    fun pauseMusic() {
        mediaPlayer?.pause()
        showNotification("Music Paused")
    }

    // ✅ Check if playing
    fun isPlaying() = mediaPlayer?.isPlaying ?: false

    fun getDuration() = mediaPlayer?.duration ?: 0

    fun getCurrentPosition() = mediaPlayer?.currentPosition ?: 0

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    // ✅ Show Foreground Notification
    private fun showNotification(content: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText(content)
            .setSmallIcon(R.drawable.music_note)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()

    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()
    }

}
