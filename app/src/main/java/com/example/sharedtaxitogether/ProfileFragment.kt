package com.example.sharedtaxitogether

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.sharedtaxitogether.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var room: AppDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    private fun bind() {
        binding.logoutTextView.setOnClickListener {
            val user = room.userDao().getUser()
            Log.d("profileFragment", user.toString())
            room.userDao().delete(user)

            startActivity(Intent(mainActivity,LoginActivity::class.java))
        }
        binding.withdrawTextView.setOnClickListener {
            val token = room.userDao().getUid()
            db.collection("users").document(token)
                .delete()
                .addOnSuccessListener {
                    Log.d("profileFragment","회원탈퇴 성공")
                    startActivity(Intent(mainActivity,LoginActivity::class.java))
                }
        }
        binding.profileImgView.setOnClickListener {
            // TODO 프로필 변경
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        db = Firebase.firestore

        bind()

        room = Room.databaseBuilder(
            mainActivity,
            AppDatabase::class.java, "UserDB"
        ).allowMainThreadQueries().build()

        Thread {
            activity?.runOnUiThread {
//            val userInfo = room.userDao().getAll()
//            Log.d("userInfo", userInfo.toString())
                when (room.userDao().getGender()) {
                    "Male" -> binding.genderImgView.setImageResource(R.drawable.male)
                    "Female" -> binding.genderImgView.setImageResource(R.drawable.female)
                }
                binding.nicknameTextView.text = room.userDao().getNickname()
                binding.scoreTextView.text = room.userDao().getScore()
                binding.emailTextView.text = room.userDao().getEmail()
                binding.passwdTextView.text = room.userDao().getPassword()
                binding.phoneTextView.text = room.userDao().getPhone()
            }
        }.start()

        return binding.root
    }

}