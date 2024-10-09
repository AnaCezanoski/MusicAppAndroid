package com.android.musicappandroid.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs")
data class FavoriteSong(
    @PrimaryKey(autoGenerate = true) val songId: Int = 0,
    val songName: String,
    val albumName: String,
    val duration: String
)
