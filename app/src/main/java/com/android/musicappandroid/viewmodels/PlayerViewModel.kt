package com.android.musicappandroid.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.musicappandroid.models.Music

class PlayerViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentSongIndex = MutableLiveData<Int>()
    val currentSongIndex: LiveData<Int> = _currentSongIndex

    private val _songQueue = MutableLiveData<List<Music>>()
    val songQueue: LiveData<List<Music>> = _songQueue

    init {
        _isPlaying.value = false
        _currentSongIndex.value = 0
        _songQueue.value = emptyList()
    }

    fun playPause() {
        _isPlaying.value = !(_isPlaying.value ?: true)
    }

    fun setSongQueue(queue: List<Music>) {
        _songQueue.value = queue
        _currentSongIndex.value = 0
    }

    fun nextSong() {
        _currentSongIndex.value = (_currentSongIndex.value?.plus(1))?.let { nextIndex ->
            if (nextIndex >= (_songQueue.value?.size ?: 0)) 0 else nextIndex
        }
    }

    fun previousSong() {
        _currentSongIndex.value = (_currentSongIndex.value?.minus(1))?.let { previousIndex ->
            if (previousIndex < 0) (_songQueue.value?.size ?: 0) - 1 else previousIndex
        }
    }

    fun getCurrentSong(): Music? {
        return _songQueue.value?.get(_currentSongIndex.value ?: 0)
    }

    fun setPlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun setCurrentSongIndex(index: Int) {
        _currentSongIndex.value = index
    }
}