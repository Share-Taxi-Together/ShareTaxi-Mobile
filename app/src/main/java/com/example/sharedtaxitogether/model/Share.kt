package com.example.sharedtaxitogether.model

import com.google.firebase.Timestamp

data class Share(
    var creatorId: String,
    var creatorImgUrl: String,
    var creatorNickname: String,
    var creatorGender: String,

    var startPlace: String,
    var destPlace: String,
    var memberNum: String,
    var memberGender: String,
    // 이거맞나 자료형을 어떻게 해야되지,,,
    var departTime: Timestamp){
    data class Participant(
        var uid: String? = null,
        var imgUrl: String? = null,
        var nickname: String? = null,
        var gender: String? = null)
}
//    var participants: Map<String, String> = HashMap()){