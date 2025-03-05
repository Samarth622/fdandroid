package com.example.foodlens.network

import android.content.Context
import com.example.foodlens.networks.LoginApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.219.138:3000/api/v1/" // Replace with your local IP

    private var token: String? = null

    fun setToken(newToken: String?) {
        token = newToken
    }

    private fun getTokenFromPrefs(context: Context): String? {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("AUTH_TOKEN", null)
    }

    private fun buildOkHttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request/response body
        }
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS)   // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)  // Write timeout
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestToken = token ?: getTokenFromPrefs(context)
                val request = chain.request().newBuilder().apply {
                    requestToken?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }.build()
                chain.proceed(request)
            }
            .build()
    }

    fun getApiService(context: Context): LoginApiService {
        val okHttpClient = buildOkHttpClient(context)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApiService::class.java)
    }
}