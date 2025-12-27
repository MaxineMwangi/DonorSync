package com.example.donorsync

import java.io.Serializable

data class BloodDriveData(
    val id: String = "",
    val organizationId: String = "",
    val venue: String = "",
    val date: String = "",
    val time: String = "",
    val eligibility: String = "",
    val requirements: String = "",
    val notes: String = "",
    val postedDate: Long = 0,
    val status: String = "active"
) : Serializable {
    // Default constructor required for Firebase
    constructor() : this("", "", "", "", "", "", "", "", 0, "active")
}