package com.example.sharedtaxitogether.mailAuth

import com.example.sharedtaxitogether.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GMailSender: Authenticator() {
    // 발신 메일과 비밀번호
    val fromEmail = BuildConfig.GMAIL_ADDRESS
    val password = BuildConfig.GMAIL_PASSWD

    var code = "-1"

    init{
        // 이메일 인증 코드 생성
        code = createEmailCode()
    }

    // 발신자 계정 확인
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(fromEmail, password)
    }

    // 메일 보내기
    fun sendEmail(toEmail: String){
        CoroutineScope(Dispatchers.IO).launch {
            val props = Properties()
            props.setProperty("mail.transport.protocol", "smtp")
            props.setProperty("mail.host", "smtp.gmail.com")
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.socketFactory.fallback"] = "false"
            props.setProperty("mail.smtp.quitwait", "false")

            // Google에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달
            val session = Session.getDefaultInstance(props, this@GMailSender)

            // message 객체 만들기
            val message = MimeMessage(session)
            message.sender = InternetAddress(fromEmail)                              // 보내는 사람 설정
            message.addRecipient(Message.RecipientType.TO, InternetAddress(toEmail)) // 받는 사람 설정
            message.subject = "[쉐어택시] 이메일 인증번호 안내"                                     // 이메일 제목
            message.setText("안녕하세요? 쉐어택시입니다.\n\n $code \n\n 위 코드를 입력해 이메일 인증을 완료해주세요.")                        // 이메일 내용

            // 메일 전송
            Transport.send(message)
        }
    }

    // 메일 인증 코드 생성
    private fun createEmailCode(): String{
        val str = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        var code = ""
        for(i in 1..6)
            code += str[(Math.random() * str.size).toInt()]
        return code
    }
}