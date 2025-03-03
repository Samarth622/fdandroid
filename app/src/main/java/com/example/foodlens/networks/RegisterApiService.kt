package com.example.foodlens.networks

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val name: String,
    val gender: String,
    val email: String,
    val mobile: String,
    val password: String
)

data class RegisterResponse(
    val message: String?,
    val error: String?
)

interface RegisterApiService {
    @POST("user/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>
}