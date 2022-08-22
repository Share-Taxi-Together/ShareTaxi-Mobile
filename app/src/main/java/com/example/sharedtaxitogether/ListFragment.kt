package com.example.sharedtaxitogether

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.number.IntegerWidth
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sharedtaxitogether.adapter.ShareListAdapter
import com.example.sharedtaxitogether.databinding.FragmentListBinding
import com.example.sharedtaxitogether.model.Chat
import com.example.sharedtaxitogether.model.Share
import com.example.sharedtaxitogether.viewModel.LoginViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentListBinding
    private val userViewModel: LoginViewModel by activityViewModels()

    lateinit var listAdapter: ShareListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity

        binding = FragmentListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initListRecyclerView()

        return binding.root
    }

    private fun initListRecyclerView() {
        listAdapter = ShareListAdapter(mainActivity)
        listAdapter.setOnItemClickListener(object : ShareListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Share, position: Int) {
                if (checkGender(data.memberGender)) {
                    val builder = AlertDialog.Builder(mainActivity)
                    builder.setMessage("입장하시겠습니까?")
                        .setPositiveButton("입장하기") { _, _ ->
                            checkEntryAvailability(data.shareUid, data.memberCount, data.memberNum, data)
                        }
                        .setNegativeButton("경로확인") { _, _ ->
                            val intent = Intent(context, ShowPathActivity::class.java)
                            intent.putExtra("place", data)
//                            intent.putExtra("start", data.start)
//                            intent.putExtra("dest", data.dest)
                            startActivity(intent)
                        }
                    builder.show()
                } else {
                    Toast.makeText(context, "입장이 불가능한 성별입니다", Toast.LENGTH_SHORT).show()
                }
            }
        })


//        listAdapter.currentList.add(
//            Share(
//                "1234", "", "test", "Female", 1,
//                "TIP", "정왕역", "3", "무관", "23:30"
//            )
//        )
//        listAdapter.currentList.add(
//            Share(
//                "1569", "", "쭈댕", "Male", 1,
//                "정왕역", "유호엔시티", "2", "무관", "23:00"
//            )
//        )
//        db.collection("shares").get()
//            .addOnSuccessListener {
//                for(document in it){
//                    var item = document.toObject(Share::class.java)
//                    data.add(item)
//                }
//                listAdapter.notifyDataSetChanged()
//            }
        binding.recyclerList.adapter = listAdapter
    }

    private fun checkEntryAvailability(shareUid: String, memberCount: Int, memberNum: String, data: Share) {
        val userUid = Firebase.auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("shares").document(shareUid).get()
            .addOnSuccessListener {
                for (i in 1..memberCount) { //이미 입장해있는 참가자
                    val participantUid = it.get(FieldPath.of("participants", "$i", "uid"))
                    if (participantUid == userUid) {
                        Intent(context, MessageActivity::class.java).apply {
                            putExtra("data", data)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }.run { mainActivity.startActivity(this) }
                        break
                    } else {    //새로운 참가자 추가
                        if (i < memberCount) continue

                        if (memberCount >= Integer.parseInt(memberNum)) {
                            Toast.makeText(context, "최대 인원이 다 차서 입장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            val participant = hashMapOf(
                                "participants" to hashMapOf(
                                    "${memberCount + 1}" to Share.Participant(
                                        userViewModel.uid.value!!,
                                        userViewModel.imgUrl.value!!,
                                        userViewModel.nickname.value!!,
                                        userViewModel.gender.value!!
                                    )
                                ))
                            //참가자 추가
                            db.collection("shares").document(shareUid)
                                .set(participant, SetOptions.merge())
                                .addOnSuccessListener {
                                    db.collection("shares").document(shareUid)
                                        .update("memberCount", memberCount + 1)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                                }
                            Intent(context, MessageActivity::class.java).apply {
                                putExtra("data", data)
                                putExtra("myProfile", Share.Participant(
                                        userViewModel.uid.value!!,
                                        userViewModel.imgUrl.value!!,
                                        userViewModel.nickname.value!!,
                                        userViewModel.gender.value!!
                                    ))
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }.run { mainActivity.startActivity(this) }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkGender(memberGender: String): Boolean {
        var result = false
        when (memberGender) {
            "All" -> result = true
            "Female" -> result = userViewModel.gender.value == "Female"
            "Male" -> result = userViewModel.gender.value == "Male"
        }
        return result
    }
}