package com.example.muc_warn.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import com.example.muc_warn.views.OnlineView
import java.util.Date

@Composable
fun AlertCard(alert: Alert){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(8.dp)) // Red background with opacity
                .border(1.dp, Color(0xFF941A2E), shape = RoundedCornerShape(8.dp)) // Red border
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Rounded.Warning, // Replace with your icon resource
                contentDescription = null, // Provide a content description for accessibility
                modifier = Modifier.size(30.dp) // Adjust the size of the icon as needed
            )

            Spacer(modifier = Modifier.width(8.dp)) // Add some space between the icon and text

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Test",
                    color = Color.White, // Text color
                    fontWeight = FontWeight.Bold, // Text style
                )

                Text(
                    text = "T2",
                    color = Color.White, // Text color
                    fontWeight = FontWeight.Normal, // Text style
                )
            }
        }
    }
}

@Preview(
    name = "Dark Mode",
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    name = "Light Mode",
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun AlertCardPreview() {
    val alert = Alert(
        title = "Amtliche Warnung vor Gewitter",
        description = "Es wird erwartet, dass ein starkes Gewitter die Region um Ludwigsstraße 3 in München in den nächsten Stunden erreichen wird. Es besteht die Möglichkeit von heftigem Regen, starken Windböen und Blitzschlägen. Bitte nehmen Sie Schutzmaßnahmen, insbesondere im Freien, um Ihre Sicherheit zu gewährleisten. Vermeiden Sie offene Flächen und suchen Sie geschützte Bereiche auf.",
        threadLevel = 1,
        postDate = Date(),
        expireDate = Date(),
        location = Location(40.7128, -74.0060),
        locationString = "Ludwigsstraße 3, München"
    )

    AlertCard(alert = alert)
}