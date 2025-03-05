package com.example.foodlens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.foodlens.networks.ImageProductAnalysisResponse
import com.example.foodlens.screens.AnalysisPage
import com.example.foodlens.screens.AnalysisPageImage
import com.example.foodlens.screens.CategoriesPage
import com.example.foodlens.screens.GetStarted
import com.example.foodlens.screens.Home
import com.example.foodlens.screens.LoadingScreen
import com.example.foodlens.screens.LoginPage
import com.example.foodlens.screens.ProfilePage
import com.example.foodlens.screens.Register
import com.example.foodlens.screens.SuggestionPage
import com.example.foodlens.screens.UploadScreen
import com.google.gson.Gson

@Composable
fun Navigation(navController: NavController,context: Context) {

    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(context) // Pass the factory
    )
    val startDestination = if (isLoggedIn) "home" else "getStarted"

    NavHost(
        navController = navController as? NavHostController ?: return,
        startDestination = startDestination,
    ) {
        composable("getStarted") { GetStarted(navController) }
        composable("loginPage") { LoginPage(navController) }
        composable("register") { Register(navController,) }
        composable("home") { Home(navController,userViewModel) }
        composable("search") { UploadScreen(navController, viewModel = userViewModel) }
        composable("profile") { ProfilePage(navController,userViewModel) }
        composable("categoriesPage"){ CategoriesPage(navController, userViewModel) }
        composable(
            "analysisPage/{productName}",
            arguments = listOf(navArgument("productName") { type = NavType.StringType })
        ) { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            AnalysisPage(productName = productName, navHostController = navController)
        }
        composable(
            "imageAnalysisPage/{analysisJson}",
            arguments = listOf(navArgument("analysisJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val analysisJson = backStackEntry.arguments?.getString("analysisJson") ?: ""
            val analysisResponse = Gson().fromJson(analysisJson, ImageProductAnalysisResponse::class.java)
            AnalysisPageImage(analysisResponse = analysisResponse, navHostController = navController)
        }
        composable("loadingPage") { LoadingScreen(navController) }
        composable("suggestion") { SuggestionPage(navController, userViewModel)  }
    }
}



