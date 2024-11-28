package com.example.sendit.logic.App

import com.example.sendit.logic.FirebaseAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private var retrofit: Retrofit? = null

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(FirebaseAuthInterceptor())
            .build()
    }

    fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://laxnit-backend-auth.onrender.com")
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    // Call this when token changes
    fun resetRetrofit() {
        retrofit = null
    }
}