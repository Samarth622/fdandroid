package com.example.foodlens.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodlens.R
import java.util.Locale

@Composable
fun GetStarted(navHostController: NavHostController) {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    // Load initial language from SharedPreferences
    var selectedLanguage by remember {
        mutableStateOf(preferences.getString("language", "English") ?: "English")
    }
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Hindi")

    val updateLocale: (String) -> Unit = { language ->
        val locale = when (language) {
            "Hindi" -> Locale("hi")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background2),
            contentDescription = "BackGround",
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.shoppinghome),
                contentDescription = null,
                modifier = Modifier.scale(1.8f)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Eat Healthy",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                color = Color(70, 66, 66, 193),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Normal),
            )

            Text(
                text = "Maintaining good health should be the primary focus of everyone",
                modifier = Modifier.padding(10.dp),
                color = Color(70, 66, 66, 133),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )

            Button(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 15.dp)
                    .height(60.dp)
                    .fillMaxWidth(.8f)
                    .background(colorResource(R.color.green), CircleShape),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = {
                    navHostController.navigate("register")
                }
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 19.sp
                )
            }
        }

        // Language dropdown positioned at top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 38.dp, bottom = 16.dp, start = 16.dp, end = 20.dp)
        ) {

            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = selectedLanguage,
                    color = Color.Black
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            selectedLanguage = language
                            updateLocale(language)
                            preferences.edit().putString("language", language).apply()
                            expanded = false
                            (context as? Activity)?.recreate()
                        }
                    )
                }
            }
        }
    }
}