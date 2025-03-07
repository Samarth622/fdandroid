package com.example.foodlens.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.foodlens.R
import com.example.foodlens.network.RetrofitClient
import com.example.foodlens.networks.LoginApiService
import com.example.foodlens.networks.ProductAnalysisResponse
import com.example.foodlens.networks.SuggestedAlternative
import kotlinx.coroutines.launch
import kotlin.math.min


@SuppressLint("SuspiciousIndentation")
@Composable
fun AnalysisPage(productName: String, navHostController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService: LoginApiService = RetrofitClient.getApiService(context)
    var analysisResponse by remember { mutableStateOf<ProductAnalysisResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(productName) {
        coroutineScope.launch {
            try {
                val response = apiService.getProductAnalysis(productName)
                if (response.isSuccessful) {
                    analysisResponse = response.body()
                } else {
                    Toast.makeText(context, "Failed to fetch analysis: ${response.code()}", Toast.LENGTH_SHORT).show()
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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(navHostController, "Analysis")

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            analysisResponse?.let { response ->
                val data = response.analysis
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .padding(top = 20.dp, bottom = 20.dp)
                                .fillMaxWidth()
                                .size(350.dp),
                            colors = CardDefaults.cardColors(Color.White),
                            elevation = CardDefaults.cardElevation(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val img = response.productImage
                                Image(
                                    painter = rememberAsyncImagePainter(img),
                                    contentDescription = null,
                                    modifier = Modifier.size(160.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Text(
                                    text = productName,
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(top = 210.dp),
                                    color = Color(54, 54, 54, 191),
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1, // Allows unlimited lines, text will wrap as needed
                                    overflow = TextOverflow.Clip, // Optional: specifies how to handle overflow, Clip is default
                                    textAlign = TextAlign.Center
                                )
                                Text(text=data.overall_analysis.rating.toString(), fontSize = 30.sp)

                                MeterArc(data.overall_analysis.rating.toFloat(), modifier = Modifier.scale(1.15f)) // Scale 1-5 to 1-10
                                AboutColor()
                            }
                        }
                    }
                    item{
                        Row (modifier = Modifier.fillMaxWidth()){
                            Text(text="*Disclaimer: This analysis is based on our study, for a proper explanation refer to a dietitian.", fontStyle = FontStyle.Italic, fontSize = 14.sp)
                        }
                    }
                    item {
                        // Group nutrients dynamically
                        val concerns = data.nutrient_analysis.filter { it.rating <= 4 }
                        val neutral = data.nutrient_analysis.filter { it.rating > 4 && it.rating <= 7 }
                        val likes = data.nutrient_analysis.filter { it.rating > 7 }

                        if (concerns.isNotEmpty()) {
                            WhatIsItUpTo("What Concerns Us", R.drawable.shocked)
                            concerns.forEach { nutrient ->
                                NutritionItem(nutrient.nutrient_en, nutrient.rating.toFloat(), nutrient.explanation_en)
                            }
                        }

                        if (neutral.isNotEmpty()) {
                            WhatIsItUpTo("Neutral", R.drawable.neutral)
                            neutral.forEach { nutrient ->
                                NutritionItem(nutrient.nutrient_en, nutrient.rating.toFloat(), nutrient.explanation_en)
                            }
                        }

                        if (likes.isNotEmpty()) {
                            WhatIsItUpTo("What We Like", R.drawable.smile)
                            likes.forEach { nutrient ->
                                NutritionItem(nutrient.nutrient_en, nutrient.rating.toFloat(), nutrient.explanation_en)
                            }
                        }
                    }
                    item {
                        SuggestionsInAnalysis(data.suggested_alternatives)
                    }
                    item {
                        Conclusion("Conclusion", data.overall_analysis.explanation_en)
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No analysis data available", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}


@Composable
fun MeterArc(value: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        val sweepAngle = (252f / 5) * value.coerceIn(0.0f, 5.0f) // 70% of 360° scaled to 5 max value

        val color = when {
            value < 1.25 -> colorResource(R.color.red)
            value in 1.25f..2.9f -> colorResource(R.color.orange)
            value in 3.0f..3.9f -> colorResource(R.color.yellow)
            value in  4.0..5.0 -> colorResource(R.color.green)
            else -> colorResource(R.color.black)
        }

        Canvas(modifier = modifier.size(400.dp)) {
            val strokeWidth = size.minDimension * 0.07f
            val radius = min(size.width, size.height) / 2 - strokeWidth

            drawArc(
                color = Color(209, 231, 223, 237),
                startAngle = -216f, // Start at -216° (left-top)
                sweepAngle = 252f, // Full arc for reference
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
            )

            drawArc(
                color = color,
                startAngle = -216f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
            )
        }
    }

}


@Composable
fun AboutColor() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            AboutColorItem("Safe", colorResource(R.color.green))
            AboutColorItem("Moderate", colorResource(R.color.yellow))
            AboutColorItem("Risky", colorResource(R.color.orange))
            AboutColorItem("Avoid", colorResource(R.color.red))


        }
    }

}

@Composable
fun AboutColorItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Canvas(modifier = Modifier.size(15.dp)) {
            drawCircle(color = color, radius = size.minDimension / 2)
        }
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = text,
            fontSize = 15.sp
        )

    }

}

@Composable
fun RatingMeter(
    rating: Float = 7f // Value from 0 to 10
) {
    val animatedRating by animateFloatAsState(targetValue = min(rating, 10f), label = "")

    val colors = listOf(
        Color(0xFFD32F2F), // Red
        Color(0xFFFF8C00), // Orange
        Color(0xFFFFC20E), // Yellow
        Color(0xFF7AC943), // Light Green
    )

    val sectionCount = colors.size
    val sectionWidth = 60.dp // Adjust width of each section
    val spacing = 4.dp // Gap between sections

    Column(modifier = Modifier.fillMaxWidth()) {
        // Meter
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(213, 210, 210, 164))
                .padding(5.dp)
                .fillMaxWidth()

        ) {
            colors.forEachIndexed { index, color ->
                if (index == 0) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(15.dp)
                            .padding(horizontal = 1.dp)
                            .clip(RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp))
                            .background(color)
                            .padding(horizontal = if (index < sectionCount - 1) spacing else 0.dp)
                    )
                } else if (index == 3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.dp)
                            .height(15.dp)
                            .clip(RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                            .background(color)
                            .padding(horizontal = if (index < sectionCount - 1) spacing else 0.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.dp)
                            .height(15.dp)
                            .background(color)
                            .padding(horizontal = if (index < sectionCount - 1) spacing else 0.dp)
                    )
                }

            }
        }

        // Pointer
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
        ) {
            val totalWidth = size.width
            val position = (animatedRating / 10f) * totalWidth

            // Draw pointer triangle
            val pointerSize = 40f
            val path = Path().apply {
                moveTo(position, 0f)
                lineTo(position - pointerSize / 2, pointerSize)
                lineTo(position + pointerSize / 2, pointerSize)
                close()
            }

            drawIntoCanvas {
                it.drawPath(path, Paint())
            }
        }
    }
}

