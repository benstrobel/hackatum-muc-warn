package com.example.muc_warn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.muc_warn.components.BottomBar.Screen
import com.example.muc_warn.models.NavigationViewModel
import com.example.muc_warn.ui.theme.MucWarnTheme
import com.example.muc_warn.views.InfoView
import com.example.muc_warn.views.MainView
import com.example.muc_warn.views.SettingView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MucWarnTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navigationViewModel = NavigationViewModel()

                    NavHost(navController = navController, startDestination = "warnings" ) {
                        composable("warnings") {
                            MainView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("info") {
                            InfoView(navController = navController, viewModel = navigationViewModel)
                        }
                        composable("settings") {
                            SettingView(navController = navController, viewModel = navigationViewModel)
                        }
                    }
                }
            }
        }
    }
}