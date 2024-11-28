package com.example.sendit.logic.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sendit.logic.model.DepositModel
import com.example.sendit.logic.model.User
import com.example.sendit.logic.model.UserResponseModel
import com.example.sendit.logic.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _userDetails = MutableStateFlow<Result<UserResponseModel>?>(null)
    val userDetails: StateFlow<Result<UserResponseModel>?> = _userDetails

    suspend fun registerUser(name: String, email: String, photoUrl: String?): Result<Any> {
        return repository.registerUser(User(username = name, email =  email))
    }

    suspend fun deposit(deposit : DepositModel): Result<Any> {
        return repository.deposit(deposit)
    }

    suspend fun updateUserDetails(
        userDetails: User
    ): Result<UserResponseModel> {
        return repository.updateUserDetails(userDetails)
    }

    // Function to fetch user details and return Result<User>
    suspend fun fetchUserDetails(): Result<UserResponseModel> {
        return try {
            val response = repository.getUserDetails()  // Assuming this is the function in the repository
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

