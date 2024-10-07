package com.android.musicappandroid

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.android.musicappandroid.databinding.ActivityPlayerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PlayerActivity : AppCompatActivity(), ServiceConnection {

    companion object {
        lateinit var musicList : ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService : MusicService? = null
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.darkSlateBlue)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        initializeLayout()
        binding.backBtn.setOnClickListener { finish() }
        binding.playPauseBtn.setOnClickListener {
            if(isPlaying) pauseMusic()
            else playMusic()
        }
        binding.previousBtn.setOnClickListener { prevNextSong(increment = false) }
        binding.nextBtn.setOnClickListener { prevNextSong(increment = true) }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(musicList[songPosition].artUri)
            .apply(RequestOptions()
            .placeholder(R.drawable.music_player_icon_slash_screen)
            .centerCrop()).into(binding.songImg)
        binding.songName.text = musicList[songPosition].title
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        } catch (e: Exception) {
            return
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicList = ArrayList()
                musicList.addAll(MainActivity.MusicList)
                setLayout()
            }
            "MainActivity" -> {
                musicList = ArrayList()
                musicList.addAll(MainActivity.MusicList)
                musicList.shuffle()
                setLayout()
            }
        }
    }
    private fun playMusic() {
        binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic () {
        binding.playPauseBtn.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else{
            setSongPosition(increment = false
            )
            setLayout()
            createMediaPlayer()
        }
    }
    private fun setSongPosition(increment: Boolean) {
        if(increment) {
            if(musicList.size - 1 == songPosition)
                songPosition = 0
            else ++songPosition
        } else {
            if(0 == songPosition)
                songPosition = musicList.size - 1
            else --songPosition
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }
}