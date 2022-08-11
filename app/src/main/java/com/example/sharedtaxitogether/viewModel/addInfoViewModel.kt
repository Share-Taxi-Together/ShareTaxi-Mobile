package com.example.sharedtaxitogether.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class addInfoViewModel: ViewModel() {
    var startLongitude = MutableLiveData<String>()
    var startLatitude = MutableLiveData<String>()
    var destLongitude = MutableLiveData<String>()
    var destLatitude = MutableLiveData<String>()

    var startAddress = MutableLiveData<String>()
    var destAddress = MutableLiveData<String>()

    var start = MutableLiveData<String>()
    var dest = MutableLiveData<String>()
    var memberNum = MutableLiveData<String>()
    var memberGender = MutableLiveData<String>()
    var time = MutableLiveData<String>()
}