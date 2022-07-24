package com.example.sharedtaxitogether

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.ActivityModifyinfoBinding
import com.example.sharedtaxitogether.databinding.ActivitySignupBinding

class ModifyInfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityModifyinfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bind()
    }

    private fun bind(){
        binding.btnBack.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정보수정을 취소하시겠습니까?")
                .setPositiveButton("네",
                    DialogInterface.OnClickListener { _, _ ->
                        Log.d(TAG, "정보수정취소 - 네")
                        finish()
                    })
                .setNegativeButton("아니요",
                    DialogInterface.OnClickListener { _, _ ->
                        Log.d(TAG, "정보수정취소 - 아니요")
                    })
            builder.show()
        }
    }
    companion object{
        private const val TAG = "ModifyInfoActivity"

    }
}