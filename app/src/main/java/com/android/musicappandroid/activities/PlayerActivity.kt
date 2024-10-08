package com.android.musicappandroid.activities

import com.android.musicappandroid.FavoriteActivity
import com.android.musicappandroid.MainActivity
import com.android.musicappandroid.R
import com.android.musicappandroid.models.Music
import com.android.musicappandroid.services.MusicService
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
        var isFavorite: Boolean = false
        lateinit var binding: ActivityPlayerBinding
        lateinit var nowPlayingId : String
        var fIndex: Int = -1
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.darkSlateBlue)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeLayout()
        binding.backBtn.setOnClickListener { finish() }
        binding.playPauseBtn.setOnClickListener { if (isPlaying) pauseMusic() else playMusic() }
        binding.previousBtn.setOnClickListener { prevNextSong(increment = false) }
        binding.nextBtn.setOnClickListener { prevNextSong(increment = true) }
        binding.favoriteBtn.setOnClickListener {
            if(isFavorite){
                isFavorite = false
                binding.favoriteBtn.setImageResource(R.drawable.favorite_empty_icon)
                FavoriteActivity.favoriteSongs.removeAt(fIndex)
            } else {
                isFavorite = true
                binding.favoriteBtn.setImageResource(R.drawable.favorite_icon)
                FavoriteActivity.favoriteSongs.add(musicList[songPosition])
            }
        }
    }

    private fun setLayout() {
        fIndex = favoriteChecker(musicList[songPosition].id)
        Glide.with(this)
            .load(musicList[songPosition].artUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.music_player_icon_slash_screen)
                .centerCrop()).into(binding.songImg)
        binding.songName.text = musicList[songPosition].title
        if(isFavorite)
            binding.favoriteBtn.setImageResource(R.drawable.favorite_icon)
        else
            binding.favoriteBtn.setImageResource(R.drawable.favorite_empty_icon)
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
            "FavoriteAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(FavoriteActivity.favoriteSongs)
                setLayout()
            }
            "MusicAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(MainActivity.MusicList)
                setLayout()
            }
            "MainActivity" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
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