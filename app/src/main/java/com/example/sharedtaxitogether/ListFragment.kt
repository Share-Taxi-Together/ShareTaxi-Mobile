package com.example.sharedtaxitogether

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sharedtaxitogether.adapter.ShareListAdapter
import com.example.sharedtaxitogether.databinding.FragmentListBinding

class ListFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentListBinding

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
}