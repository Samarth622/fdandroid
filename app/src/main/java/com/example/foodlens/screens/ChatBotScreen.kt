import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodlens.R
import com.example.foodlens.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun ChatBotScreen(viewModel: UserViewModel) {

    val hasShownGreeting = viewModel.hasShownGreeting // Track if greeting has been shown

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier
                .padding(end = 30.dp)
                .size(70.dp)
                .clip(RoundedCornerShape(50))
                .border(2.dp,Color.Black, RoundedCornerShape(50))
                .clickable {
                    openChatTab(context)
                },
            colors = CardDefaults.cardColors(colorResource(R.color.lightGreen)),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.chat),
                contentDescription = "Chat Bot",
                modifier = Modifier
                    .padding()
            )
        }

        if (!hasShownGreeting) {
            Text(
                text = "Hi, How Can I Help You?",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 70.dp, end = 10.dp)
                    .clip(RoundedCornerShape(20))
                    .background(Color.LightGray)
            )

            LaunchedEffect(Unit) {
                delay(3000)
                viewModel.markGreetingAsShown()
            }
        }
    }
}

fun openChatTab(context: Context) {
    val url = "https://www.jotform.com/app/250591641166457"
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}


