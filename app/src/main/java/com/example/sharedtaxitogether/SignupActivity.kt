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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var code = "-1"                 //이메일 인증코드
    private var phoneAuthNum = ""           //sms 인증코드
    private var storedVerificationId = ""   //얘는 머지??

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        authenticateEmail()
        authenticatePhone()
        val gender = saveGender()
        duplicateNicknameCheck()
        checkPassword()

        if (checkValidValue()) {
            binding.btnSignup.isEnabled = true
        }

        // 데이터 베이스에 정보 추가
        binding.btnSignup.setOnClickListener {
            val uid = auth.uid
            val email = valueString("email")
            val phone = valueString("phone")
            val nickname = valueString("nickname")
            val password = valueString("password")

            val user =
                com.example.sharedtaxitogether.model.User(
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
        }
    }

    private fun valueString(value: String): String {
        val valueStr = ""
        when (value) {
            "email" -> binding.editEmail.text.toString()
            "emailCode" -> binding.editEmailCode.text.toString()
            "phone" -> binding.editPhone.text.toString()
            "phoneCode" -> binding.editPhoneCode.text.toString()
            "nickname" -> binding.editNickname.text.toString()
            "password" -> binding.editPassword.text.toString()
            "passwordConfirm" -> binding.editPasswordConfirm.text.toString()
        }
        return valueStr
    }

    // 인증 메일 발송
    private fun authenticateEmail() {
        binding.textEmailCheck.setOnClickListener {
            val email = valueString("email")

            if (isEmailValid(email)) {
                val mailSender = GMailSender()
                code = mailSender.code  //이메일 인증코드 저장
                mailSender.sendEmail(email)
                Toast.makeText(this, "이메일을 확인하여 인증을 완료해주세요", Toast.LENGTH_SHORT).show()
            } else {
                binding.editEmail.error = "이메일을 정확히 입력해주세요"
            }
            emailCodeCheck(code)
        }
    }

    //메일인증코드 확인
    private fun emailCodeCheck(code: String) {
        binding.btnEmailCodeCheck.setOnClickListener {
            if (code == valueString("emailCode")) {
                binding.layoutStep1.visibility = View.GONE
                binding.layoutStep2.visibility = View.VISIBLE
            } else Toast.makeText(applicationContext, "코드가 맞지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 핸드폰 번호
    private fun authenticatePhone() {
        binding.textPhoneCheck.setOnClickListener {
            val phoneNum = valueString("phone")
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
        }

        binding.btnPhoneCodeCheck.setOnClickListener {
            val phoneCredential =
                PhoneAuthProvider.getCredential(storedVerificationId, phoneAuthNum)
            signInWithPhoneAuthCredential(phoneCredential)
        }
    }

    // 번호인증코드 확인
    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        if (valueString("phoneCode") == phoneAuthNum) {
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

    private fun saveGender(): String {
        var gender = ""
        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.genderM -> gender = "Male"
                R.id.genderF -> gender = "Female"
            }
            binding.editNickname.isEnabled = true
        }
        return gender
    }

    private fun duplicateNicknameCheck() {
        binding.editNickname.doOnTextChanged { _, _, _, _ ->
            db.collection("users")
                .whereEqualTo("nickname", valueString("nickname"))
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        binding.editNickname.error = "같은 아이디가 존재합니다"
                    }else{
                        binding.editPassword.isEnabled = true
                    }
                }
        }
    }

    private fun checkPassword() {
        binding.editPassword.doOnTextChanged { _, _, _, _ ->
            val password = valueString("password")
            if (!isPasswordValid(password)) binding.editPassword.error = "숫자, 문자, 특수문자 중 2가지 포함(6~15자)"
            else binding.editPasswordConfirm.isEnabled = true
        }

        binding.editPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            val password = valueString("password")
            val passwordConfirm = valueString("passwordConfirm")

            if (password != passwordConfirm) binding.editPasswordConfirm.error = "비밀번호가 다릅니다"
        }
    }

    // TODO db 삽입 전 각 value 별 유효성 검사
    private fun checkValidValue(): Boolean {

        return true
    }

    //이메일 형식 체크
    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@tukorea.ac.kr")) return false
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    // 숫자, 문자, 특수문자 중 2가지 포함(6~15자)
    private fun isPasswordValid(password: String): Boolean {
        return password.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{6,15}\$".toRegex())
    }
}