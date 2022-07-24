package com.example.sharedtaxitogether

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.sharedtaxitogether.databinding.ActivityLoginBinding
import com.example.sharedtaxitogether.model.User
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//TODO viewModel 적용 (https://ddolcat.tistory.com/603)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: LoginViewModel
    private lateinit var room: AppDatabase

    //자동로그인
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    var mBackWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.viewModel = viewModel
        db = Firebase.firestore

        room = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserDB"
        ).fallbackToDestructiveMigration().build()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

        checkSharedPreference()
        bind()
    }

    private fun checkSharedPreference() {
        if ((sharedPreferences.getString("email", "") != "")
            && (sharedPreferences.getString("password", "") != "")
        ) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onBackPressed() {
        //뒤로가기 버튼 클릭
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish()    //액티비티 종료
        }
    }

    private fun bind() {
        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.findPasswdText.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            viewModel.email.value = binding.loginMailEdit.text.toString()
            viewModel.password.value = binding.loginPasswdEdit.text.toString()
            val usersRef = db.collection("users")

            usersRef.whereEqualTo("email", viewModel.email.value)
                .whereEqualTo("password", viewModel.password.value)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        for (document in result) {
                            viewModel.uid.value = document["uid"] as String
                            viewModel.phone.value = document["phone"] as String
                            viewModel.gender.value = document["gender"] as String
                            viewModel.nickname.value = document["nickname"] as String
                            viewModel.imgUrl.value = document["imgUrl"] as String?
                            viewModel.score.value = document["score"] as String?
                            viewModel.countAddress.value = document["countAddress"] as String?
                        }
                        val user: User = viewModel.insertUserInfo()
                        Log.d(TAG, user.toString())

                        Thread {
                            room.userDao().insertUser(user)
                        }.start()

                        //자동로그인 email, password 저장
                        editor.putString("email", viewModel.email.value!!)
                        editor.putString("password", viewModel.password.value!!)
                        editor.commit()

                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "로그인 실패 : 아이디 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}