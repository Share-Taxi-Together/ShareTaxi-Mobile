package com.example.sharedtaxitogether.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class addInfoViewModel: ViewModel() {
    var startLongitude = MutableLiveData<String>()
    var startLatitute = MutableLiveData<String>()
    var destLongitude = MutableLiveData<String>()
    var destLatitude = MutableLiveData<String>()

//    var member = MutableLiveData<String>()
//    var gender = MutableLiveData<String>()
//    var time = MutableLiveData<String>()


}