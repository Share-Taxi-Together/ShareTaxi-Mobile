package com.example.sharedtaxitogether.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedtaxitogether.model.User

class UserInfoViewModel : ViewModel() {
    var uid = MutableLiveData<String>()          //유저 고유 아이디
    var email = MutableLiveData<String>()        //example@tukorea.ac.kr형식
    var phone = MutableLiveData<String>()
    var gender = MutableLiveData<String>()
    var nickname = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var imgUrl = MutableLiveData<String>()       //프로필 이미지url
    var score = MutableLiveData<String>()         //점수(노쇼 방지)
    var countAddress = MutableLiveData<String>()  //계좌번호

    init {
        Log.i("SignupViewModel", "SignupViewModel Created!!")
    }

    fun insertUserInfo(): User{
        return User(
                uid.value!!,
                email.value!!,
                phone.value!!,
                gender.value!!,
                nickname.value!!,
                password.value!!
            )
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@tukorea.ac.kr")) return false
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}