package com.android.musicappandroid.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.musicappandroid.models.FavoriteSong

@Dao
interface FavoriteDao {
    @Insert
    suspend fun addFavorite(song: FavoriteSong)

    @Query("SELECT * FROM favorite_songs")
    suspend fun getFavorites(): List<FavoriteSong>

    @Query("DELETE FROM favorite_songs WHERE songId = :id")
    suspend fun removeFavorite(id: String)
}