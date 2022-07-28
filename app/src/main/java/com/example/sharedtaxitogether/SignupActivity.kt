package com.example.sharedtaxitogether

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.example.sharedtaxitogether.auth.GMailSender
import com.example.sharedtaxitogether.databinding.ActivitySignupBinding
import com.example.sharedtaxitogether.model.User
import com.example.sharedtaxitogether.viewModel.UserInfoViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: UserInfoViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var code = "-1"                 //이메일 인증코드
    private var phoneAuthNum = ""           //sms 인증코드
    private lateinit var phoneCredential: PhoneAuthCredential
    private var storedVerificationId = ""   //인증완료시 부여되는 Id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        binding.viewModel = viewModel

        viewModel.existEmail.observe(this) {
            if (it) {
                binding.editEmail.error = "이미 같은 메일이 존재합니다"
                binding.btnSendMail.isEnabled = false
//                binding.btnSendMail.setTextColor(
//                    ContextCompat.getColor(this, R.color.colorGray8c8c))
            } else {
//                binding.btnSendMail.setTextColor(
//                    ContextCompat.getColor(this, R.color.colorGreen)
//                )
                binding.btnSendMail.isEnabled = true
            }
        }

        viewModel.existPhone.observe(this) {
            if (it) {
                binding.editPhone.error = "이미 같은 번호가 존재합니다"
                binding.btnSendMessage.isEnabled = false
            } else {
                binding.btnSendMessage.isEnabled = true
            }
        }

        auth = Firebase.auth
        db = Firebase.firestore

        bind()
    }

    private fun bind() {
        binding.btnBack.setOnClickListener {
            cancelSignup()
        }
        binding.editEmail.doAfterTextChanged {
            existEmail()
        }
        binding.btnSendMail.setOnClickListener {
            sendMail()
            binding.btnEmailCheck.isEnabled = true
        }
        binding.btnEmailCheck.setOnClickListener {
            emailCodeCheck(code)
        }
        binding.editPassword.doOnTextChanged { _, _, _, _ ->
            checkPassword()
        }
        binding.editPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            checkPasswordConfirm()
        }
        binding.btnNextStep.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        viewModel.email.value = email
                        viewModel.password.value = password
                        binding.layoutStep2.visibility = View.VISIBLE
                        binding.layoutStep1.visibility = View.GONE
                    } else {
                        Log.d(TAG + "2", task.exception.toString())
                    }
                }
        }
        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            getGender()
            if (checkedId != -1) binding.editNickname.isEnabled = true
        }
        binding.editNickname.doOnTextChanged { _, _, _, _ ->
            duplicateNicknameCheck()
        }
        binding.editPhone.doAfterTextChanged {
            existPhone()
        }
