package com.example.sharedtaxitogether

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sharedtaxitogether.dao.UserDao
import com.example.sharedtaxitogether.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}