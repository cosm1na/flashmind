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

    fun getUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }

    fun saveEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    fun getEmail(): String? {
        return prefs.getString("USER_EMAIL", null)
    }
    fun logout() {
        prefs.edit().clear().apply()
    }

    fun saveUsername(username: String) {
        prefs.edit().putString("USERNAME", username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString("USERNAME", null)
    }

    fun saveProfilePicUri(uri: String) {
        prefs.edit().putString("PROFILE_PIC", uri).apply()
    }

    fun getProfilePicUri(): String? {
        return prefs.getString("PROFILE_PIC", null)
    }

}