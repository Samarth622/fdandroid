package com.example.foodlens.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.foodlens.R
import com.example.foodlens.UserViewModel
import com.example.foodlens.networks.LoginApiService
import com.example.foodlens.network.RetrofitClient
import com.example.foodlens.networks.Product
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun CategoriesPage(navHostController: NavHostController, viewModel: UserViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService: LoginApiService = RetrofitClient.getApiService(context)
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val category by viewModel.category.collectAsState()

    LaunchedEffect(category) {
        if (category != null) {
            coroutineScope.launch {
                try {
                    val response = apiService.getProductsByCategory(category!!)
                    if (response.isSuccessful) {
                        products = response.body()?.products ?: emptyList()
                        if (products.isEmpty()) {
                            Toast.makeText(context, "No products found for $category", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMessage = when (response.code()) {
                            401 -> "Unauthorized: Please log in again"
                            403 -> "Forbidden: Invalid token"
                            else -> "Failed to fetch products: ${response.code()}"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
        } else {
            Toast.makeText(context, "No category selected", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(navHostController, "Products")

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products available", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    items(products.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (rowItems.size == 1) Arrangement.Center else Arrangement.SpaceEvenly
                        ) {
                            rowItems.forEach { product ->
                                ProductItem(navHostController, product)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
        }
    }
}

@Composable
fun TopAppBar(navHostController: NavHostController, title: String) {
    Card(
        modifier = Modifier
            .padding(top = 40.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(colorResource(R.color.lightGreen)),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(.7f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back button"
                )
            }
            Text(
                text = title,
                color = Color(54, 54, 54, 191),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}

@Composable
fun ProductItem(navController: NavHostController, product: Product) {
    Card(
        modifier = Modifier
            .clickable {
                navController.navigate("analysisPage/${product.name}")
            }
            .size(160.dp)
            .padding(top = 12.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image_url),
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Center, // Ensure center alignment
                modifier = Modifier.fillMaxWidth() // Make text take full width to center properly
            )
        }
    }
}
