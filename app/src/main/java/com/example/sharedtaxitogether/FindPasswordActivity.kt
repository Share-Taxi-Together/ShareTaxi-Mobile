package com.example.sharedtaxitogether

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityFindpasswordBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FindPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindpasswordBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        bind()
    }

    private fun bind() {
        binding.btnResetPasswd.setOnClickListener {
            resetPassword()
        }
        binding.btnBack.setOnClickListener {
            cancelFindPassword()
        }
    }

    private fun cancelFindPassword() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("비밀번호 찾기를 취소하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                finish()
            }
            .setNegativeButton("아니요") { _, _ -> }
        builder.show()
    }

    private fun resetPassword() {
        val email = binding.editEmail.text.toString()
        val phone = "+82" + binding.editPhone.text.toString().slice(1..10)
        val usersRef = db.collection("users")

        usersRef.whereEqualTo("email", email)
            .whereEqualTo("phone", phone)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("비밀번호")
                            .setMessage("${document["nickname"]}님의 비밀번호는 ${document["password"]}입니다.")
                            .setPositiveButton("로그인 하러가기") { _, _ ->
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.putExtra("email", binding.editEmail.text.toString())
                                intent.putExtra("password", document["password"] as String)
                                startActivity(intent)
                            }
                        builder.show()
                    }
                } else binding.passwdText.text = "메일 또는 전화번호가 틀렸습니다"
            }
    }
}