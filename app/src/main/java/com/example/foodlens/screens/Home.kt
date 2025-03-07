package com.example.foodlens.screens

import ChatBotScreen
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodlens.FloatingBottomNavigation
import com.example.foodlens.R
import com.example.foodlens.UserViewModel
import kotlinx.coroutines.delay


@Composable
fun Home(navHostController: NavHostController, viewModel: UserViewModel) {

    val context = LocalContext.current

    val category = listOf(
        Pair(R.drawable.beverages, "Beverages"),
        Pair(R.drawable.biscuits, "Biscuits"),
        Pair(R.drawable.bread, "Breads & Toast"),
        Pair(R.drawable.cereals, "Cereals"),
        Pair(R.drawable.chocolates, "Chocolates"),
        Pair(R.drawable.dairyproducts, "Dairy Products"),
        Pair(R.drawable.icecream, "Ice Cream"),
        Pair(R.drawable.nutbars, "Nutbars"),
        Pair(R.drawable.oilandghee, "Oil & Ghee"),
        Pair(R.drawable.sweetandcake, "Sweets & Cakes"),
        Pair(R.drawable.snackstemplate, "Snacks"),
        Pair(R.drawable.spices, "Spices & Seasonings"),
        Pair(R.drawable.vegetable, "Vegetable"),
        Pair(R.drawable.spreadandsauces, "Spreads & Sauces"),
        Pair(R.drawable.wheat, "Grains"),
        Pair(R.drawable.noodleaandpasta, "Noodles & Pasta"),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(30.dp),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp,start = 15.dp, end = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Welcome",
                            modifier = Modifier.padding(horizontal = 30.dp),
                            color = Color(54, 54, 54, 191),
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Normal)
                        )
                        Text(
                            text = "healthy insights",
                            modifier = Modifier.padding(horizontal = 30.dp),
                        )
                    }

                }
            }
            item {
                Carousel()
            }

            item {
                Text(
                    text = "Healthy Food Choice",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(54, 54, 54, 191),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal)
                )

            }
            items(category.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowItems.forEach { (imageRes, title) ->

                        HomeCategoryItem(
                            viewModel,
                            navHostController,
                            cat = title,
                            drawable = imageRes
                        )
                    }
                    // If the last row has only one item, add an empty spacer
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }

        }

        ChatBotScreen(viewModel)

        FloatingBottomNavigation(navHostController)


        ExitDialogBox(context)

    }


}


@Composable
fun ExitDialogBox(context: Context) {

    val activity = rememberUpdatedState(newValue = context as? Activity)

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }


    if (showExitDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit App", color = Color(1, 1, 1)) },
            text = { Text("Are you sure you want to exit?", color = Color(1, 1, 1)) },
            confirmButton = {

                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = {
                        showExitDialog = false

                        (context as? Activity)?.finish()

//                    activity.value?.finish()    Exit app
                    }) {
                    Text("Yes")
                }
            },
            dismissButton = {

                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

}


@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeCategoryItem(
    viewModel: UserViewModel,
    navController: NavController,
    cat: String,
    drawable: Int
) {
    val currentRoute = CurrentRoute(navController)

    Card(
        modifier = Modifier
            .clickable {
                viewModel.setCategory(cat)
                navController.navigate("loadingPage")

            }
            .size(180.dp)
            .padding(top = 20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = drawable), contentDescription = cat,
                contentScale = ContentScale.Fit,
                modifier = Modifier.scale(1.5f).size(100.dp)
            )
            Text(text = cat, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
    }
}


@Composable
fun Carousel() {

    val images = listOf(
        R.drawable.carousel1,
        R.drawable.carousel1,
        R.drawable.carousel1,
        R.drawable.carousel1
    )

    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L) // Auto-scroll every 2 seconds
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Carousel Image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }

}


@Composable
fun HomeTopBar(title: String, icon: ImageVector, navHostController: NavHostController) {

    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.lightGreen)),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(40.dp),
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(.7f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = {
                        if (icon == Icons.Default.AccountCircle) {
                            navHostController.navigate("profile")
                        } else {
                            navHostController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(color = colorResource(R.color.white))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = colorResource(R.color.green)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = title,
                    color = Color(70, 66, 66, 193),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
                )

            }
        }


    }
}

@Composable
fun CurrentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


@Composable
fun LoadingScreen(navController: NavController) {

    LaunchedEffect(Unit) {

        navController.navigate("categoriesPage") {
            popUpTo("loadingPage") { inclusive = true } // Remove loading screen from back stack
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BackHandler {
            navController.navigate("home")
        }
        CircularProgressIndicator()
    }
}
