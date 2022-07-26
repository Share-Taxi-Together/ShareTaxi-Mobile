package com.example.sharedtaxitogether

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sharedtaxitogether.databinding.FragmentAddBinding
import com.example.sharedtaxitogether.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddListFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentAddBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(layoutInflater)

        return binding.root
    }
}