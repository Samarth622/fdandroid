package com.example.foodlens

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChatbotWebView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                // Enable JavaScript
                settings.javaScriptEnabled = true

                // Optional: Additional WebView settings
                settings.domStorageEnabled = true
                settings.allowContentAccess = true
                settings.allowFileAccess = true

                // Load the HTML content with the chatbot script
                loadDataWithBaseURL(
                    "https://www.jotform.com",
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body>
                        <div id="JotformAgent-019550c8ae607051b84274ec80cf96039cb3"></div>
                        <script src="https://cdn.jotfor.ms/s/umd/latest/for-embedded-agent.js"></script>
                        <script>
                            window.addEventListener("DOMContentLoaded", function () {
                                window.AgentInitializer.init({
                                    agentRenderURL: "https://agent.jotform.com/019550c8ae607051b84274ec80cf96039cb3",
                                    rootId: "JotformAgent-019550c8ae607051b84274ec80cf96039cb3",
                                    formID: "019550c8ae607051b84274ec80cf96039cb3",
                                    queryParams: ["skipWelcome=1", "maximizable=1"],
                                    domain: "https://www.jotform.com",
                                    isDraggable: false,
                                    background: "linear-gradient(180deg, #21bf73 0%, #21bf73 100%)",
                                    buttonBackgroundColor: "#21bf73",
                                    buttonIconColor: "#FFFFFF",
                                    variant: false,
                                    customizations: {
                                        "greeting": "Yes",
                                        "greetingMessage": "Hi! How can I assist you?",
                                        "pulse": "Yes",
                                        "position": "right"
                                    },
                                    isVoice: false
                                });
                            });
                        </script>
                    </body>
                    </html>
                    """.trimIndent(),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}