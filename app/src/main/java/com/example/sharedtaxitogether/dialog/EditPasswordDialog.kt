package com.example.sharedtaxitogether.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.sharedtaxitogether.R

class EditPasswordDialog(context: Context, preValue: String) {
    private val dialog = Dialog(context)
    private val preValue = preValue

    fun myDialog() {
        dialog.setContentView(R.layout.dialog_edit_password)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val password = dialog.findViewById<EditText>(R.id.password)
        val newPassword = dialog.findViewById<EditText>(R.id.newPassword)
        val newPasswordConfirm = dialog.findViewById<EditText>(R.id.newPasswordConfirm)

        val btnDone = dialog.findViewById<TextView>(R.id.finish_button)
        val btnCancel = dialog.findViewById<TextView>(R.id.cancel_button)

        password.doAfterTextChanged {
            if(preValue == password.text.toString()){
                newPassword.isEnabled = true
            } else {
                password.error = "이전 비밀번호가 틀렸습니다"
                newPassword.isEnabled = false
            }
        }

        newPassword.doOnTextChanged { _, _, _, _ ->
            val pw = newPassword.text.toString()
            if(pw == password.text.toString()){
                newPassword.error = "이전 비밀번호와 같습니다"
            }

            if(!isPasswordValid(pw)){
                newPassword.error = "숫자, 문자, 특수문자 중 2가지 포함(6~15자)"
                newPasswordConfirm.isEnabled = false
            } else newPasswordConfirm.isEnabled = true
        }

        newPasswordConfirm.doAfterTextChanged {
            if(newPassword.text.toString() != newPasswordConfirm.text.toString()){
                newPasswordConfirm.error = "비밀번호가 다릅니다"
                btnDone.isEnabled = false
            } else btnDone.isEnabled = true
        }

        btnDone.setOnClickListener {
            onClickListener.onClicked(newPassword.text.toString())
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{6,15}\$".toRegex())
    }

    interface OnDialogClickListener {
        fun onClicked(password: String)
    }

    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }
}