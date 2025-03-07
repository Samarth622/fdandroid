package com.example.foodlens.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.foodlens.R
import com.example.foodlens.networks.ImageProductAnalysisResponse

@Composable
fun AnalysisPageImage(analysisResponse: ImageProductAnalysisResponse, navHostController: NavHostController) {
    val data  = analysisResponse.analysis
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    // Load the saved language (set in GetStarted or ProfilePage)
    val selectedLanguage by remember { mutableStateOf(preferences.getString("language", "English") ?: "English") }

    val isHindi = selectedLanguage == "Hindi"

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

                            Image(
                                painter = rememberAsyncImagePainter(R.drawable.nt),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Crop
                            )

                        Text(
                            text = stringResource(R.string.analyzed_image),
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 210.dp),
                            color = Color(54, 54, 54, 191),
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = Int.MAX_VALUE, // Allows unlimited lines, text will wrap as needed
                            overflow = TextOverflow.Clip, // Optional: specifies how to handle overflow, Clip is default
                            textAlign = TextAlign.Center
                        )
                        MeterArc(data.overall_analysis.rating.toFloat() * 2, modifier = Modifier.scale(1.15f))
                        AboutColor()
                    }
                }
            }
            item{
                Row (modifier = Modifier.fillMaxWidth()){
                    Text(text= stringResource(R.string.disclaimer), fontStyle = FontStyle.Italic, fontSize = 14.sp)
                }
            }
            item {
                val concerns = data.nutrient_analysis.filter { it.rating <= 4.0 }
                val neutral = data.nutrient_analysis.filter { it.rating in 5.0..7.0 }
                val likes = data.nutrient_analysis.filter { it.rating > 7.0 }

                if (concerns.isNotEmpty()) {
                    WhatIsItUpTo(stringResource(R.string.what_concerns_us), R.drawable.shocked)
                    concerns.forEach { nutrient ->
                        NutritionItem(
                            item = if (isHindi) nutrient.nutrient_hi else nutrient.nutrient_en,
                            rating = nutrient.rating.toFloat(),
                            description = if (isHindi) nutrient.explanation_hi else nutrient.explanation_en
                        )
                    }
                }

                if (neutral.isNotEmpty()) {
                    WhatIsItUpTo(stringResource(R.string.neutral), R.drawable.neutral)
                    neutral.forEach { nutrient ->
                        NutritionItem(
                            item = if (isHindi) nutrient.nutrient_hi else nutrient.nutrient_en,
                            rating = nutrient.rating.toFloat(),
                            description = if (isHindi) nutrient.explanation_hi else nutrient.explanation_en
                        )
                    }
                }

                if (likes.isNotEmpty()) {
                    WhatIsItUpTo(stringResource(R.string.what_we_like), R.drawable.smile)
                    likes.forEach { nutrient ->
                        NutritionItem(
                            item = if (isHindi) nutrient.nutrient_hi else nutrient.nutrient_en,
                            rating = nutrient.rating.toFloat(),
                            description = if (isHindi) nutrient.explanation_hi else nutrient.explanation_en
                        )
                    }
                }
            }
            item {
                SuggestionsInAnalysis(data.suggested_alternatives, selectedLanguage)
            }
            item {
                Conclusion(stringResource(R.string.conclusion), description = if (isHindi) data.overall_analysis.explanation_hi else data.overall_analysis.explanation_en)
            }
        }
    }
}