@Composable
fun NutritionItem(item: String, rating: Float, description: String) {

    val colors = listOf(
        Color(0xFFD32F2F), // Red
        Color(0xFFFFC20E), // Yellow
        Color(0xFF7AC943), // Light Green
    )
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(top = 10.dp, bottom = if (expanded) 10.dp else 0.dp)
            .fillMaxWidth()
            .wrapContentSize(),
        colors = CardDefaults.cardColors(Color(236, 235, 235, 128))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = item,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = if (rating <= 4) colors[0]
                    else if (rating > 4 && rating <= 7) colors[1]
                    else colors[2],
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                IconButton(onClick = { expanded = !expanded }) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    RatingMeter(rating)
                    Text(text = description, color = Color.Black)
                }
            }
        }
    }
}


@Composable
fun WhatIsItUpTo(title: String, emoji: Int) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(40.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
        Spacer(modifier = Modifier.size(7.dp))
        Image(painter = painterResource(emoji), contentDescription = null)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Conclusion(item: String, description: String) {
    var expanded by remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(top = 10.dp, bottom = if (expanded) 10.dp else 20.dp)
            .fillMaxWidth()
            .wrapContentSize()
            .onGloballyPositioned {
                if (expanded) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        colors = CardDefaults.cardColors(Color(236, 235, 235, 128))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                        if (expanded) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = item,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 23.sp
                )

                IconButton(onClick = {
                    expanded = !expanded
                    if (expanded) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Text(
                        text = description,
                        color = Color.Black,
                        textAlign = TextAlign.Justify,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionsInAnalysis(suggestions: List<SuggestedAlternative>) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 20.dp),
        colors = CardDefaults.cardColors(Color.White),
    ) {
        Column {
            Text(
                text = "Suggested Food Items",
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(10.dp)
            )

            LazyRow(
                modifier = Modifier.padding()
            ) {
                items(suggestions) { suggestion ->
                    SuggestedItems(suggestion.name, suggestion.reason_en)
                }
            }
        }
    }
}

@Composable
fun SuggestedItems(item: String, description: String) {
    Card(
        modifier = Modifier
            .size(width = 300.dp, height = 150.dp) // Fixed width and height
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(), // Ensure content fills the card
            verticalArrangement = Arrangement.SpaceBetween // Distribute space evenly
        ) {
            Text(
                text = item,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1, // Limit to one line
                overflow = TextOverflow.Ellipsis // Truncate with ellipsis if too long
            )
            Text(
                text = description,
                fontSize = 12.sp,
                maxLines = 4, // Limit to three lines (adjust as needed)
                overflow = TextOverflow.Ellipsis // Truncate with ellipsis if too long
            )
        }
    }
}