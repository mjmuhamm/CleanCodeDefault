package com.example.sharedflow.domain

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    val temperature: Double,
    val windSpeed: Double
)
