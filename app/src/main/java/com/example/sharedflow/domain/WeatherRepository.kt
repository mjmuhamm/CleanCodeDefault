package com.example.sharedflow.domain

interface WeatherRepository {
    suspend fun searchCity(name: String) : Result<List<City>>
    suspend fun fetchWeather(): Result<CurrentWeather>
    suspend fun fetchWeather(lat: Double, lon: Double): Result<CurrentWeather>
}