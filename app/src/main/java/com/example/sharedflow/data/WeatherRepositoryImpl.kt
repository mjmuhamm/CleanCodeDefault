package com.example.sharedflow.data

import com.example.sharedflow.domain.City
import com.example.sharedflow.domain.CurrentWeather
import com.example.sharedflow.domain.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi) : WeatherRepository {
    override suspend fun searchCity(name: String): Result<List<City>> {
        return try {
            val response = api.searchCity(name)
            Result.success(response.results ?: emptyList())
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchWeather(): Result<CurrentWeather> {
        return fetchWeather(52.52, 13.41)
    }

    override suspend fun fetchWeather(
        lat: Double,
        lon: Double
    ): Result<CurrentWeather> {
        return try {
            val response = api.getWeatherData(lat,lon)
            Result.success(response.currentWeather)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

}