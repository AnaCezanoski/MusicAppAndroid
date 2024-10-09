package com.android.musicappandroid.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        val registeredUsername = intent.getStringExtra("REGISTERED_USERNAME")
        if (registeredUsername != null) {
            binding.usernameET.setText(registeredUsername)
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.registerTV.setOnClickListener {
            binding.registerTV.setTextColor(ContextCompat.getColor(this, R.color.holo_orange_light))
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.registerTV.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    binding.registerTV.setTextColor(ContextCompat.getColor(this, R.color.holo_orange_light))
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.registerTV.setTextColor(ContextCompat.getColor(this, R.color.secondary_text_light))
                }
            }
            false
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

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = userDao.login(username, password)
            if (user != null) {
                Toast.makeText(this@LoginActivity, "Bem-vindo, ${user.username}!", Toast.LENGTH_SHORT).show()
                val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.apply()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("USERNAME", user.username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Nome de usuário ou senha inválidos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}