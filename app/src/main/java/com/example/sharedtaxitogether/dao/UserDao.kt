package com.example.sharedtaxitogether.dao

import androidx.room.*
import com.example.sharedtaxitogether.model.User

@Dao
interface UserDao {
    @Query("select * from user")
    fun getAll(): List<User>

    @Query("select * from user")
    fun getUser(): User

    @Query("select uid from user")
    fun getUid(): String
    @Query("select email from user")
    fun getEmail(): String
    @Query("select phone from user")
    fun getPhone(): String
    @Query("select gender from user")
    fun getGender(): String
    @Query("select nickname from user")
    fun getNickname(): String
    @Query("select password from user")
    fun getPassword(): String
    @Query("select score from user")
    fun getScore(): String

    @Query("select imgUrl from user")
    fun getImgUrl(): String
    @Query("select countAddress from user")
    fun getCountAddress(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Delete
    fun delete(user: User)

}