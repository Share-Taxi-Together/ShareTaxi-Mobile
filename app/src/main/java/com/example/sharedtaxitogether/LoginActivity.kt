package com.example.sharedtaxitogether

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityLoginBinding
import com.example.sharedtaxitogether.databinding.ActivitySignupBinding

class LoginActivity:AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }
}