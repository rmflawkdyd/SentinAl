package com.example.sentinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sentinal.presentation.root.AppEntryScreen
import com.example.sentinal.ui.theme.SentinATheme
import com.example.sentinal.ui.theme.SentinAISystemBarTransparent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = SentinAISystemBarTransparent,
                darkScrim = SentinAISystemBarTransparent,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = SentinAISystemBarTransparent,
                darkScrim = SentinAISystemBarTransparent,
            ),
        )
        setContent {
            SentinATheme {
                AppEntryScreen()
            }
        }
    }
}
