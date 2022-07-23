package com.example.sharedtaxitogether

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
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
                            .setPositiveButton("로그인 하러가기",
                                DialogInterface.OnClickListener { dialog, id ->
                                    startActivity(Intent(this, LoginActivity::class.java))
                                })
                            .setNegativeButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                binding.passwdText.text = document["password"] as String
                            })

                        builder.show()
//                        val passwd = document["password"] as String
//                        binding.passwdText.text = passwd
                    }
                } else {
                    binding.passwdText.text = "메일 또는 전화번호가 틀렸습니다"
                }
            }
//            .addOnSuccessListener { documents ->
//                if (!documents.isEmpty) {
//                    for(document in documents){
//                        Log.d("thisiii", "${document.id} => ${document.data}")
//                        Log.d("thisiiiiiiiii", document.data.toString())
//
////                        binding.passwdText.setText(document.data.toString())
//                    }
//
//                    Toast.makeText(this, "확인되었습니다", Toast.LENGTH_SHORT).show()
////                    binding.editEmail.isEnabled = false
////                    binding.editPhone.isEnabled = false
////                    binding.layoutStep2.visibility = View.VISIBLE
//                } else {
//                    Toast.makeText(this, "실패 : 메일주소 또는 전화번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
//                }
//            }
    }
}