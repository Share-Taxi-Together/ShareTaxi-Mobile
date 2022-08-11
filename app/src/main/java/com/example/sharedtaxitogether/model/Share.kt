package com.example.sharedtaxitogether.model

import java.io.Serializable

class Share(
//    val creatorId: String,
//    val creator: HashMap<String, Participant> = HashMap(),

//    var startPlace: HashMap<String, Place> = HashMap(),
//    var destPlace: HashMap<String, Place> = HashMap(),

//    var creatorUid: String = "",
//    var creatorImgUrl: String = "",
//    var creatorNickname: String = "",
//    var creatorGender: String = "",

    var shareUid: String = "",  //db에 추가한 시간
    var memberCount: Int = 1,

//    var start: String = "",
//    var dest: String = "",
    var place: HashMap<String, Place> = HashMap(),
    var memberNum: String = "",
    var memberGender: String = "",
    var departTime: String = "",

    var participants: HashMap<String, Participant> = HashMap()): Serializable {
    class Participant(
        var uid: String? = null,
        var imgUrl: String? = null,
        var nickname: String? = null,
        var gender: String? = null
    ):Serializable
}