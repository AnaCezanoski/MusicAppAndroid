package com.android.musicappandroid.activities

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
import androidx.lifecycle.ViewModelProvider
import com.android.musicappandroid.databinding.ActivityPlayerBinding
import com.android.musicappandroid.viewmodels.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PlayerActivity : AppCompatActivity(), ServiceConnection {

    companion object {
        lateinit var musicList : ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService : MusicService? = null
        var isFavorite: Boolean = false
        var fIndex: Int = -1
    }

    private lateinit var playerViewModel: PlayerViewModel

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.darkSlateBlue)

        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeLayout()

        val sharedPreferences = getSharedPreferences("PlayerPreferences", MODE_PRIVATE)
        val isPlayingFromPrefs = sharedPreferences.getBoolean("isPlaying", false)
        val currentSongIndexFromPrefs = sharedPreferences.getInt("currentSongIndex", 0)

        playerViewModel.setPlayingState(isPlayingFromPrefs)
        playerViewModel.setCurrentSongIndex(currentSongIndexFromPrefs)

        initializeSongQueue()

        if (playerViewModel.isPlaying.value == true) {
            createMediaPlayer()
        }

        binding.backBtn.setOnClickListener { finish() }
        binding.playPauseBtn.setOnClickListener { if (playerViewModel.isPlaying.value == true) pauseMusic() else playMusic() }
        binding.previousBtn.setOnClickListener { prevNextSong(increment = false) }
        binding.nextBtn.setOnClickListener { prevNextSong(increment = true) }
        binding.favoriteBtn.setOnClickListener {
            if(isFavorite){
                if (fIndex != -1) {
                    isFavorite = false
                    binding.favoriteBtn.setImageResource(R.drawable.favorite_empty_icon)
                    FavoriteActivity.favoriteSongs.removeAt(fIndex)
                }
            } else {
                isFavorite = true
                binding.favoriteBtn.setImageResource(R.drawable.favorite_icon)
                FavoriteActivity.favoriteSongs.add(musicList[songPosition])
            }
        }

        fIndex = favoriteChecker(musicList[songPosition].id)
        setLayout()

        playerViewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
            } else {
                binding.playPauseBtn.setIconResource(R.drawable.play_icon)
            }
        }

        playerViewModel.currentSongIndex.observe(this) { index ->
            songPosition = index
            setLayout()
            createMediaPlayer()
        }

    }

    private fun initializeSongQueue() {
        musicList = ArrayList()
        musicList.addAll(MainActivity.MusicList)
        playerViewModel.setSongQueue(musicList, songPosition)
        setLayout()
    }

    private fun favoriteChecker(id: String): Int {
        FavoriteActivity.favoriteSongs.forEachIndexed { index, music ->
            if (id == music.id) {
                return index
            }
        }
        return -1
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
            val currentSong = playerViewModel.getCurrentSong()
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            currentSong?.let {
                musicService!!.mediaPlayer!!.setDataSource(it.path)
                musicService!!.mediaPlayer!!.prepare()
                musicService!!.mediaPlayer!!.start()
            }
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")) {
            "FavoriteAdapter" -> {
                startAndBindMusicService()
                musicList = ArrayList(FavoriteActivity.favoriteSongs)
            }
            "MusicAdapter" -> {
                startAndBindMusicService()
                musicList = ArrayList(MainActivity.MusicList)
            }
            "MainActivity" -> {
                startAndBindMusicService()
                musicList = ArrayList(MainActivity.MusicList)
                musicList.shuffle()
            }
        }
        setLayout()
    }

    private fun startAndBindMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun playMusic() {
        musicService!!.mediaPlayer!!.start()
        playerViewModel.setPlayingState(true)
    }

    private fun pauseMusic() {
        musicService!!.mediaPlayer!!.pause()
        playerViewModel.setPlayingState(false)
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            playerViewModel.nextSong()
        } else {
            playerViewModel.previousSong()
        }

        setLayout()
        createMediaPlayer()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        songPosition = intent.getIntExtra("index", 0)
        createMediaPlayer()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("PlayerPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isPlaying", playerViewModel.isPlaying.value == true)
        editor.putInt("currentSongIndex", playerViewModel.currentSongIndex.value ?: 0)
        editor.apply()
    }
}