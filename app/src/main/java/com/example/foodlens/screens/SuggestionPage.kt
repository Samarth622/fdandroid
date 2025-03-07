package com.example.foodlens.screens

import ChatBotScreen
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodlens.FloatingBottomNavigation
import com.example.foodlens.R
import com.example.foodlens.UserViewModel
import com.example.foodlens.network.RetrofitClient
import com.example.foodlens.networks.FoodRecommendation
import com.example.foodlens.networks.FoodRecommendationsResponse
import com.example.foodlens.networks.LoginApiService
import kotlinx.coroutines.launch

@Composable
fun SuggestionPage(navHostController: NavHostController, viewModel: UserViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService: LoginApiService = RetrofitClient.getApiService(context)
    var recommendations by remember { mutableStateOf<FoodRecommendationsResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch recommendations on load
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = apiService.getFoodRecommendations()
                if (response.isSuccessful) {
                    recommendations = response.body()
                } else {
                    Toast.makeText(context, "Failed to fetch recommendations: ${response.code()}", Toast.LENGTH_SHORT).show()
                    if (response.code() == 401) {
                        navHostController.navigate("loginPage") { popUpTo(0) { inclusive = true } }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column {
            Title()

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.padding(bottom = 65.dp)) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        recommendations?.let { response ->
                            SuggestionContent(response.foodRecommendations)
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No recommendations available", color = Color.Gray, fontSize = 16.sp)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }

        ChatBotScreen(viewModel)
        FloatingBottomNavigation(navHostController)
    }
}

@Composable
fun SuggestionContent(recommendations: List<FoodRecommendation>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        recommendations.forEach { recommendation ->
            SuggestionItem(
                item = recommendation.productName,
                description = recommendation.benefits,
                category = recommendation.category
            )
        }
    }
}

@Composable
fun SuggestionItem(item: String, description: String, category: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(20))
            .wrapContentSize(),
        colors = CardDefaults.cardColors(Color(236, 235, 235, 255))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.suggestionitem),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = category,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = description,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    textAlign = TextAlign.Justify,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun Title() {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Need a healthy suggestion?",
                fontWeight = FontWeight.SemiBold,
                fontSize = 37.sp,
                lineHeight = 42.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}