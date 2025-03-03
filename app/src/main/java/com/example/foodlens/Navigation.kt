package com.example.foodlens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foodlens.screens.AnalysisPage
import com.example.foodlens.screens.CategoriesPage
import com.example.foodlens.screens.GetStarted
import com.example.foodlens.screens.Home
import com.example.foodlens.screens.LoadingScreen
import com.example.foodlens.screens.LoginPage
import com.example.foodlens.screens.ProfilePage
import com.example.foodlens.screens.Register
import com.example.foodlens.screens.SearchScreen
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
        composable("search") { SearchScreen(navController, viewModel = userViewModel) }
        composable("profile") { ProfilePage(navController,userViewModel) }
        composable("categoriesPage"){ CategoriesPage(navController, userViewModel) }
        composable("analysisPage"){ AnalysisPage("Snickers",navController,) }
        composable("loadingPage") { LoadingScreen(navController) }

    }
}


