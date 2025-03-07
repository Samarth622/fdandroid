package com.example.foodlens.networks

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query


data class LoginRequest(
    val mobile: String,
    val password: String
)

data class LoginResponse(
    val message: String?,
    val token: String?,
    val error: String?
)

data class Product(
    val name: String,
    val image_url: String,
    val ingredients: String,
    val nutritions: String
)

data class ProductsResponse(
    val products: List<Product>
)

data class NutrientAnalysis(
    val nutrient_en: String,
    val nutrient_hi: String,
    val rating: Float, // 1-10
    val explanation_en: String,
    val explanation_hi: String,
)

data class OverallAnalysis(
    val rating: Float, // 1-5
    val explanation_en: String,
    val explanation_hi: String,
)

data class SuggestedAlternative(
    val name: String,
    val reason_en: String,
    val reason_hi: String,
)

data class ProductAnalysis(
    val nutrient_analysis: List<NutrientAnalysis>,
    val overall_analysis: OverallAnalysis,
    val suggested_alternatives: List<SuggestedAlternative>
)

data class ProductAnalysisResponse(
    val analysis: ProductAnalysis , // Wrap the actual analysis data
    val productImage: String
)

data class ImageProductAnalysisResponse(
    val analysis: ProductAnalysis
)

data class UserProfile(
    val name: String?,
    val mobile: String?,
    val gender: String?,
    val email: String?,
    val age: String?,
    val height: String?,
    val weight: String?,
    val medicalHistory: String?,
    val allergies: String?,
    val bloodGroup: String?
)

data class FoodRecommendation(
    val productName: String,
    val benefits: String,
    val category: String
)

data class FoodRecommendationsResponse(
    val foodRecommendations: List<FoodRecommendation>
)

// Response wrapper for getProfile
data class UserProfileResponse(
    val User: UserProfile
)

// Request body for updateProfile
data class UpdateProfileRequest(
    val name: String?,
    val mobile: String?,
    val gender: String?,
    val email: String?,
    val age: Int?,
    val height: Int?,
    val weight: Int?,
    val medicalHistory: String?,
    val allergies: String?,
    val bloodGroup: String?
)

// Response wrapper for updateProfile
data class UpdateProfileResponse(
    val message: String,
    val user: UserProfile
)

interface LoginApiService {
    @POST("user/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @GET("product/category")
    suspend fun getProductsByCategory(@Query("categoryName") categoryName: String): Response<ProductsResponse>

    @GET("product/productAnalysis")
    suspend fun getProductAnalysis(@Query("productName") productName: String): Response<ProductAnalysisResponse>

    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @POST("user/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>

    @Multipart
    @POST("product/imageProductAnalysis")
    suspend fun analyzeImage(@Part image: MultipartBody.Part): Response<ImageProductAnalysisResponse>

    @GET("product/productSuggest")
    suspend fun getFoodRecommendations(): Response<FoodRecommendationsResponse>
}