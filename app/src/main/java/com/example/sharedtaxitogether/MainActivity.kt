package com.example.sharedtaxitogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val addListFragment = AddListFragment()
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()
    private var mBackWait: Long = 0     //뒤로가기 연속 클릭 대기시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigationBar()
    }

    override fun onBackPressed() {
        //뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >= 2000){
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else{
            finish()    //액티비티 종료
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        if(auth.currentUser != null){
//            Log.d("123mainActivity", "${auth.currentUser.}")
//            startActivity(Intent(this, MainActivity::class.java))
//        } else{
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//    }

    private fun initNavigationBar() {
        val navigation=findViewById<BottomNavigationView>(R.id.navigation)
        navigation.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.waiting_list -> {
                        changeFragment(listFragment)
                    }
                    R.id.add_list -> {
                        changeFragment(addListFragment)
                    }
                    R.id.chatting -> {
                        changeFragment(chatFragment)
                    }
                    R.id.profile -> {
                        changeFragment(profileFragment)
                    }
                }
                true
            }
            selectedItemId = R.id.waiting_list
        }
    }

    private fun changeFragment(fragment: Fragment){
        if(fragment == profileFragment){
            //유저정보 넘겨주기
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}