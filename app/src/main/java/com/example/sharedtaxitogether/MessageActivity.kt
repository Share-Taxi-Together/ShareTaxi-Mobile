package com.example.sharedtaxitogether

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedtaxitogether.adapter.MessageAdapter
import com.example.sharedtaxitogether.adapter.ProfileAdapter
import com.example.sharedtaxitogether.databinding.AcivityMessageBinding
import com.example.sharedtaxitogether.model.Chat
import com.example.sharedtaxitogether.model.Share
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: AcivityMessageBinding
    private lateinit var datas: Share
    private lateinit var profileDatas: HashMap<String, Share.Participant>
    private lateinit var myProfileData: Share.Participant
    private lateinit var db: FirebaseFirestore

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        datas = intent.getSerializableExtra("data") as Share
        profileDatas = datas.participants
//        for(i in 1..profileDatas.size){
//            Log.d(TAG+"before", profileDatas[i.toString()]!!.nickname!!)
//        }
        if(intent.hasExtra("myProfile")){
            myProfileData = intent.getSerializableExtra("myProfile") as Share.Participant
            profileDatas["${profileDatas.size+1}"] = myProfileData
        }

        Log.d("profileDatas", datas.participants.size.toString())

        initMessageRecyclerView()
        initProfileRecyclerView()

        // datas 에는 생성자 정보, 합승 정보, 참가자 정보
        binding.messageActivityStart.text = "출발 - ${datas.place["start"]?.id}"
        binding.messageActivityDest.text = "도착 - ${datas.place["dest"]?.id}"
        binding.messageActivityDepartTime.text = "출발시간 - ${datas.departTime.slice(11..15)}"

        binding.messageActivitySendBtn.setOnClickListener {
            onClickSendBtn()
        }

//        initProfileRecyclerView()
    }

    private fun onClickSendBtn() {
        val msg = binding.messageActivityEditText.text.toString()
        //todo nickname 제대로 가져오기!!
        val userUID = Firebase.auth.currentUser!!.uid
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()).toString()
        if ("" == msg) return

        db.collection("users").document(userUID).get()
            .addOnSuccessListener {
                val nickname = it["nickname"].toString()

                val message = Chat.Comment(Firebase.auth.currentUser?.uid, nickname, msg, time)

                db.collection("shares").document(datas.shareUid)
                    .collection("chat").document().set(message)
                    .addOnSuccessListener {
                        binding.messageActivityEditText.setText("")
                    }
            }
    }

    private fun initMessageRecyclerView() {
        messageAdapter = MessageAdapter(this, datas.shareUid)

        binding.messageActivityRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.messageActivityRecyclerview.adapter = messageAdapter
    }

    private fun initProfileRecyclerView() {
        val profileList = mutableListOf<Share.Participant>()

        for (i in 1..profileDatas.size) {
//            Log.d("here", profileDatas[i.toString()].toString())
            profileList.add(profileDatas[i.toString()]!!)
//            Log.d("for문 ${i}", profileList[i-1])
        }

        //index0이 생성자
        profileAdapter = ProfileAdapter(this, profileList)

        binding.messageActivityProfiles.adapter = profileAdapter
    }

    companion object {
        private const val TAG = "MessageActivity/"
    }
}