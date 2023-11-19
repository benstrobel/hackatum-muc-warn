package com.example.muc_warn.schema

import java.util.Date

data class Alert(
    val id: String,
    val senderName: String,
    val title: String,
    val description: String,
    val threadLevel: Int,
    val postDate: Date,
    val expireDate: Date,
    val location: Location,
    val locationString: String
)

data class Location(
    val latitude: Double,
    val longitude: Double
)