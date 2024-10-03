package com.android.musicappandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.musicappandroid.databinding.ActivityPlayerBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.darkSlateBlue)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}