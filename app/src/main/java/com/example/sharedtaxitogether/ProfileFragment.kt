package com.example.sharedtaxitogether

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.sharedtaxitogether.databinding.FragmentProfileBinding
import com.example.sharedtaxitogether.dialog.EditCountAddressDialog
import com.example.sharedtaxitogether.dialog.EditNicknameDialog
import com.example.sharedtaxitogether.dialog.EditPasswordDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var room: AppDatabase
    private val pref: UserSharedPreferences by lazy { UserSharedPreferences(mainActivity) }

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

        initView()

        return binding.root
    }

    private fun initView() {
        Thread {
            activity?.runOnUiThread {
                when (room.userDao().getGender()) {
                    "Male" -> binding.genderImgView.setImageResource(R.drawable.male)
                    "Female" -> binding.genderImgView.setImageResource(R.drawable.female)
                }
                if (room.userDao().getImgUrl().isBlank())
                    binding.profileImgView.setImageResource(R.drawable.default_profile)
                else {
                    Glide.with(mainActivity)
                        .load(room.userDao().getImgUrl())
                        .into(binding.profileImgView)
                }
                binding.nicknameTextView.text = room.userDao().getNickname()
                binding.scoreTextView.text = room.userDao().getScore()
                binding.emailTextView.text = room.userDao().getEmail()
                var pw = ""
                for (i in 1..room.userDao().getPassword().length) {
                    pw += "*"
                }
                binding.passwdTextView.text = pw
                binding.phoneTextView.text = room.userDao().getPhone()
                binding.countAddressTextView.text = room.userDao().getCountAddress()
            }
        }.start()
    }

    private fun bind() {
        binding.editNickname.setOnClickListener {
//            showDialog("nickname", binding.nicknameTextView)
            val dialog = EditNicknameDialog(mainActivity)
            dialog.myDialog()

            dialog.setOnClickListener(object : EditNicknameDialog.OnDialogClickListener {
                override fun onClicked(nickname: String) {
                    binding.nicknameTextView.text = nickname
                    modifyInfo("nickname", nickname)
                }
            })
        }
        //TODO 이메일 수정
//        binding.editEmail.setOnClickListener {
//            // 중복확인, 유효성검사, db수정, ui 수정, 메일인증
//            showDialog("email",binding.emailTextView)
//        }
        binding.editPassword.setOnClickListener {
            val passwordDialog = EditPasswordDialog(mainActivity, pref.getStringValue("password"))
            passwordDialog.myDialog()

            passwordDialog.setOnClickListener(object : EditPasswordDialog.OnDialogClickListener {
                override fun onClicked(password: String) {
                    var pw = ""
                    for (i in 1..password.length) {
                        pw += "*"
                    }
                    binding.passwdTextView.text = pw
                    modifyInfo("password", password)
                }
            })
        }
        //TODO 전화번호 수정
//        binding.editPhone.setOnClickListener {
//        }
        binding.editAccountAddress.setOnClickListener {
            val dialog = EditCountAddressDialog(mainActivity)
            dialog.myDialog()

            dialog.setOnClickListener(object : EditCountAddressDialog.OnDialogClickListener {
                override fun onClicked(countAddress: String) {
                    binding.countAddressTextView.text = countAddress
                    modifyInfo("countAddress", countAddress)
                }
            })
        }

        binding.logoutTextView.setOnClickListener {
            logout()
        }
        binding.withdrawTextView.setOnClickListener {
            withdraw()
        }
        binding.profileImgView.setOnClickListener {
            pickImageFromGallery()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ContextCompat.checkSelfPermission(
//                        mainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE
//                    )
//                    == PackageManager.PERMISSION_DENIED
//                ) {
//                    //권한 없으면 권한 받기
//                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                    mainActivity.requestPermissions(permissions, PERMISSION_CODE)
//                } else {
//                    pickImageFromGallery()
//                }
//            } else {
//                pickImageFromGallery()
//            }
        }
    }

//    private fun showDialog(target: String, textView: TextView) {
//        val dialog = EditDialog(mainActivity, target)
//        dialog.myDialog()
//
//        dialog.setOnClickListener(object : EditDialog.OnDialogClickListener {
//            override fun onClicked(value: String) {
//                textView.text = value
//                modifyInfo(target, value)
//            }
//        })
//    }

    // Open Gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun uploadImageToFirebase(uri: Uri, userId: String) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val fileName = "IMAGE_${userId}_.png"

        val imgRef = storage.reference.child("images/profile/").child(fileName)

        imgRef.putFile(uri).continueWithTask {
            return@continueWithTask imgRef.downloadUrl
        }.addOnSuccessListener {
            modifyInfo("imgUrl", it.toString())
        }.addOnFailureListener {
            Log.d(TAG + "fail", it.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.profileImgView.setImageURI(data?.data)
            Log.d(TAG + "profileData", data?.data.toString())

            if (ContextCompat.checkSelfPermission(
                    mainActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                // storage에 이미지 업로드
                uploadImageToFirebase(data?.data!!, room.userDao().getUid())
            }
        }
    }

    private fun modifyInfo(field: String, value: String) {
        db.collection("users").document(room.userDao().getUid())
            .update(field, value)
        pref.putValue(field, value)
    }

    private fun withdraw() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("회원탈퇴")
            .setMessage("${room.userDao().getNickname()}님 정말 탈퇴하시겠습니까?")
            .setPositiveButton("탈퇴하기") { _, _ ->
                val token = room.userDao().getUid()
                db.collection("users").document(token)
                    .delete()
                    .addOnSuccessListener {
                        startActivity(Intent(mainActivity, LoginActivity::class.java))
                        activity?.finish()
                    }
            }
            .setNegativeButton("취소") { _, _ -> }
        builder.show()
        pref.editor.clear().commit()
    }

    private fun logout() {
        val user = room.userDao().getUser()
        room.userDao().delete(user)

        pref.editor.clear().commit()

        startActivity(Intent(mainActivity, LoginActivity::class.java))
        activity?.finish()
    }

    companion object {
        private const val TAG = "profileFrag/"
        private const val IMAGE_PICK_CODE = 1000
    }
}