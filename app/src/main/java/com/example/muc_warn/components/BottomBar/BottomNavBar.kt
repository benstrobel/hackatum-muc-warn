package com.example.muc_warn.components.BottomBar

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@ExperimentalAnimationApi
@Composable
fun BottomNavBar(
    navController: NavController,
    currentScreenId: String,
    onItemSelected: (Screen) -> Unit
) {
    val items = Screen.Items.list

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            CustomBottomNavigationItem(item = item, isSelected = item.id == currentScreenId) {
                onItemSelected(item)
                navController.navigate(item.id)
            }
        }
    }
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