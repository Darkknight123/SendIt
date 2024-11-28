package com.example.sendit.logic.model

data class User(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val postalAddress: String? = null,
    val telephone1: String? = null,
    val telephone2: String? = null,
    val username: String? = null
)

data class UserResponseModel(
    val Message: String,
    val MessageToClient: Any,
    val Payload: UserResponsePayload,
    val Status: Int
)

data class UserResponsePayload(
    val _date: Date,
    val _id: String,
    val _timestamp: Long,
    val active: Boolean,
    val approved: Boolean,
    val email: String,
    val feduid: String,
    val firstName: String,
    val lastName: String,
    val location: Location,
    val organisationId: String,
    val postalAddress: String,
    val roles: List<Role>,
    val telephone1: String,
    val telephone2: String,
    val uid: String,
    val username: String
)

data class Date(
    val `$date`: String
)

data class Location(
    val _id: Any,
    val imageUrl: Any,
    val locationName: Any,
    val name: String
)

data class Role(
    val client: Boolean
)

data class ExistingUserResponse(
    val Status: Int,
    val Message: String,
    val Payload: String,
    val MessageToClient: List<String>
)

data class DepositModel(
    val amount: Int,
    val currency: String,
    val transactionRef: String
)

data class DepositResponseModel(
    val Message: String,
    val MessageToClient: Any,
    val Payload: Payload,
    val Status: Int
)

data class Payload(
    val _id: String,
    val amount: Int,
    val currency: String,
    val feduid: String,
    val stage: String,
    val status: String,
    val transactionRef: String
)