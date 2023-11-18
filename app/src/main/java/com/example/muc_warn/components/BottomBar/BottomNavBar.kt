package com.example.muc_warn.components.BottomBar

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muc_warn.models.NavigationViewModel
import com.example.muc_warn.views.validateCertificate

@ExperimentalAnimationApi
@Composable
fun BottomNavBar(
    navController: NavController,
    currentScreenId: String,
    onItemSelected: (Screen) -> Unit,
    viewModel: NavigationViewModel
) {
    val context: Context = LocalContext.current

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        viewModel.mutableListItems.forEach { item ->
            CustomBottomNavigationItem(item = item, isSelected = item.id == currentScreenId) {
                onItemSelected(item)
                navController.navigate(item.id)
            }
        }
    }
}

fun hasPermission(context: Context): Boolean {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val certificate = sharedPreferences.getString("user_certificate", null)
    return validateCertificate(certificate ?: "")
}

@ExperimentalAnimationApi
@Composable
fun CustomBottomNavigationItem(item: Screen, isSelected: Boolean, onClick: () -> Unit) {
    val background = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = Color.Black

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = item.icon, contentDescription = null, tint = contentColor)

            AnimatedVisibility(visible = isSelected) {
                Text(text = item.title, color = contentColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}