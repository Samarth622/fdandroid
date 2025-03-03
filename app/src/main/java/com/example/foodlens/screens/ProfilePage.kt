package com.example.foodlens.screens

import ChatBotScreen
import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodlens.FloatingBottomNavigation
import com.example.foodlens.R
import com.example.foodlens.UserViewModel

@Composable
fun ProfilePage(navHostController: NavHostController,viewModel: UserViewModel) {

    var isEditing by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("John Doe") }
    var mobile by remember { mutableStateOf("9876543210") }
    var gender by remember { mutableStateOf("Male") }
    var email by remember { mutableStateOf("john@example.com") }
    var age by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("170 cm") }
    var weight by remember { mutableStateOf("60 kg") }
    var medicalHistory by remember { mutableStateOf("No history") }
    var allergies by remember { mutableStateOf("None") }
    var bloodGroup by remember { mutableStateOf("O+") }

    val context = LocalContext.current
    val userMobileNO = getCurrentUser(context = context )



    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 30.dp),
        contentAlignment = Alignment.Center){

        Image(
            painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn (modifier = Modifier.fillMaxSize().padding(bottom = 40.dp)){
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically){

                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center){
                            Text(
                                text = "Profile",
                                color = Color(54, 54, 54, 191),
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            LogoutButton(
                                navHostController,context
                            )
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
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = if (!isEditing) ButtonDefaults.buttonColors(Color.Gray) else ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Text(text = if (isEditing) "Save" else "Edit",
                            color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        ChatBotScreen(viewModel = viewModel )

        FloatingBottomNavigation(navHostController)
    }


}

fun getCurrentUser(context: Context): String? {
    val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    return sharedPref.getString("LOGGED_IN_USER", null)
}


@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit, isEditing: Boolean, icon: ImageVector, isPassword: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))

        if (isEditing) {
            OutlinedTextField(
                value = value,
                textStyle = TextStyle.Default.copy(color=Color.Black, fontSize = 16.sp),
                onValueChange = onValueChange,
                leadingIcon = { Icon(icon, contentDescription = label,tint = Color.Gray, modifier = Modifier.padding(start=7.dp)) },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Black
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
fun LogoutButton(navHostController: NavHostController,context: Context) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showLogoutDialog=true
    }, modifier = Modifier.padding(start = 290.dp)) {
        Icon(painter = painterResource(R.drawable.logout),
            contentDescription = "logout Button",
            tint = Color.Gray,
            modifier = Modifier.scale(1.2f))
    }

    if (showLogoutDialog) {
        AlertDialog(
            containerColor =Color.White,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = Color(1,1,1)) },
            text = { Text("Are you sure you want to Logout?", color = Color(1,1,1)) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = {
                        showLogoutDialog = false

                        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("isLoggedIn", false).apply()

                        navHostController.navigate("loginPage"){
                            popUpTo(0)
                        }
                    }) {
                    Text("Yes")
                }
            },
            dismissButton = {

                Button(
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.lightGreen)),
                    onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }


}