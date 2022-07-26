package com.example.sharedtaxitogether

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val addListFragment = AddListFragment()
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()

    private val pref: UserSharedPreferences by lazy { UserSharedPreferences(this) }
    private lateinit var room: AppDatabase

    private var mBackWait: Long = 0     //뒤로가기 연속 클릭 대기시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        room = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserDB"
        ).fallbackToDestructiveMigration().build()

        //앨범 접근 권한 - 허용하지 않으면 유저정보 띄울 수 없음(프로필사진)
        //TODO 권한 거부했을 경우
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1001
        )

        initNavigationBar()
    }

    override fun onStart() {
        super.onStart()

        initUserInfo()
    }

    private fun initUserInfo() {
        val user = pref.getUser()

        Log.d("main - user", user.toString())
        Thread {
            room.userDao().insertUser(user)
        }.start()
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