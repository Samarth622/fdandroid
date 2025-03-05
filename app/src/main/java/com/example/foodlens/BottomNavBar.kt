package com.example.foodlens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun FloatingBottomNavigation(navHostController: NavHostController) {
    val items = listOf("home", "search", "suggestion", "profile",)
    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.Notifications, Icons.Default.Person)

    // Track current destination
    val currentDestination by navHostController.currentBackStackEntryAsState()
    val selectedItem = items.indexOf(currentDestination?.destination?.route)

    Box(
        modifier = Modifier
            .fillMaxSize().padding(bottom = 15.dp, start = 10.dp, end = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, route ->
                    IconButton(
                        onClick = {
                            if (route != currentDestination?.destination?.route) {
                                navHostController.navigate(route) {
                                    popUpTo(navHostController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                if (selectedItem == index) Color(183, 181, 181, 119) else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = icons[index],
                            modifier = Modifier.scale(1.4f),
                            contentDescription = route,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
