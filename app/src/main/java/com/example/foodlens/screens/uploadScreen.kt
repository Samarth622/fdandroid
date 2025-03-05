package com.example.foodlens.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.foodlens.FloatingBottomNavigation
import com.example.foodlens.R
import com.example.foodlens.UserViewModel
import com.example.foodlens.network.RetrofitClient
import com.example.foodlens.networks.LoginApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import android.widget.Toast
import kotlinx.coroutines.launch

@Composable
fun UploadScreen(navHostController: NavHostController, viewModel: UserViewModel) {
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

        Heading()

        ImageField()

        UploadImageType(navHostController)

        FloatingBottomNavigation(navHostController)
    }
}

@Composable
fun Heading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Upload & Analyze",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "Just in a click", fontSize = 20.sp)
                Image(
                    painter = painterResource(R.drawable.uploadfor),
                    contentDescription = "camera",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun ImageField() {
    Box(
        modifier = Modifier
            .fillMaxSize().
        padding(top = 190.dp, start = 48.dp, bottom = 30.dp ),
    ) {
        Card(
            modifier = Modifier.size(300.dp),
            elevation = CardDefaults.cardElevation(1.dp),
            colors = CardDefaults.cardColors(Color(232, 246, 253, 255))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Image")
            }
        }
    }
}

@Composable
fun UploadImageType(navHostController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService: LoginApiService = RetrofitClient.getApiService(context)

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                capturedImageUri?.let { uri ->
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        Log.d("UploadScreen", "Image captured from camera: $uri")
                    } catch (e: Exception) {
                        Log.e("UploadScreen", "Error loading camera image: ${e.message}")
                        Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                capturedImageUri = it
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    Log.d("UploadScreen", "Image selected from gallery: $uri")
                } catch (e: Exception) {
                    Log.e("UploadScreen", "Error loading gallery image: ${e.message}")
                    Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 150.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        imageBitmap?.let { bitmap ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 205.dp)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(220.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAnalyzing = true
                            try {
                                // Convert Bitmap to File
                                val file = File(context.cacheDir, "upload_image.jpg")
                                FileOutputStream(file).use { out ->
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                }
                                Log.d("UploadScreen", "Image file created: ${file.absolutePath}, size: ${file.length()} bytes")

                                // Create MultipartBody.Part for image upload
                                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                                // Send image to backend
                                val response = apiService.analyzeImage(imagePart)
                                Log.d("UploadScreen", "Response code: ${response.code()}")
                                if (response.isSuccessful) {
                                    response.body()?.let { analysisResponse ->
                                        val analysisJson = Gson().toJson(analysisResponse)
                                        navHostController.navigate("imageAnalysisPage/$analysisJson")
                                    } ?: Toast.makeText(context, "No analysis data received", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e("UploadScreen", "Analysis failed with code: ${response.code()}, message: ${response.message()}")
                                    Toast.makeText(context, "Analysis failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    if (response.code() == 401) {
                                        navHostController.navigate("loginPage") { popUpTo(0) { inclusive = true } }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("UploadScreen", "Error during analysis: ${e.localizedMessage}")
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isAnalyzing = false
                            }
                        }
                    },
                    modifier = Modifier.width(120.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green)),
                    enabled = !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = "Analyze",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            val file = File(context.cacheDir, "captured_image.jpg")
                            capturedImageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                                putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
                            }
                            cameraLauncher.launch(intent)
                        },
                    colors = CardDefaults.cardColors(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(R.drawable.photography),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentDescription = "Take photo"
                    )
                }
                Text(text = "Take Photo", fontWeight = FontWeight.SemiBold)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            galleryLauncher.launch("image/*")
                        },
                    colors = CardDefaults.cardColors(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(R.drawable.gallery),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentDescription = "Gallery"
                    )
                }
                Text(text = "From Gallery", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}