package com.example.sharedtaxitogether

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedtaxitogether.adapter.MessageAdapter
import com.example.sharedtaxitogether.databinding.AcivityMessageBinding
import com.example.sharedtaxitogether.model.Chat
import com.example.sharedtaxitogether.model.Share
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: AcivityMessageBinding
    lateinit var datas: Share
    lateinit var profileDatas: HashMap<String, Share.Participant>
    private lateinit var db: FirebaseFirestore

    lateinit var messageAdapter: MessageAdapter
//    lateinit var profileAdapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        datas = intent.getSerializableExtra("data") as Share
        profileDatas = datas.participants

        initMessageRecyclerView()

        // datas 에는 생성자 정보, 합승 정보, 참가자 정보
        binding.messageActivityStart.text = "출발 - ${datas.place["start"]?.id}"
        binding.messageActivityDest.text = "도착 - ${datas.place["dest"]?.id}"
        binding.messageActivityDepartTime.text = "출발시간 - ${datas.departTime}"

        binding.messageActivitySendBtn.setOnClickListener {
            onClickSendBtn()
        }

//        initProfileRecyclerView()
    }

    private fun onClickSendBtn() {
        val msg = binding.messageActivityEditText.text.toString()
        //todo nickname 제대로 가져오기!!
        val userUID = Firebase.auth.currentUser!!.uid

        if ("" == msg) return

        db.collection("users").document(userUID).get()
            .addOnSuccessListener {
                val nickname = it["nickname"].toString()

                val message = Chat.Comment(Firebase.auth.currentUser?.uid, nickname, msg)
                Log.d("hhh", datas.shareUid)

                db.collection("shares").document(datas.shareUid)
                    .collection("chat").document().set(message)
                    .addOnSuccessListener {
                        binding.messageActivityEditText.setText("")
                    }
            }
    }


    private fun initMessageRecyclerView() {
        Log.d(TAG, "initMessageRecyclerView()")
        messageAdapter = MessageAdapter(this, datas.shareUid)

        binding.messageActivityRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.messageActivityRecyclerview.adapter = messageAdapter
    }

    //    private fun initProfileRecyclerView() {
//        profileAdapter = ProfileAdapter(this, profileDatas)
//        binding.messageActivityProfiles.adapter = profileAdapter
//    }
    companion object {
        private const val TAG = "MessageActivity/"
    }
}