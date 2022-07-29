package com.example.sharedtaxitogether.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserInfoViewModel : ViewModel() {
    var uid = MutableLiveData<String>()          //유저 고유 아이디
    var email = MutableLiveData<String>()        //example@tukorea.ac.kr형식
    var phone = MutableLiveData<String>()
    var gender = MutableLiveData<String>()
    var nickname = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var score = MutableLiveData<String>()         //점수(노쇼 방지)
    var imgUrl = MutableLiveData<String>()
    var countAddress = MutableLiveData<String>()

    var existEmail = MutableLiveData<Boolean>()
    var existPhone = MutableLiveData<Boolean>()

    init {
        score.value = "0"
        imgUrl.value = ""
        countAddress.value = ""
    }
}