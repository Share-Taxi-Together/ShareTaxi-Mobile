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

        bind()
    }

    private fun bind() {
        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.findPasswdText.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginMailEdit.text.toString()
            val password = binding.loginPasswdEdit.text.toString()
            val usersRef = db.collection("users")

            usersRef.whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener {
                    if(!it.isEmpty){
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "로그인 실패 : 아이디 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}