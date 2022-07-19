package com.example.sharedtaxitogether

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sharedtaxitogether.databinding.ActivityTextBinding
import com.example.sharedtaxitogether.viewModel.CounterViewModel

class CounterActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTextBinding
    private val model : CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text)

        binding.lifecycleOwner = this
        binding.viewModel = model
    }
}