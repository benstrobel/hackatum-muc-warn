package com.example.muc_warn.views

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.InternetConnectionChecker
import com.example.muc_warn.models.NavigationViewModel


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingView(navController: NavController, viewModel: NavigationViewModel) {
    //var text: MutableState<String> = mutableStateOf("")

    Scaffold(
        topBar = {
            IndicatorTopBar(isNetworkAvailable = viewModel.isNetworkAvailable, title = "PeerGuard")
        },
        bottomBar = {
            BottomNavBar(navController = navController, currentScreenId = viewModel.currentScreen.value.id,
                onItemSelected = {viewModel.currentScreen.value = it})
        }
    ) { innerPadding ->
        InternetConnectionChecker(viewModel.isNetworkAvailable, innerPadding, viewModel = viewModel)
        // Use a LazyColumn for better performance
        /*Column {
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                label = { Text("Label") }
            )
        }*/
        Text("Hi")
    }
}