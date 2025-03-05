package com.example.foodlens.screens

import ChatBotScreen
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodlens.FloatingBottomNavigation
import com.example.foodlens.R
import com.example.foodlens.UserViewModel
import com.example.foodlens.network.RetrofitClient
import com.example.foodlens.networks.LoginApiService
import com.example.foodlens.networks.UpdateProfileRequest
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(navHostController: NavHostController, viewModel: UserViewModel) {
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService: LoginApiService = RetrofitClient.getApiService(context)

    // Fetch profile data on load
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = apiService.getUserProfile()
                if (response.isSuccessful) {
                    response.body()?.let { profileResponse ->
                        val profile = profileResponse.User
                        name = profile.name ?: ""
                        mobile = profile.mobile ?: ""
                        gender = profile.gender ?: ""
                        email = profile.email ?: ""
                        age = profile.age?.toString() ?: ""
                        height = profile.height?.toString() ?: ""
                        weight = profile.weight?.toString() ?: ""
                        medicalHistory = profile.medicalHistory ?: ""
                        allergies = profile.allergies ?: ""
                        bloodGroup = profile.bloodGroup ?: ""
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch profile: ${response.code()}", Toast.LENGTH_SHORT).show()
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

    // Function to save profile updates
    fun saveProfile() {
        coroutineScope.launch {
            try {
                val request = UpdateProfileRequest(
                    name = name.takeIf { it.isNotBlank() },
                    mobile = mobile.takeIf { it.isNotBlank() },
                    gender = gender.takeIf { it.isNotBlank() },
                    email = email.takeIf { it.isNotBlank() },
                    age = age.toIntOrNull(),
                    height = height.toIntOrNull(),
                    weight = weight.toIntOrNull(),
                    medicalHistory = medicalHistory.takeIf { it.isNotBlank() },
                    allergies = allergies.takeIf { it.isNotBlank() },
                    bloodGroup = bloodGroup.takeIf { it.isNotBlank() }
                )
                val response = apiService.updateUserProfile(request) // Use apiService instance
                if (response.isSuccessful) {
                    Toast.makeText(context, response.body()?.message ?: "Profile updated", Toast.LENGTH_SHORT).show()
                    isEditing = false
                } else {
                    Toast.makeText(context, "Failed to update profile: ${response.code()}", Toast.LENGTH_SHORT).show()
                    if (response.code() == 401) {
                        navHostController.navigate("loginPage") { popUpTo(0) { inclusive = true } }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Profile",
                                    color = Color(54, 54, 54, 191),
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                LogoutButton(navHostController, context)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileField("Name", name, { name = it }, isEditing, Icons.Default.Person)
                        ProfileField("Mobile No", mobile, { mobile = it }, isEditing, Icons.Default.Phone)
                        ProfileField("Gender", gender, { gender = it }, isEditing, Icons.Default.Lock)
                        ProfileField("Email", email, { email = it }, isEditing, Icons.Default.Email)
                        ProfileField("Age", age, { age = it }, isEditing, Icons.Default.Info)
                        ProfileField("Height(cm)", height, { height = it }, isEditing, Icons.Default.Info)
                        ProfileField("Weight(kg)", weight, { weight = it }, isEditing, Icons.Default.Info)
                        ProfileField("Medical History", medicalHistory, { medicalHistory = it }, isEditing, Icons.Default.Info)
                        ProfileField("Allergies", allergies, { allergies = it }, isEditing, Icons.Default.Warning)
                        ProfileField("Blood Group", bloodGroup, { bloodGroup = it }, isEditing, Icons.Default.Info)

                        Spacer(modifier = Modifier.height(15.dp))

                        Button(
                            onClick = { if (isEditing) saveProfile() else isEditing = true },
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .padding(bottom = 25.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = if (!isEditing) ButtonDefaults.buttonColors(Color.Gray) else ButtonDefaults.buttonColors(Color.Black)
                        ) {
                            Text(
                                text = if (isEditing) "Save" else "Edit",
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }

        ChatBotScreen(viewModel = viewModel )
        FloatingBottomNavigation(navHostController)
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))

        if (isEditing) {
            OutlinedTextField(
                value = value,
                textStyle = TextStyle.Default.copy(color = Color.Black, fontSize = 16.sp),
                onValueChange = onValueChange,
                leadingIcon = { Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.padding(start = 7.dp)) },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (label == "Age" || label == "Height(cm)" || label == "Weight(kg)") KeyboardType.Number else KeyboardType.Text
                )
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = label, tint = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutButton(navHostController: NavHostController, context: Context) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showLogoutDialog = true },
        modifier = Modifier.padding(start = 290.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.logout),
            contentDescription = "Logout Button",
            tint = Color.Gray,
            modifier = Modifier.scale(1.2f)
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = Color(1, 1, 1)) },
            text = { Text("Are you sure you want to logout?", color = Color(1, 1, 1)) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = {
                        showLogoutDialog = false
                        // Clear SharedPreferences data (token and login status)
                        val sharedPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        with(sharedPrefs.edit()) {
                            putBoolean("isLoggedIn", false)
                            remove("AUTH_TOKEN") // Remove the token
                            apply()
                        }
                        // Optionally clear RetrofitClient's in-memory token if set
                        RetrofitClient.setToken(null)
                        // Navigate to login page
                        navHostController.navigate("loginPage") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}