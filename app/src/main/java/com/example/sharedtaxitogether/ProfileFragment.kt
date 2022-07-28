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
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.sharedtaxitogether.databinding.FragmentProfileBinding
import com.example.sharedtaxitogether.dialog.EditCountAddressDialog
import com.example.sharedtaxitogether.dialog.EditNicknameDialog
import com.example.sharedtaxitogether.dialog.EditPasswordDialog
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore

        bind()
        initView()

        return binding.root
    }

    private fun initView() {
        when (viewModel.gender.value) {
            "Male" -> binding.genderImgView.setImageResource(R.drawable.male)
            "Female" -> binding.genderImgView.setImageResource(R.drawable.female)
        }
        if (viewModel.imgUrl.value!!.isBlank())
            binding.profileImgView.setImageResource(R.drawable.default_profile)
        else {
            Glide.with(mainActivity)
                .load(viewModel.imgUrl.value)
                .into(binding.profileImgView)
        }
        binding.nicknameTextView.text = viewModel.nickname.value
        binding.scoreTextView.text = viewModel.score.value
        binding.emailTextView.text = auth.currentUser!!.email
        var pw = ""
        for (i in 1..viewModel.password.value!!.length) {
            pw += "*"
        }
        binding.passwdTextView.text = pw
        binding.phoneTextView.text = auth.currentUser!!.phoneNumber
        binding.countAddressTextView.text = viewModel.countAddress.value
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
            val passwordDialog = EditPasswordDialog(mainActivity, viewModel.password.value!!)
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
                uploadImageToFirebase(data?.data!!, viewModel.uid.value!!)
            }
        }
    }

    private fun modifyInfo(field: String, value: String) {
        db.collection("users").document(viewModel.uid.value!!)
            .update(field, value)

        when(field){
            "imgUrl" -> viewModel.imgUrl.value = value
            "nickname" -> viewModel.nickname.value = value
            "password" -> viewModel.password.value = value
            "countAddress" -> viewModel.countAddress.value = value
        }
    }

    private fun withdraw() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("회원탈퇴")
            .setMessage("${viewModel.nickname.value}님 정말 탈퇴하시겠습니까?")
            .setPositiveButton("탈퇴하기") { _, _ ->
                db.collection("users").document(viewModel.uid.value!!)
                    .delete()
                    .addOnSuccessListener {
                        startActivity(Intent(mainActivity, LoginActivity::class.java))
                        activity?.finish()
                    }
                auth.currentUser!!.delete()
            }
            .setNegativeButton("취소") { _, _ -> }
        builder.show()
    }

    private fun logout() {
        auth.signOut()

        startActivity(Intent(mainActivity, LoginActivity::class.java))
        activity?.finish()
    }

    companion object {
        private const val TAG = "profileFrag/"
        private const val IMAGE_PICK_CODE = 1000
    }
}