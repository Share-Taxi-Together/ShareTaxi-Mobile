package com.example.sharedtaxitogether

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.sharedtaxitogether.auth.GMailSender
import com.example.sharedtaxitogether.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    var code = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 이메일 유효성 체크
        binding.editEmail.doOnTextChanged { text, start, before, count ->
            val email = binding.editEmail.text.toString()

            if(!isEmailValid(email)){
                binding.editEmail.error = "이메일을 정확히 입력해주세요"
            }
        }

        //메일 발송
        binding.textEmailCheck.setOnClickListener {
            val email = binding.editEmail.text.toString()

            //이메일 인증코드 보내기
            if (isEmailValid(email)){
                val mailSender = GMailSender()
                code = mailSender.code
                mailSender.sendEmail(email)
                Toast.makeText(applicationContext, "이메일을 확인해주세요", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(applicationContext, "이메일이 유효하지 않습니다", Toast.LENGTH_SHORT).show()
        }

        //메일인증코드 확인
        binding.textEmailCodeCheck.setOnClickListener {
            if(code == binding.editEmailCode.text.toString()){
                binding.layoutStep1.visibility = View.GONE
                binding.layoutStep3.visibility = View.VISIBLE
            }
            else Toast.makeText(applicationContext, "코드가 맞지 않습니다", Toast.LENGTH_SHORT).show()
        }

        //todo 핸드폰 인증(Firebase 사용)

        //todo 닉네임 중복확인 -> DB를 먼저 해야될듯

        //비밀번호 6자리 이상
        var passwordCheck = false
        binding.editPassword.doOnTextChanged { _, _, _, _  ->
            val password = binding.editPassword.text.toString()
            if(!isPasswordValid(password)) binding.editPassword.error = "비밀번호는 6자리이상입니다"
        }

        //비밀번호 확인
        binding.editPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            val password = binding.editPassword.text.toString()
            val passwordConfirm = binding.editPasswordConfirm.text.toString()

            // 비밀번호 정확히 입력했는지 확인
            if(password == passwordConfirm) passwordCheck = true
            else binding.editPasswordConfirm.error = "비밀번호가 다릅니다"
        }

        //todo 데이터 베이스에 정보 추가

    }

    //이메일 형식 체크
    private fun isEmailValid(email: String): Boolean {
        if(!email.contains("tukorea.ac.kr")) return false
        val pattern =  android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
    //비밀번호 6자리 이상인지 체크
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}