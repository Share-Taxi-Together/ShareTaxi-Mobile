package com.example.sharedtaxitogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val addListFragment = AddListFragment()
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigationBar()
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