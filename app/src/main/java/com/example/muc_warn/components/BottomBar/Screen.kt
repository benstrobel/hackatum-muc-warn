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

    object Map: Screen("map", "Map", Icons.Outlined.LocationOn)
    object Create: Screen("create", "Create Alert", Icons.Outlined.Send)
    object Settings: Screen("settings", "Settings", Icons.Outlined.Settings)

    object Items {
        val list = listOf(
            Warnings, Info, Map, Settings
        )

        val listValidated = listOf(
            Warnings, Info, Map, Create, Settings
        )
    }
}