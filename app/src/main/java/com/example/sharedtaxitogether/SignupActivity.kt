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
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import com.example.sharedtaxitogether.model.User as dao

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var phoneAuthNum = ""

    var code = "-1" //이메일 인증코드
    private var storedVerificationId = "" //얘는 머지??

    private lateinit var auth: FirebaseAuth //파이어베이스 인증
    private lateinit var fireStore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        fireStore = Firebase.firestore

//        val verificationId = ""

        //이메일
        initEmail()
        initEmailValidCheck()
        emailCodeCheck()

        //핸드폰 인증(Firebase 사용)
        //인증하기 버튼 클릭리스너
        binding.textPhoneCheck.setOnClickListener {
            val phoneNum = binding.editPhone.text.toString()
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    phoneAuthNum = credential.smsCode.toString()
                }

                override fun onVerificationFailed(e: FirebaseException) {
////                    Log.w(TAG, "onVerificationFailed", e)
//                    if (e is FirebaseAuthInvalidCredentialsException) {
//                        // Invalid request
//                    } else if (e is FirebaseTooManyRequestsException) {
//                        // The SMS quota for the project has been exceeded
//                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d("SignupActivity", "onCodeSent:$verificationId")
                    storedVerificationId =
                        verificationId   //verificationId와 전화번호 인증코드 매칭 -> 인증에 사용할 예정
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
//                .setPhoneNumber("+821012345678")
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            auth.setLanguageCode("kr")
        }

        //확인 클릭 리스너
        binding.textPhoneCodeCheck.setOnClickListener {
            val phoneCredential =
                PhoneAuthProvider.getCredential(storedVerificationId, phoneAuthNum)
            signInWithPhoneAuthCredential(phoneCredential)
        }


        // 닉네임 중복확인 -> DB를 먼저 해야될듯
        binding.editNickname.doOnTextChanged { text, _, _, _ ->
            fireStore?.collection("users")
                .whereEqualTo("nickname", binding.editNickname.text.toString())
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        binding.editNickname.error = "같은 아이디가 존재합니다"
                    }
                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show()
//                    Log.w("hh", "Error adding document", e)
//                }

        }

        var gender: String = ""
        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.genderM -> gender = "M"
                R.id.genderW -> gender = "W"
            }
        }

        //비밀번호 6자리 이상
        var passwordCheck = false
        binding.editPassword.doOnTextChanged { _, _, _, _ ->
            val password = binding.editPassword.text.toString()
            if (!isPasswordValid(password)) binding.editPassword.error = "비밀번호는 6자리이상입니다"
        }

        //비밀번호 확인
        binding.editPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            val password = binding.editPassword.text.toString()
            val passwordConfirm = binding.editPasswordConfirm.text.toString()

            // 비밀번호 정확히 입력했는지 확인
            if (password == passwordConfirm) passwordCheck = true
            else binding.editPasswordConfirm.error = "비밀번호가 다릅니다"
        }


        // 데이터 베이스에 정보 추가
        binding.btnSignup.setOnClickListener {
            val uid = auth.uid
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val nickname = binding.editNickname.text.toString()
//            val gender = "W"


            val user =
                com.example.sharedtaxitogether.model.User(uid, email, nickname, password, gender)

            fireStore?.collection("users").document(uid!!).set(user)
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

    //메일인증코드 확인
    private fun emailCodeCheck() {
        binding.textEmailCodeCheck.setOnClickListener {
            if (code == binding.editEmailCode.text.toString()) {
                binding.layoutStep1.visibility = View.GONE
                binding.layoutStep2.visibility = View.VISIBLE
            } else Toast.makeText(applicationContext, "코드가 맞지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 전화번호 인증
    // onCodeSent()에서 받은 verificationID
    // 문자로 받은 인증코드로 생성한 phoneAuthCredential
    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        if (binding.editPhoneCode.text.toString() == phoneAuthNum) {
            auth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //인증성공
                        binding.layoutStep2.visibility = View.GONE
                        binding.layoutStep3.visibility = View.VISIBLE
                    }
                }
        } else {
            //인증실패
            Toast.makeText(this, "인증코드가 틀렸습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 이메일 유효성 체크
    private fun initEmail() {
        binding.editEmail.doOnTextChanged { text, start, before, count ->
            val email = binding.editEmail.text.toString()

            if (!isEmailValid(email)) {
                binding.editEmail.error = "이메일을 정확히 입력해주세요"
            }
        }
    }

    //인증하기 누르면 코드가 담긴 메일 발송
    private fun initEmailValidCheck() {
        binding.textEmailCheck.setOnClickListener {
            val email = binding.editEmail.text.toString()

            //이메일 인증코드 보내기
            if (isEmailValid(email)) {
                val mailSender = GMailSender()
                code = mailSender.code
                mailSender.sendEmail(email)
                Toast.makeText(applicationContext, "이메일을 확인해주세요", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(applicationContext, "이메일이 유효하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    //이메일 형식 체크
    private fun isEmailValid(email: String): Boolean {
//        if (!email.contains("tukorea.ac.kr")) return false
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    //비밀번호 6자리 이상인지 체크
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}