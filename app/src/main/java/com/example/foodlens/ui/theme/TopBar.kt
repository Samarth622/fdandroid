package com.example.foodlens.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodlens.R

@Preview(showSystemUi = true)
@Composable
fun TopBar() {

//    Card(
//        shape = RoundedCornerShape(20.dp),
//        modifier = Modifier
//            .fillMaxWidth(.6f)
//            .padding(top = 20.dp, start = 20.dp, end =20.dp)
//            .height(60.dp),
//        elevation = CardDefaults.cardElevation(10.dp),
//        colors = CardDefaults.cardColors(Color(188, 237, 212, 255))
//    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                color= colorResource(R.color.white),
                text = "Home",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
