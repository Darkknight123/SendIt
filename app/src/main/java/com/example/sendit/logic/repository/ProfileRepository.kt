package com.example.sendit.logic.repository

import com.example.sendit.logic.App.NetworkModule
import com.example.sendit.logic.App.NetworkTokenManager
import com.example.sendit.logic.model.DepositModel
import com.example.sendit.logic.model.DepositResponseModel
import com.example.sendit.logic.model.User
import com.example.sendit.logic.model.UserResponseModel
import com.example.sendit.logic.webservice.ProfileWebservice
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ProfileRepository {
    private val profileService = NetworkModule.getRetrofit().create(ProfileWebservice::class.java)

    suspend fun registerUser(user: User): Result<Any> {
        return try {
            val token = NetworkTokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))

            try {
                // First try parsing as UserResponseModel (successful new registration)
                val response = profileService.register(token, user)
                Result.success(response)
            } catch (e: JsonSyntaxException) {
                // If that fails, try parsing as ExistingUserResponse
                val existingUserResponse = profileService.registerExistingUser(token, user)
                Result.success(existingUserResponse)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deposit(user: DepositModel): Result<DepositResponseModel> {
        return try {
            val token = NetworkTokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))

            try {
                // First try parsing as UserResponseModel (successful new registration)
                val response = profileService.createTransaction(token, user)
                Result.success(response)
            } catch (e: JsonSyntaxException) {
                // If that fails, try parsing as ExistingUserResponse
                val existingUserResponse = profileService.createTransaction(token, user)
                Result.success(existingUserResponse)
            }
        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    suspend fun updateUserDetails(user: User): Result<UserResponseModel> {
        return try {
            val token = NetworkTokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))

            try {
                // First try parsing as UserResponseModel (successful new registration)
                val response = profileService.updateUserDetails(token, user)
                Result.success(response)
            } catch (e: JsonSyntaxException) {
                // If that fails, try parsing as ExistingUserResponse
                val existingUserResponse = profileService.updateUserDetails(token, user)
                Result.success(existingUserResponse)
            }
        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    // Assuming a function to fetch user details
    suspend fun getUserDetails(): UserResponseModel {
        val token = NetworkTokenManager.getToken()
            ?: throw Exception("No authentication token available")

        return try {
            val response = profileService.getUserDetails(token) // Assuming this returns a User object
            response
        } catch (e: Exception) {
            throw Exception("Failed to fetch user details", e)
        }
    }
}