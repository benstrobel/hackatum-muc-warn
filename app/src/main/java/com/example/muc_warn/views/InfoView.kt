package com.example.muc_warn.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muc_warn.R
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
            BottomNavBar(
                navController = navController,
                currentScreenId = viewModel.currentScreen.value.id,
                onItemSelected = {viewModel.currentScreen.value = it},

            )
        }
    ) { innerPadding ->
        InternetConnectionChecker(viewModel.isNetworkAvailable, innerPadding, viewModel = viewModel)
        // Use a LazyColumn for better performance
        if(viewModel.alertList.filter { alert -> alert.threadLevel != 0 }.size <= 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painter = painterResource(id = R.drawable.affection), contentDescription = "", tint= Color.DarkGray)
                Spacer(modifier = Modifier.padding(10.dp))
                Text("No Alerts in you region", color = Color.Black)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                if (!viewModel.isNetworkAvailable.value) {
                    item {
                        WarningCard(
                            title = "Achtung",
                            subtitle = "Es konnte keine Verbindung zum Internet aufgebaut werden. Du befindest dich im Notfallmodus."
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                items(viewModel.alertList.filter { alert -> alert.threadLevel == 0 }) { alert ->
                    AlertCard(alert = alert)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}