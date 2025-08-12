package com.example.broadcstapplication


import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.broadcstapplication.R.raw.my_song

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(this, my_song) // song.mp3 from res/raw
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // not binding, only start/stop
    }
}
