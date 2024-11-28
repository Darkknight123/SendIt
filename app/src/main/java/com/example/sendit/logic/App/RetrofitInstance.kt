package com.example.sendit.logic.App

import com.example.sendit.logic.webservice.ProfileWebservice
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://laxnit-backend-auth.onrender.com/"

    val api: ProfileWebservice by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // For JSON deserialization
            .build()
            .create(ProfileWebservice::class.java)
    }
}