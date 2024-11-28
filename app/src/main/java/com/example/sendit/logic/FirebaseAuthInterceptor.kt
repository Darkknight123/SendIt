package com.example.sendit.logic

import com.example.sendit.logic.App.NetworkTokenManager
import okhttp3.Interceptor
import okhttp3.Response

class FirebaseAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = NetworkTokenManager.getToken()

        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}