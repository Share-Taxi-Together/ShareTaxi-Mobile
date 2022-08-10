package com.example.sharedtaxitogether

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtaxitogether.databinding.AcivityMessageBinding
import com.example.sharedtaxitogether.model.Share

class MessageActivity: AppCompatActivity() {
    private lateinit var binding: AcivityMessageBinding
    lateinit var datas: Share

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datas = intent.getSerializableExtra("data") as Share

        // datas 에는 생성자 정보, 합승 정보, 참가자 정보
        binding.messageActivityStart.text = "출발 - ${datas.start}"
        binding.messageActivityDest.text = "도착 - ${datas.dest}"
        binding.messageActivityDepartTime.text = "출발시간 - ${datas.departTime}"

    }
}