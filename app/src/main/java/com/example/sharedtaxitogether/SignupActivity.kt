package com.example.sharedtaxitogether

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.sharedtaxitogether.auth.GMailSender
import com.example.sharedtaxitogether.databinding.ActivitySignupBinding
import com.example.sharedtaxitogether.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var code = "-1"                 //이메일 인증코드
    private var phoneAuthNum = ""           //sms 인증코드
    private var storedVerificationId = ""   //인증완료시 부여되는 Id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        bind()
    }

    private fun bind() {
        binding.btnBack.setOnClickListener {
            Toast.makeText(this, "회원가입을 취소하였습니다", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.textEmailCheck.setOnClickListener {
            sendMail()
        }
        binding.btnEmailCodeCheck.setOnClickListener {
            emailCodeCheck(code)
        }
        binding.textPhoneCheck.setOnClickListener {
            sendMessage()
        }
        binding.btnPhoneCodeCheck.setOnClickListener {
            messageCodeCheck()
        }
        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) binding.editNickname.isEnabled = true
        }
        binding.editNickname.doOnTextChanged { _, _, _, _ ->
            duplicateNicknameCheck()
        }
        binding.editPassword.doOnTextChanged { _, _, _, _ ->
            checkPassword()
        }
        binding.editPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            checkPasswordConfirm()
        }
        binding.btnSignup.setOnClickListener {
            saveUserDB()
        }
    }

    private fun sendMail() {
        val email = binding.editEmail.text.toString()
//        if (isEmailValid(email) && !checkAlreadyExist("email", email)) {
        if (isEmailValid(email)) {
            val mailSender = GMailSender()
            code = mailSender.code  //이메일 인증코드 저장
            mailSender.sendEmail(email)
            Toast.makeText(this, "이메일을 확인하여 인증을 완료해주세요", Toast.LENGTH_SHORT).show()
            binding.btnEmailCodeCheck.isEnabled = true
        } else {
            binding.editEmail.error = "이메일을 정확히 입력해주세요"
        }
    }

    private fun emailCodeCheck(code: String) {
        if (code == binding.editEmailCode.text.toString()) {
            binding.layoutStep1.visibility = View.GONE
            binding.layoutStep2.visibility = View.VISIBLE
        } else Toast.makeText(applicationContext, "코드가 맞지 않습니다", Toast.LENGTH_SHORT).show()
    }

    //TODO 핸드폰번호 유효성 검사 후 메시지 보내도록 수정
    private fun sendMessage() {
        val phoneNum = binding.editPhone.text.toString()
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                phoneAuthNum = credential.smsCode.toString()
            }

            override fun onVerificationFailed(e: FirebaseException) {}
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNum)   // +821012345678
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        auth.setLanguageCode("kr")
        Toast.makeText(applicationContext, "문자를 확인하여 인증을 완료해주세요", Toast.LENGTH_SHORT).show()
        binding.btnPhoneCodeCheck.isEnabled = true
    }

    private fun messageCodeCheck() {
        val phoneCredential =
            PhoneAuthProvider.getCredential(storedVerificationId, phoneAuthNum)
        signInWithPhoneAuthCredential(phoneCredential)
    }

    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        if (binding.editPhoneCode.text.toString() == phoneAuthNum) {
            auth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        binding.layoutStep2.visibility = View.GONE
                        binding.layoutStep3.visibility = View.VISIBLE
                    }
                }
        } else {
            Toast.makeText(this, "인증코드가 틀렸습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun duplicateNicknameCheck() {
        val nickname = binding.editNickname.text.toString()

//        if (checkAlreadyExist("nickname", nickname)) {
//            binding.editNickname.error = "같은 아이디가 존재합니다"
//            binding.editPassword.isEnabled = false
//        } else {
//            binding.editPassword.isEnabled = true
//        }
        db.collection("users")
            .whereEqualTo("nickname", nickname)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    binding.editNickname.error = "같은 아이디가 존재합니다"
                    binding.editPassword.isEnabled = false
                } else {
                    binding.editPassword.isEnabled = true
                }
            }
    }

    private fun checkPassword() {
        val password = binding.editPassword.text.toString()
        if (!isPasswordValid(password)) {
            binding.editPassword.error = "숫자, 문자, 특수문자 중 2가지 포함(6~15자)"
            binding.editPasswordConfirm.isEnabled = false
        } else binding.editPasswordConfirm.isEnabled = true
    }

    private fun checkPasswordConfirm() {
        val password = binding.editPassword.text.toString()
        val passwordConfirm = binding.editPasswordConfirm.text.toString()

        if (password != passwordConfirm) {
            binding.editPasswordConfirm.error = "비밀번호가 다릅니다"
            binding.btnSignup.isEnabled = false
        } else binding.btnSignup.isEnabled = true
    }

    private fun getGender(): String {
        var genderText = ""

        if (binding.genderM.isChecked) {
            genderText = "Male"
        } else if (binding.genderF.isChecked) {
            genderText = "Female"
        }
        Log.d("this", "genderText : $genderText")
        return genderText
    }

    private fun saveUserDB() {
        if (checkValidValue()) {
            val uid = auth.uid
            val email = binding.editEmail.text.toString()
            val phone = binding.editPhone.text.toString()
            val gender = getGender()
            val nickname = binding.editNickname.text.toString()
            val password = binding.editPassword.text.toString()

            val user =
                User(
                    uid,
                    email,
                    phone,
                    gender,
                    nickname,
                    password
                )

            db.collection("users").document(uid!!).set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "회원가입을 실패했습니다", Toast.LENGTH_SHORT).show()
                    Log.w("hh", "Error adding document", e)
                }
        } else {
            Toast.makeText(this, "입력정보 중 뭔가 틀렸습니다ㅋ", Toast.LENGTH_SHORT).show()
        }
    }

    //TODO 라이브 데이터 적용 후에 다시 하자 callback 함수는 리턴을 못해,,
    private fun checkAlreadyExist(fieldStr: String, email: String) {
//        return  db.collection("users")
//            .whereEqualTo(fieldStr, email)
//            .get()
//            .addOnSuccessListener {
//                 return it.isEmpty
//                Log.d("this,", "${it.documents}")
////                exist = !it.isEmpty
//            }
    }

    // TODO db 삽입 전 각 value 별 유효성 검사
    private fun checkValidValue(): Boolean {

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@tukorea.ac.kr")) return false
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{6,15}\$".toRegex())
    }
}