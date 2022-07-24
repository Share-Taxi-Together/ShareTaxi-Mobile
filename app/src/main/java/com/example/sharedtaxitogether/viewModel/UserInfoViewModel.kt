package com.example.sharedtaxitogether.viewModel

import android.util.Log
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

    var existEmail = MutableLiveData<Boolean>()
    var existPhone = MutableLiveData<Boolean>()

    init {
        Log.i("SignupViewModel", "SignupViewModel Created!!")
        score.value = "0"
    }


}