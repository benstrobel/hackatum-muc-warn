package com.example.muc_warn.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muc_warn.components.AlertCard
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.InternetConnectionChecker
import com.example.muc_warn.components.WarningCard
import com.example.muc_warn.models.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun InfoView(navController: NavController, viewModel: NavigationViewModel) {

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
        Text("InfoView")
    }
}