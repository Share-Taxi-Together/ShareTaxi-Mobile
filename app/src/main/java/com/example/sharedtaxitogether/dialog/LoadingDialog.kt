package com.example.sharedtaxitogether.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.sharedtaxitogether.R

class LoadingDialog(context: Context): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}