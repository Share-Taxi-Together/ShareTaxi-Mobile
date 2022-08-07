package com.example.sharedtaxitogether.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddPlaceViewModel: ViewModel() {
    var id = MutableLiveData<String>()
    var address = MutableLiveData<String>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()

    var pointX = MutableLiveData<Int>()
    var pointY = MutableLiveData<Int>()
}