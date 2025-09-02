// In MainActivity.kt

package com.multibank.stocktracker // Your new package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // <-- IMPORT THIS
import com.multibank.stocktracker.ui.PriceTrackerScreen
import com.multibank.stocktracker.ui.theme.StockTrackerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // VVV ADD THESE LINES VVV
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen on-screen for a little longer if needed
        // For a real app, you might wait for data loading or API calls here
        splashScreen.setKeepOnScreenCondition {
            // Here you can put your actual loading condition (e.g., viewModel.isLoading)
            // For now, we'll just simulate a delay.
            // Be careful not to block the UI thread too long in a real app.
            runBlocking {
                delay(1000) // Keep splash screen for 1 second
            }
            false // When false, the splash screen will dismiss
        }
        // ^^^ END SPLASH SCREEN CODE ^^^

        setContent {
            StockTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PriceTrackerScreen()
                }
            }
        }
    }
}