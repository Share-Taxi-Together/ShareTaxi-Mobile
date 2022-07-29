package com.example.sharedtaxitogether.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey var uid: String,           //유저 고유 아이디
    @ColumnInfo var email: String,         //example@tukorea.ac.kr형식
    @ColumnInfo var phone: String,
    @ColumnInfo var gender: String,
    @ColumnInfo var nickname: String,
    @ColumnInfo var password: String,
    @ColumnInfo var score: String,        //점수(노쇼 방지)
    @ColumnInfo var imgUrl: String,         //프로필 이미지url
    @ColumnInfo var countAddress: String    //계좌번호
    )