package com.example.muc_warn.components.BottomBar


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val id: String,
    val title: String,
    val icon: ImageVector
) {
    object Warnings: Screen("warnings", "Warning", Icons.Outlined.Warning)
    object Info: Screen("info", "Infomation", Icons.Outlined.Info)
    object Settings: Screen("settings", "Settings", Icons.Outlined.Settings)

    object Items {
        val list = listOf(
            Warnings, Info, Settings
        )
    }
}