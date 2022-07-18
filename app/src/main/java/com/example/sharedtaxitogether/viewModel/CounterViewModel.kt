package com.example.sharedtaxitogether.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel: ViewModel() {
    var count = MutableLiveData<Int>()

    init{
        count.value = 0
    }

    fun increase(){
        count.value = count.value?.plus(1)
    }
    fun decrease(){
        count.value = count.value?.minus(1)
    }
}
