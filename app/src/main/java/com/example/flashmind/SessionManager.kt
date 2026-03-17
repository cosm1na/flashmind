package com.example.flashmind

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FlashmindApp", Context.MODE_PRIVATE)

    fun salveazaStareaDeLogin(esteLogat: Boolean) {
        prefs.edit().putBoolean("ESTE_LOGAT", esteLogat).apply()
    }

    fun verificaDacaEsteLogat(): Boolean {
        return prefs.getBoolean("ESTE_LOGAT", false)
    }

    fun salveazaUserId(id: Int) {
        prefs.edit().putInt("USER_ID", id).apply()
    }

    fun iaUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }
}