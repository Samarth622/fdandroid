package com.example.foodlens.screens

import ChatBotScreen
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.res.stringResource
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
import java.util.Locale

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
    val preferences = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    // Load the saved language, defaulting to "English" if not set
    var selectedLanguage by remember { mutableStateOf(preferences.getString("language", "English") ?: "English") }

    // Function to update locale and recreate activity
    fun updateLocale(language: String) {
        val locale = if (language == "Hindi") Locale("hi") else Locale("en")
        val languageCode = if (language == "Hindi") "hi" else "en"
        Locale.setDefault(locale)
        val config = Configuration().apply { setLocale(locale) }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        preferences.edit()
            .putString("language", language) // Store the display name (e.g., "English" or "Hindi")
            .putString("language_code", languageCode) // Store the code for consistency
            .apply()
        (context as? Activity)?.recreate() // Recreate activity to apply language change
    }

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
                    Toast.makeText(context,R.string.failed_fetch_profile, Toast.LENGTH_SHORT).show()
                    if (response.code() == 401) {
                        navHostController.navigate("loginPage") { popUpTo(0) { inclusive = true } }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
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
                val response = apiService.updateUserProfile(request)
                if (response.isSuccessful) {
                    Toast.makeText(context, R.string.profile_updated, Toast.LENGTH_SHORT).show()
                    isEditing = false
                } else {
                    Toast.makeText(context, R.string.failed_update_profile, Toast.LENGTH_SHORT).show()
                    if (response.code() == 401) {
                        navHostController.navigate("loginPage") { popUpTo(0) { inclusive = true } }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
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
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LanguageDropdown(
                                selectedLanguage = selectedLanguage,
                                onLanguageSelected = { newLanguage ->
                                    if (selectedLanguage != newLanguage) {
                                        selectedLanguage = newLanguage
                                        updateLocale(newLanguage)
                                    }
                                }
                            )
                            Text(
                                text = stringResource(R.string.profile),
                                color = Color(54, 54, 54, 191),
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            LogoutButton(navHostController, context)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileField(stringResource(R.string.name), name, { name = it }, isEditing, Icons.Default.Person)
                        ProfileField(stringResource(R.string.mobile_no), mobile, { mobile = it }, isEditing, Icons.Default.Phone)
                        ProfileField(stringResource(R.string.gender), gender, { gender = it }, isEditing, Icons.Default.Lock)
                        ProfileField(stringResource(R.string.email), email, { email = it }, isEditing, Icons.Default.Email)
                        ProfileField(stringResource(R.string.age), age, { age = it }, isEditing, Icons.Default.Info)
                        ProfileField(stringResource(R.string.height_cm), height, { height = it }, isEditing, Icons.Default.Info)
                        ProfileField(stringResource(R.string.weight_kg), weight, { weight = it }, isEditing, Icons.Default.Info)
                        ProfileField(stringResource(R.string.medical_history), medicalHistory, { medicalHistory = it }, isEditing, Icons.Default.Info)
                        ProfileField(stringResource(R.string.allergies), allergies, { allergies = it }, isEditing, Icons.Default.Warning)
                        ProfileField(stringResource(R.string.blood_group), bloodGroup, { bloodGroup = it }, isEditing, Icons.Default.Info)

                        Spacer(modifier = Modifier.height(15.dp))

                        Button(
                            onClick = { if (isEditing) saveProfile() else isEditing = true },
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .padding(top = 25.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = if (!isEditing) ButtonDefaults.buttonColors(Color.Gray) else ButtonDefaults.buttonColors(Color.Black)
                        ) {
                            Text(
                                text = stringResource(if (isEditing) R.string.save else R.string.edit),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }

        ChatBotScreen(viewModel = viewModel)
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
                    keyboardType = if (label == stringResource(R.string.age) || label == stringResource(R.string.height_cm) || label == stringResource(R.string.weight_kg)) KeyboardType.Number else KeyboardType.Text
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
        modifier = Modifier.padding()
    ) {
        Icon(
            painter = painterResource(R.drawable.logout),
            contentDescription = stringResource(R.string.logout),
            tint = Color.Gray,
            modifier = Modifier.scale(1.2f)
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout), color = Color.Black) },
            text = { Text(stringResource(R.string.are_you_sure_logout), color = Color.Black) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = {
                        showLogoutDialog = false
                        val sharedPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        with(sharedPrefs.edit()) {
                            putBoolean("isLoggedIn", false)
                            remove("AUTH_TOKEN")
                            apply()
                        }
                        RetrofitClient.setToken(null)
                        navHostController.navigate("loginPage") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}

@Composable
fun LanguageDropdown(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languageOptions = listOf(stringResource(R.string.english), stringResource(R.string.hindi))
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(120.dp) // Increased width to ensure space for text and icon
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp), // Adjusted padding
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedLanguage,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f, fill = false) // Prevent text from pushing icon out
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp) // Fixed size for consistency
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .width(200.dp)
            ) {
                languageOptions.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language, color = Color.Black, fontSize = 18.sp) },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}