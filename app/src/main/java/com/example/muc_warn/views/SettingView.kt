package com.example.muc_warn.views

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muc_warn.components.BottomBar.BottomNavBar
import com.example.muc_warn.components.BottomBar.Screen
import com.example.muc_warn.components.IndicatorTopBar
import com.example.muc_warn.components.InternetConnectionChecker
import com.example.muc_warn.components.WarningCard
import com.example.muc_warn.models.NavigationViewModel


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun SettingView(navController: NavController, viewModel: NavigationViewModel) {
    val context: Context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var text: MutableState<String> = mutableStateOf(getCertificate(context = context))

    Scaffold(
        topBar = {
            IndicatorTopBar(isNetworkAvailable = viewModel.isNetworkAvailable, title = "PeerGuard")
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentScreenId = viewModel.currentScreen.value.id,
                onItemSelected = {viewModel.currentScreen.value = it},
                viewModel = viewModel
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text.value,
                    onValueChange = { text.value = it },
                    label = { Text("Certificate") },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        saveCertificate(context = context, certificate = text.value);
                        keyboardController?.hide();
                        if(validateCertificate(getCertificate(context))) {
                            viewModel.mutableListItems = Screen.Items.listValidated.toMutableList()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor =  MaterialTheme.colorScheme.primary, contentColor = Color.White)
                ) {
                    Text("Upload Certificate")
                }
            }

        }
    }
}

fun validateCertificate(text: String): Boolean {
    return text.length > 10;
}

fun saveCertificate(context: Context, certificate: String) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()
    editor.putString("user_certificate", certificate)
    editor.apply()
}

fun getCertificate(context: Context): String {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val certificate = sharedPreferences.getString("user_certificate", null)
    return certificate ?: ""
}
