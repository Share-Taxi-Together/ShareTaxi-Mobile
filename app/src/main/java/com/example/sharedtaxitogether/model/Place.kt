package com.example.sharedtaxitogether.model

import java.io.Serializable

//리사이클러 뷰 아이템
class Place (
    val id: String = "",
    val address: String ="",
    val latitude: String = "",
    val longitude: String = "",
    ): Serializable