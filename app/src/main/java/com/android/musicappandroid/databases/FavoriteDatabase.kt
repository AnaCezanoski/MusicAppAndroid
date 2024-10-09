package com.android.musicappandroid.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.musicappandroid.interfaces.FavoriteDao
import com.android.musicappandroid.models.FavoriteSong

@Database(entities = [FavoriteSong::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}