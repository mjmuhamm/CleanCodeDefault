package com.example.sharedflow.domain

import com.google.gson.annotations.SerializedName

data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    @SerializedName("admin1")
    val state: String? = null
)