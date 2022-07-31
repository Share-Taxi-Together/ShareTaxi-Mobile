package com.example.sharedtaxitogether.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import com.example.sharedtaxitogether.R

class EditCountAddressDialog(context: Context) {
    private val dialog = Dialog(context)

    fun myDialog() {
        dialog.setContentView(R.layout.dialog_edit_countaddress)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val bank = dialog.findViewById<Spinner>(R.id.bankSpinner)
        val address = dialog.findViewById<EditText>(R.id.countAddress)
        val btnDone = dialog.findViewById<TextView>(R.id.finish_button)
        val btnCancel = dialog.findViewById<TextView>(R.id.cancel_button)

        var saveAddress = ""

        bank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                saveAddress = bank.getItemAtPosition(position).toString() + " "
            }
        }
        address.doAfterTextChanged {
            btnDone.isEnabled = address.text.length > 10
        }

        btnDone.setOnClickListener {
            saveAddress += address.text.toString()
            Log.d("save",saveAddress)

            onClickListener.onClicked(saveAddress)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    interface OnDialogClickListener {
        fun onClicked(countAddress: String)
    }

    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    private fun isCountAddressValid(address: String): Boolean {
        return address.matches("^[0-9]+$".toRegex())
    }
}