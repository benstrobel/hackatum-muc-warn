package com.example.muc_warn.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AlertCard(alert: Alert) {

    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Adjust the format as needed
        return dateFormat.format(date)
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp, getColorBasedOnNumber(
                            number = alert.threadLevel
                        ), shape = CircleShape
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Icon(
                    Icons.Outlined.Warning, // Replace with your icon resource
                    contentDescription = null, // Provide a content description for accessibility
                    modifier = Modifier.size(20.dp),
                    tint = getColorBasedOnNumber(number = alert.threadLevel)
                )
            }

            Spacer(modifier = Modifier.width(20.dp)) // Add some space between the icon and text

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth(),
                    text = alert.senderName,
                    color = Color.Black, // Text color
                    fontWeight = FontWeight.Bold, // Text style
                )

                Text(
                    modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth(),
                    text = formatDate(alert.postDate),
                    color = Color.Gray, // Text color
                    fontWeight = FontWeight.Normal, // Text style
                )
            }
        }

        Spacer( modifier = Modifier.height(20.dp))

        Column(

        ) {
            Text(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(),
                text = alert.title,
                color = Color.Black, // Text color
                fontWeight = FontWeight.Bold, // Text style
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = alert.description,
                color = Color.Black, // Text color
                fontWeight = FontWeight.Normal, // Text style
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Icon(
                    Icons.Outlined.LocationOn, // Replace with your icon resource
                    contentDescription = null, // Provide a content description for accessibility
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth(),
                    text = alert.locationString,
                    color = Color.Gray, // Text color
                    fontWeight = FontWeight.Normal, // Text style
                )
            }
        }
    }
}

@Composable
fun getColorBasedOnNumber(number: Int): Color {
    val colorRange = listOf(
        Color(0xFFF3E6B8),
        Color(0xFFF1C951),
        Color(0xFFEE8053),
        Color(0xFFFF634D),
        Color(0xFF912929),
    )

    // Assuming your numbers range from 0 to 3, adjust the conditions based on your specific range
    return when {
        number < 0 -> Color.Gray
        number == 1 -> colorRange[0]
        number == 2 -> colorRange[1]
        number == 3 -> colorRange[2]
        number == 4 -> colorRange[3]
        else -> colorRange[4]
    }
}

@Preview
@Composable
fun AlertCardPreview() {
    val alert = Alert(
        id = "456",
        senderName = "Lena Müller",
        title = "Amtliche Warnung vor Gewitter",
        description = "Es wird erwartet, dass ein starkes Gewitter die Region um Ludwigsstraße 3 in München in den nächsten Stunden erreichen wird. Es besteht die Möglichkeit von heftigem Regen, starken Windböen und Blitzschlägen. Bitte nehmen Sie Schutzmaßnahmen, insbesondere im Freien, um Ihre Sicherheit zu gewährleisten. Vermeiden Sie offene Flächen und suchen Sie geschützte Bereiche auf.",
        threadLevel = 4,
        postDate = Date(),
        expireDate = Date(),
        location = Location(40.7128, -74.0060),
        locationString = "Ludwigsstraße 3, München"
    )

    AlertCard(alert = alert)
}