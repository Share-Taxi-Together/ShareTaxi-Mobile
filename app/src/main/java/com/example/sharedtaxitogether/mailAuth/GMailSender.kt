package com.example.sharedtaxitogether.mailAuth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GMailSender: Authenticator() {

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(fromEmail, password)
    }

    fun sendEmail(toEmail: String){
        CoroutineScope(Dispatchers.IO).launch {
            val props = Properties()
            props.setProperty("mail.transport.protocol", "smtp")
            props.setProperty("mail.host", "smtp.gmail.com")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.socketFactory.fallback", "false")
            props.setProperty("mail.smtp.quitwait", "false")

            val session = Session.getDefaultInstance(props, this@GMailSender)

            val message = MimeMessage(session)
            message.sender = InternetAddress(fromEmail)
            message.addRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
            message.subject = "이메일 인증"
            message.setText("인증번호는 1234입니다.")

            Transport.send(message)
        }
    }
    //todo 인증코드 생성
}