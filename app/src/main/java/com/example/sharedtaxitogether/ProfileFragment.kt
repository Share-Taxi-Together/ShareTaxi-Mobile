package com.example.sharedtaxitogether

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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

    private fun bind() {
        binding.modifyInfoTextView.setOnClickListener {

        }
        binding.logoutTextView.setOnClickListener {
            logout()
        }
        binding.withdrawTextView.setOnClickListener {
            withdraw()
        }
        binding.profileButton.setOnClickListener {
            // TODO 프로필 변경
//            Log.d(TAG, "프로필 변경 버튼 클릭")
//            when {
//                ContextCompat.checkSelfPermission(
//                    mainActivity,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED
//                ->
//                    //권한 존재 이미지 가져오기
//                    getImageFomAlbum()
//            }
//            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) ->{
//            showPermissionContextPopup()
//        }

        }
    }
//
//    private fun getImageFomAlbum() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        startActivityForResult(intent, 1)
//    }

    private fun withdraw() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("회원탈퇴")
            .setMessage("${room.userDao().getNickname()}님 정말 탈퇴하시겠습니까?")
            .setPositiveButton("탈퇴하기",
                DialogInterface.OnClickListener { _, _ ->
                    val token = room.userDao().getUid()
                    db.collection("users").document(token)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "회원탈퇴 성공")
                            startActivity(Intent(mainActivity, LoginActivity::class.java))
                        }
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { _, _ ->
                    Log.d(TAG, "회원탈퇴 취소")
                })
        builder.show()
    }

    private fun logout() {
        val user = room.userDao().getUser()
        Log.d(TAG, user.toString())
        room.userDao().delete(user)

        startActivity(Intent(mainActivity, LoginActivity::class.java))
    }

    companion object {
        private const val TAG = "profileFragment"
    }
}