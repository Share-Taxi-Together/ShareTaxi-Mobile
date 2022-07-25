//package com.example.sharedtaxitogether.dialog
//
//import android.app.Dialog
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.WindowManager
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.TextView
//import com.example.sharedtaxitogether.R
//import com.example.sharedtaxitogether.databinding.DialogEditCountaddressBinding
//import com.example.sharedtaxitogether.databinding.DialogEditNicknameBinding
//import com.example.sharedtaxitogether.databinding.DialogEditPasswordBinding
//import com.example.sharedtaxitogether.databinding.FragmentProfileBinding
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//class EditDialog(context: Context, target: String) {
//    private lateinit var cancelButton: TextView
//    private lateinit var finishButton: TextView
//    //닉네임
//    private lateinit var checkDup: TextView
//    //계좌번호
//    private lateinit var bank: Spinner
//    private lateinit var address: EditText
//    //비밀번호
//
//    private val context = context
//    private val dialog = Dialog(context)
//    private val db: FirebaseFirestore = Firebase.firestore
//    private val target = target
//
//    private lateinit var bindingNickname: DialogEditNicknameBinding
//    private lateinit var bindingPassword: DialogEditPasswordBinding
//    private lateinit var bindingCountAddress: DialogEditCountaddressBinding
//
//    private var layout = 0
//
//    private fun initValue(){
//        when(target){
//            "nickname" -> {
//                layout = R.layout.dialog_edit_nickname
//                bindingNickname = DialogEditNicknameBinding.inflate(LayoutInflater.from(context))
//
//                cancelButton = bindingNickname.cancelButton
//                finishButton = bindingNickname.finishButton
//                checkDup = bindingNickname.checkDup
//            }
//            "password" -> {
//                layout = R.layout.dialog_edit_password
//                bindingPassword = DialogEditPasswordBinding.inflate(LayoutInflater.from(context))
//
//                cancelButton = bindingPassword.cancelButton
//                finishButton = bindingPassword.finishButton
//
//            }
//            "countAddress" -> {
//                layout = R.layout.dialog_edit_countaddress
//                bindingCountAddress = DialogEditCountaddressBinding.inflate(LayoutInflater.from(context))
//
//                cancelButton = bindingCountAddress.cancelButton
//                finishButton = bindingCountAddress.finishButton
//
//
//            }
//        }
//    }
//
//    fun myDialog() {
//        initValue()
//
//        dialog.setContentView(bindingNickname.root)
//        dialog.window!!.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.WRAP_CONTENT
//        )
//        dialog.setCanceledOnTouchOutside(true)
//        dialog.setCancelable(true)
//        dialog.show()
//
//        val value = when(target){
//            "nickname" -> bindingNickname.nickname
//            "password" -> bindingPassword.password
//            "countAddress" -> bindingCountAddress.countAddress
//            else -> bindingNickname.nickname
//        }
//
//        var saveAddress = ""
//
//
//        checkDup.setOnClickListener {
//            db.collection("users")
//                .whereEqualTo(target, value.text.toString())
//                .get()
//                .addOnSuccessListener {
//                    if(!it.isEmpty){
//                        bindingNickname.finishButton.isEnabled = false
//                        value.error = "이미 존재하는 값입니다"
//                    } else bindingNickname.finishButton.isEnabled = true
//                }
//        }
//
//        finishButton.setOnClickListener {
//            onClickListener.onClicked(value.text.toString())
//            dialog.dismiss()
//        }
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
////
////        when(target){
////            "nickname" -> {
////                bindingNickname.checkDup.setOnClickListener {
////                    db.collection("users")
////                        .whereEqualTo(target, value.text.toString())
////                        .get()
////                        .addOnSuccessListener {
////                            if(!it.isEmpty){
////                                bindingNickname.finishButton.isEnabled = false
////                                value.error = "이미 존재하는 값입니다"
////                            } else bindingNickname.finishButton.isEnabled = true
////                        }
////                }
////
////                bindingNickname.finishButton.setOnClickListener {
////                    onClickListener.onClicked(value.text.toString())
////                    dialog.dismiss()
////                }
////                bindingNickname.cancelButton.setOnClickListener {
////                    dialog.dismiss()
////                }
////            }
////            "password" -> {
////
////            }
////            "countAddress" -> {
////
////            }
////        }
//
//
//    }
//
//    interface OnDialogClickListener {
//        fun onClicked(value: String)
//    }
//
//    private lateinit var onClickListener: OnDialogClickListener
//
//    fun setOnClickListener(listener: OnDialogClickListener) {
//        onClickListener = listener
//    }
//
//}