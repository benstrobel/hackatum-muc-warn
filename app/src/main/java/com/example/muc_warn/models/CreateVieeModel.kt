package com.example.muc_warn.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.muc_warn.schema.Alert
import com.example.muc_warn.schema.Location
import java.util.Date

class CreateViewModel : ViewModel() {

    // Define the alert data using MutableState
    val alert = mutableStateOf(
        Alert(
            id = "789",
            senderName = "",
            title = "",
            description = "",
            threadLevel = 0,
            postDate = Date(),
            expireDate = Date(),
            location = Location(0.0, 0.0), // Assuming Location is a data class with latitude and longitude
            locationString = ""
        )
    )

    // Functions to handle changes in the form fields
    fun onSenderNameChange(senderName: String) {
        alert.value = alert.value.copy(senderName = senderName)
    }

    fun onTitleChange(title: String) {
        alert.value = alert.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        alert.value = alert.value.copy(description = description)
    }

    fun onPostDateSelected(date: Date) {
        alert.value = alert.value.copy(postDate = date)
    }

    fun onExpireDateSelected(date: Date) {
        alert.value = alert.value.copy(expireDate = date)
    }

    fun onLocationSelected(location: Location) {
        alert.value = alert.value.copy(location = location)
    }
}