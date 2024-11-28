package com.example.sendit.logic.webservice

import com.example.sendit.logic.model.DepositModel
import com.example.sendit.logic.model.DepositResponseModel
import com.example.sendit.logic.model.ExistingUserResponse
import com.example.sendit.logic.model.User
import com.example.sendit.logic.model.UserResponseModel
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileWebservice {

    @POST("/createuser")
    suspend fun register(
        @Header("SKEY") skey: String,
        @Body registrationRequest: User
    ): UserResponseModel

    @POST("/createTransaction")
    suspend fun createTransaction(
        @Header("SKEY") skey: String,
        @Body registrationRequest: DepositModel
    ): DepositResponseModel


    @POST("/createuser")
    suspend fun registerExistingUser(
        @Header("SKEY") skey: String,
        @Body registrationRequest: User
    ): ExistingUserResponse

    @POST("/updateuserdetails")
    fun updateUserDetails(
        @Header("SKEY") skey: String,
        @Body userDetails: User
    ): UserResponseModel

    @POST("/getuserdetails")
    suspend fun getUserDetails(
        @Header("SKEY") skey: String
    ): UserResponseModel
}