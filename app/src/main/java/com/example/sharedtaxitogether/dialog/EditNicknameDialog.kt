package com.example.sharedtaxitogether.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.example.sharedtaxitogether.R

class EditNicknameDialog(context: Context) {
    private val dialog = Dialog(context)

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

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }
}