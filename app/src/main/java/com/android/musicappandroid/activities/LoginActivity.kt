package com.android.musicappandroid.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.musicappandroid.R
import com.android.musicappandroid.databases.UserDatabase
import com.android.musicappandroid.databinding.ActivityLoginBinding
import com.android.musicappandroid.interfaces.UserDao
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.darkSlateBlueNav)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDao = UserDatabase.getDatabase(applicationContext).userDao()

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.registerTV.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun login() {
        val username = binding.usernameET.text.toString()
        val password = binding.passwordET.text.toString()

        lifecycleScope.launch {
            val user = userDao.login(username, password)
            if (user != null) {
                val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.apply()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Nome de usuário ou senha inválidos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}