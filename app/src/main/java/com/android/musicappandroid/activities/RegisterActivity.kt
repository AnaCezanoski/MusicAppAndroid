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
import com.android.musicappandroid.databinding.ActivityRegisterBinding
import com.android.musicappandroid.interfaces.UserDao
import com.android.musicappandroid.models.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.darkSlateBlueNav)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDao = UserDatabase.getDatabase(applicationContext).userDao()

        binding.btnRegister.setOnClickListener {
            register()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun register() {
        val username = binding.newUsernameET.text.toString()
        val password = binding.newPasswordET.text.toString()
        val confirmPassword = binding.confirmPasswordET.text.toString()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas não são iguais.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser == null) {
                val newUser = User(username = username, password = password)
                userDao.insert(newUser)
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.putExtra("REGISTERED_USERNAME", username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@RegisterActivity, "Nome de usuário já existe.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}