package com.example.sharedtaxitogether

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedtaxitogether.databinding.FragmentChatBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        
        binding.chatFragmentRecyclerview.layoutManager = LinearLayoutManager(requireContext())


        return binding.root
    }



    companion object{
        fun newInstance(): ChatFragment{
            return ChatFragment()
        }
    }
}