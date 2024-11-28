package com.example.sendit.logic.App

import android.util.Log

object NetworkTokenManager {
    private var firebaseToken: String? = null

    fun updateToken(token: String) {
        firebaseToken = token
        Log.e("TAG", "updateToken: $firebaseToken")
    }

    fun getToken(): String? = firebaseToken
}
