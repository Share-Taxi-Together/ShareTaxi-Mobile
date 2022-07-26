package com.example.sharedtaxitogether

import android.content.Context
import android.content.SharedPreferences
import com.example.sharedtaxitogether.model.User
import java.lang.UnsupportedOperationException

class UserSharedPreferences(context: Context) {
    val PREFS_FILENAME = "user"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    val editor = prefs.edit()

    fun putValue(key: String, value: Any?){
        when(value){
            is String? -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> throw UnsupportedOperationException("Error")
        }
        editor.commit()
    }

    fun getStringValue(key: String): String{
        return prefs.getString(key,"").toString()
    }

    fun getLoginSession(): Boolean{
        return prefs.getBoolean("loginSession", false)
    }

    fun saveUser(user: User){
        editor.putString("uid", user.uid).apply()
        editor.putString("email", user.email).apply()
        editor.putString("phone", user.phone).apply()
        editor.putString("gender", user.gender).apply()
        editor.putString("nickname", user.nickname).apply()
        editor.putString("password", user.password).apply()
        editor.putString("score", user.score).apply()
        editor.putString("imgUrl", user.imgUrl).apply()
        editor.putString("countAddress", user.countAddress).apply()
    }

    fun getUser(): User{
        return User(
            prefs.getString("uid","").toString(),
            prefs.getString("email","").toString(),
            prefs.getString("phone","").toString(),
            prefs.getString("gender","").toString(),
            prefs.getString("nickname","").toString(),
            prefs.getString("password","").toString(),
            prefs.getString("score","").toString(),
            prefs.getString("imgUrl","").toString(),
            prefs.getString("countAddress","").toString()
        )
    }
}