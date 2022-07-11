package com.example.sharedtaxitogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.sharedtaxitogether.mailAuth.GMailSender

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val email = editEmail.text.toString()

            GMailSender().sendEmail(email)
            Toast.makeText(applicationContext, "이메일을 확인해주세요", Toast.LENGTH_SHORT).show()
        }
    }
}