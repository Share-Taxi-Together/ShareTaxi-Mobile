package com.example.sharedtaxitogether

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityLoginBinding
import com.example.sharedtaxitogether.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity:AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.findPasswdText.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }

        binding.loginButton.setOnClickListener {

        }
    }
}