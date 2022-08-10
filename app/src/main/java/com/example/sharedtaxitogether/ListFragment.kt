package com.example.sharedtaxitogether

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sharedtaxitogether.adapter.ShareListAdapter
import com.example.sharedtaxitogether.databinding.FragmentListBinding
import com.example.sharedtaxitogether.model.Share
import com.example.sharedtaxitogether.viewModel.LoginViewModel

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
                    builder.setMessage("회원가입을 취소하시겠습니까?")
                        .setPositiveButton("입장하기") { _, _ ->
                            Intent(context, MessageActivity::class.java).apply {
                                putExtra("data", data)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }.run { mainActivity.startActivity(this) }
                        }
                        .setNegativeButton("경로확인") { _, _ ->

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