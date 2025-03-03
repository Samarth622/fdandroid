package com.example.foodlens.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodlens.R

@Composable
fun GetStarted(navHostController: NavHostController) {

        Box(modifier = Modifier.fillMaxSize()){

            Image(
                painter = painterResource(R.drawable.background2),
                contentDescription = "BackGround",
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )



            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(13.dp)){

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
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)
                        .height(60.dp)
                        .fillMaxWidth(.8f)
                        .background(colorResource(R.color.green), CircleShape),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    onClick = {
                            navHostController.navigate("register")
                    }) {
                    Text(
                        text = "Get Started",color= Color.White,
                        fontSize = 19.sp
                    )
                }
            }


        }
    }

