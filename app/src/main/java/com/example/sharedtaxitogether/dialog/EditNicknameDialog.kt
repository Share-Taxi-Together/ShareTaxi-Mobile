package com.example.sharedtaxitogether.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.example.sharedtaxitogether.CheckValid
import com.example.sharedtaxitogether.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditNicknameDialog(context: Context) {
    private val dialog = Dialog(context)
//    private val valid: CheckValid by lazy { CheckValid() }
    private val db: FirebaseFirestore = Firebase.firestore

    fun myDialog() {
        dialog.setContentView(R.layout.dialog_edit_nickname)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val nickname = dialog.findViewById<EditText>(R.id.nickname)
        val btnDone = dialog.findViewById<TextView>(R.id.finish_button)
        val btnCancel = dialog.findViewById<TextView>(R.id.cancel_button)
        val btnDup = dialog.findViewById<TextView>(R.id.checkDup)

        btnDup.setOnClickListener {
            db.collection("users")
                .whereEqualTo("nickname", nickname.text.toString())
                .get()
                .addOnSuccessListener {
                    if(!it.isEmpty){
                        btnDone.isEnabled = false
                        nickname.error = "이미 존재하는 닉네임입니다"
                    } else btnDone.isEnabled = true
                }
        }

        btnDone.setOnClickListener {
            onClickListener.onClicked(nickname.text.toString())
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    interface OnDialogClickListener {
        fun onClicked(nickname: String)
    }

    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }
}