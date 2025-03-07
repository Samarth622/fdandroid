package com.example.foodlens.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodlens.R
import com.example.foodlens.networks.LoginApiService
import com.example.foodlens.networks.LoginRequest
import com.example.foodlens.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginPage(navHostController: NavHostController) {
    var mobileNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService: LoginApiService = RetrofitClient.getApiService(context)
    val preferences = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    // Load the saved language (set in GetStarted)
    val selectedLanguage = preferences.getString("language", "English") ?: "English"

    Image(
        painter = painterResource(R.drawable.background2),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(160.dp))

        Text(
            text = stringResource(R.string.welcome_back),
            color = Color(70, 66, 66, 193),
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Normal),
        )

        Text(
            text = stringResource(R.string.sign_in_to_continue),
            color = Color(70, 66, 66, 79),
            fontSize = 20.sp,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(40.dp))

        TransparentTextField(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            placeholder = stringResource(R.string.mobile_no),
            isNumberKeyboard = true,
            icon = Icons.Default.AccountBox
        )

        TransparentTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(R.string.password),
            icon = Icons.Default.Lock
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(.8f)
                .background(colorResource(R.color.green), CircleShape),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            onClick = {
                if (mobileNo.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, R.string.incomplete_credentials, Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val request = LoginRequest(mobileNo, password)
                            val response = apiService.loginUser(request)

                            if (response.isSuccessful) {
                                response.body()?.let { body ->
                                    body.token?.let { token ->
                                        // Store token
                                        RetrofitClient.setToken(token)
                                        saveToken(context, token)
                                        saveCurrentUser(context, mobileNo)

                                        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                                            .edit().putBoolean("isLoggedIn", true).apply()

                                        Toast.makeText(context, body.message ?: "login successful", Toast.LENGTH_SHORT).show()
                                        navHostController.navigate("home") {
                                            popUpTo(0) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, response.errorBody()?.string() ?: "login failed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }
        ) {
            Text(
                text = stringResource(R.string.login),
                fontSize = 19.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = { /* TODO: Handle forgot password */ }) {
            Text(
                text = stringResource(R.string.forget_password),
                color = colorResource(R.color.green)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.do_not_have_account),
                color = Color(1, 1, 1, 122)
            )

            TextButton(
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    navHostController.navigate("register") {
                        popUpTo("loginPage") { inclusive = true }
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.register),
                    color = colorResource(R.color.green)
                )
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

fun saveToken(context: Context, token: String) {
    val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPref.edit().putString("AUTH_TOKEN", token).apply()
}

fun saveCurrentUser(context: Context, mobileNo: String) {
    val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPref.edit().putString("LOGGED_IN_USER", mobileNo).apply()
}

@Composable
fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isNumberKeyboard: Boolean = false,
    isPassword: Boolean = false,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(10.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = Color.Gray, fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumberKeyboard) KeyboardType.Number else KeyboardType.Text
            ),
            decorationBox = { innerTextField ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(text = placeholder, color = Color.Gray, fontSize = 16.sp)
                            }
                            innerTextField()
                        }
                    }
                    Divider()
                }
            }
        )
    }
}