//        binding.editPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }
        binding.btnPhoneCodeCheck.setOnClickListener {
            messageCodeCheck()
        }

        binding.btnSignup.setOnClickListener {
            linkCredential(phoneCredential)
            saveUserDB()
        }
    }

    private fun cancelSignup() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("회원가입을 취소하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                Log.d(TAG, "회원가입취소 - 네")
                auth.currentUser?.delete()
                finish()
            }
            .setNegativeButton("아니요") { _, _ ->
                Log.d(TAG, "회원가입취소 - 아니요")
            }
        builder.show()
    }
    private fun existEmail() {
        val email = binding.editEmail.text.toString()
        viewModel.email.value = email

        if (isEmailValid(email) && email.length > 15) {
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener {
                    viewModel.existEmail.value = !it.isEmpty
                }
        } else binding.editEmail.error = "이메일형식이 맞지 않습니다"
    }
    private fun sendMail() {
        val email = viewModel.email.value!!

        val mailSender = GMailSender()
        code = mailSender.code  //이메일 인증코드 저장
        mailSender.sendEmail(email)
        Toast.makeText(this, "이메일을 확인하여 인증을 완료해주세요", Toast.LENGTH_SHORT).show()

    }
    private fun emailCodeCheck(code: String) {
        if (code == binding.editEmailCode.text.toString()) {
            Toast.makeText(applicationContext, "인증되었습니다", Toast.LENGTH_SHORT).show()
            binding.editPassword.isEnabled = true
        } else Toast.makeText(applicationContext, "코드가 맞지 않습니다", Toast.LENGTH_SHORT).show()
    }
    private fun checkPassword() {
        val password = binding.editPassword.text.toString()
        viewModel.password.value = password

        if (!isPasswordValid(password)) {
            binding.editPassword.error = "숫자, 문자, 특수문자 중 2가지 포함(6~15자)"
            binding.editPasswordConfirm.isEnabled = false
        } else binding.editPasswordConfirm.isEnabled = true
    }
    private fun checkPasswordConfirm() {
        val password = viewModel.password.value
        val passwordConfirm = binding.editPasswordConfirm.text.toString()

        if (password != passwordConfirm) {
            binding.editPasswordConfirm.error = "비밀번호가 다릅니다"
            binding.btnNextStep.isEnabled = false
        } else binding.btnNextStep.isEnabled = true
    }
    private fun getGender() {
        if (binding.genderM.isChecked) {
            viewModel.gender.value = "Male"
        } else if (binding.genderF.isChecked) {
            viewModel.gender.value = "Female"
        }
    }
    private fun duplicateNicknameCheck() {
        val nickname = binding.editNickname.text.toString()
        viewModel.nickname.value = nickname

        db.collection("users")
            .whereEqualTo("nickname", nickname)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    binding.editNickname.error = "같은 아이디가 존재합니다"
                    binding.editPhone.isEnabled = false
                } else {
                    binding.editPhone.isEnabled = true
                }
            }
    }
    private fun existPhone() {
        if (binding.editPhone.text.toString().length == 11) {
            val phone = "+82" + binding.editPhone.text.toString().slice(1..10)
            viewModel.phone.value = phone

            db.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener {
                    viewModel.existPhone.value = !it.isEmpty
                }
        }
    }

//    private fun phoneNumForm(phone: String) =
//        "+82" + phone.replace("-", "").slice(1..10)

    private fun sendMessage() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                phoneAuthNum = credential.smsCode.toString()
                phoneCredential = credential
//                linkCredential(credential)
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
            .setPhoneNumber(viewModel.phone.value!!)   // +821012345678
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        auth.setLanguageCode("kr")
        Toast.makeText(applicationContext, "문자를 확인하여 인증을 완료해주세요", Toast.LENGTH_SHORT).show()
        binding.btnPhoneCodeCheck.isEnabled = true
    }
//    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
//        if (binding.editPhoneCode.text.toString() == phoneAuthNum) {
//            auth.signInWithCredential(phoneAuthCredential)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        binding.layoutStep2.visibility = View.GONE
//                    }
//                }
//        } else {
//            Toast.makeText(this, "인증코드가 틀렸습니다", Toast.LENGTH_SHORT).show()
//        }
//    }
    private fun messageCodeCheck() {
        if(binding.editPhoneCode.text.toString() == phoneAuthNum){
            binding.btnSignup.isEnabled = true
        }else {
            Toast.makeText(this, "인증코드가 틀렸습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun linkCredential(credential: PhoneAuthCredential) {
        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.layoutStep2.visibility = View.GONE
                    val user = task.result?.user
                    Log.d(TAG + "4", user.toString())
                } else {
                    Log.d(TAG + "3", task.exception.toString())
                }
            }
    }
    private fun saveUserDB() {
        if (checkValidValue()) {
            viewModel.uid.value = auth.uid

            val user =
                User(
                    viewModel.uid.value!!,
                    viewModel.email.value!!,
                    viewModel.phone.value!!,
                    viewModel.gender.value!!,
                    viewModel.nickname.value!!,
                    viewModel.password.value!!,
                    viewModel.score.value!!,
                    viewModel.imgUrl.value!!,
                    viewModel.countAddress.value!!
                )

            db.collection("users").document(user.uid).set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "회원가입을 실패했습니다", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Error adding document", e)
                }
        } else {
            Toast.makeText(this, "입력정보 중 뭔가 틀렸습니다ㅋ", Toast.LENGTH_SHORT).show()
        }
    }

    // TODO db 삽입 전 각 value 별 유효성 검사
    private fun checkValidValue(): Boolean {
        return true
    }

    // TODO 유효성 검사 static으로 빼기
    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@tukorea.ac.kr")) return false
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{6,15}\$".toRegex())
    }

    companion object {
        private const val TAG = "SignupActivity/"
    }
}