package com.example.muc_warn.views

import android.widget.DatePicker
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muc_warn.busineslogic.PeerToPeerManager
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.BottomBar.Screen
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.InternetConnectionChecker
import com.example.muc_warn.components.WarningCard
import com.example.muc_warn.models.CreateViewModel
import com.example.muc_warn.models.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CreateAlertView(
    navController: NavController,
    viewModel: NavigationViewModel,
    p2p: PeerToPeerManager
) {

    var createViewModel = CreateViewModel()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            IndicatorTopBar(isNetworkAvailable = viewModel.isNetworkAvailable, title = "PeerGuard")
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentScreenId = viewModel.currentScreen.value.id,
                onItemSelected = { viewModel.currentScreen.value = it },

            )
        }
    ) { innerPadding ->
        InternetConnectionChecker(viewModel.isNetworkAvailable, innerPadding, viewModel = viewModel)
        // Use a LazyColumn for better performance
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

            item {
                Text(text = "Create Alert", modifier = Modifier.padding(8.dp), color = Color.Black)
            }

            item {
                OutlinedTextField(
                    value = createViewModel.alert.value.senderName,
                    onValueChange = { createViewModel.onSenderNameChange(it) },
                    label = { Text("Sender Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = createViewModel.alert.value.title,
                    onValueChange = { createViewModel.onTitleChange(it) },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = createViewModel.alert.value.description,
                    onValueChange = { createViewModel.onDescriptionChange(it) },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = createViewModel.alert.value.locationString,
                    onValueChange = { createViewModel.onLocationStrinChange(it) },
                    label = { Text("Location") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Thread Level", color = Color.Black)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Slider(
                        value = createViewModel.alert.value.threadLevel.toFloat(),
                        onValueChange = {createViewModel.onThreadLvlChange(it.toInt())},
                        valueRange = 1f..5f,
                        steps = 1,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        p2p.addToAlertsToShare(createViewModel.alert.value)
                        keyboardController?.hide()
                        navController.navigate(
                            if (createViewModel.alert.value.threadLevel != 0) {
                                "warnings"
                            } else {
                                "info"
                            })
                        viewModel.currentScreen.value = if (createViewModel.alert.value.threadLevel != 0) {
                            Screen.Warnings
                        } else {
                            Screen.Info
                        }
                    }, modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Send Alert")
                }
            }
        }
    }
}