package com.example.foodlens.networks

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RegisterRetrofitClient {
    private const val BASE_URL = "http://192.168.219.138:3000/api/v1/" // Use 10.0.2.2 for Android emulator localhost

    val apiService: RegisterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegisterApiService::class.java)
    }
}