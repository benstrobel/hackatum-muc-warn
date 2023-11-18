package com.example.muc_warn.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WarningCard(title: String, subtitle: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.Red.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) // Red background with opacity
                .border(1.dp, Color.Red, shape = RoundedCornerShape(8.dp)) // Red border
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Info, // Replace with your icon resource
                contentDescription = null, // Provide a content description for accessibility
                modifier = Modifier.size(30.dp) // Adjust the size of the icon as needed
            )

            Spacer(modifier = Modifier.width(8.dp)) // Add some space between the icon and text

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    color = Color.White, // Text color
                    fontWeight = FontWeight.Bold, // Text style
                )

                Text(
                    text = subtitle,
                    color = Color.White, // Text color
                    fontWeight = FontWeight.Normal, // Text style
                )
            }
        }
    }
}