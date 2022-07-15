package com.example.sharedtaxitogether.model

data class User (
    var uid : String?,           //유저 고유 아이디
    var email : String,         //example@tukorea.ac.kr형식
    var phone: String,
    var gender: String,
    var nickname: String,
    var password: String,
    var imgUrl: String? = null,         //프로필 이미지url
    var score: Int? = null,             //점수(노쇼 방지)
    var countAddress: String? = null    //계좌번호
    )