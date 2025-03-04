package com.example.foodlens.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.foodlens.R
import com.example.foodlens.networks.ImageProductAnalysisResponse
import com.example.foodlens.networks.ProductAnalysis

@Composable
fun AnalysisPageImage(analysisResponse: ImageProductAnalysisResponse, navHostController: NavHostController) {
    val data  = analysisResponse.analysis

    if (data == null) {
        // Display an error message or a loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Analysis data is unavailable")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(navHostController, "Image Analysis")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp)
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

                            Image(
                                painter = rememberAsyncImagePainter(R.drawable.biscuits),
                                contentDescription = null,
                                modifier = Modifier
                                    .scale(1.2f)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                        Text(
                            text = "Analyzed Image",
                            modifier = Modifier.padding(top = 180.dp),
                            color = Color(54, 54, 54, 191),
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        MeterArc(data.overall_analysis.rating.toFloat() * 2, modifier = Modifier.scale(1.1f))
                        AboutColor()
                    }
                }
            }
            item {
                val concerns = data.nutrient_analysis.filter { it.rating < 4 }
                val neutral = data.nutrient_analysis.filter { it.rating in 4..7 }
                val likes = data.nutrient_analysis.filter { it.rating > 7 }

                if (concerns.isNotEmpty()) {
                    WhatIsItUpTo("What Concerns Us", R.drawable.shocked)
                    concerns.forEach { nutrient ->
                        NutritionItem(nutrient.nutrient, nutrient.rating.toFloat(), nutrient.explanation)
                    }
                }

                if (neutral.isNotEmpty()) {
                    WhatIsItUpTo("Neutral", R.drawable.neutral)
                    neutral.forEach { nutrient ->
                        NutritionItem(nutrient.nutrient, nutrient.rating.toFloat(), nutrient.explanation)
                    }
                }

                if (likes.isNotEmpty()) {
                    WhatIsItUpTo("What We Like", R.drawable.smile)
                    likes.forEach { nutrient ->
                        NutritionItem(nutrient.nutrient, nutrient.rating.toFloat(), nutrient.explanation)
                    }
                }
            }
            item {
                SuggestionsInAnalysis(data.suggested_alternatives)
            }
            item {
                Conclusion("Conclusion", data.overall_analysis.explanation)
            }
        }
    }
}