package com.example.sharedtaxitogether

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sharedtaxitogether.databinding.ActivityMainBinding
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val addListFragment = AddListFragment()
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()

    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var mBackWait: Long = 0     //뒤로가기 연속 클릭 대기시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        auth = Firebase.auth

        getUserInfo()

        //앨범 접근 권한 - 허용하지 않으면 유저정보 띄울 수 없음(프로필사진)
        //TODO 권한 거부했을 경우
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1001
        )

        initNavigationBar()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else finish()    //액티비티 종료
    }

    //fragment간 이동
    fun replaceFragment(fragment: Fragment){
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment).commit()
    }

    private fun getUserInfo() {
        val user = auth.currentUser

        // user정보 viewModel에 저장
        db.collection("users")
            .whereEqualTo("uid", user!!.uid)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (document in it) {
                        viewModel.uid.value = document["uid"] as String
                        viewModel.email.value = document["email"] as String
                        viewModel.password.value = document["password"] as String
                        viewModel.phone.value = document["phone"] as String
                        viewModel.gender.value = document["gender"] as String
                        viewModel.nickname.value = document["nickname"] as String
                        viewModel.imgUrl.value = document["imgUrl"] as String?
                        viewModel.score.value = document["score"] as String?
                        viewModel.countAddress.value = document["countAddress"] as String?
                    }
                }
            }
    }

    private fun initNavigationBar() {
        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.waiting_list -> changeFragment(listFragment)
                    R.id.add_list -> changeFragment(addListFragment)
                    R.id.chatting -> changeFragment(chatFragment)
                    R.id.profile -> changeFragment(profileFragment)
                }
                true
            }
            selectedItemId = R.id.waiting_list
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}