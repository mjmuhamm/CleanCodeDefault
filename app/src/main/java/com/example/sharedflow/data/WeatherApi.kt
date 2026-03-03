package com.example.sharedflow.data

import com.example.sharedflow.domain.City
import com.example.sharedflow.domain.CurrentWeather
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") name: String
    ) : CitySearchResponse

    @GET("v1/forecast?current_weather=true")
    suspend fun getWeatherData(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ) : WeatherResponse
}

data class CitySearchResponse(
    val results: List<City>? = emptyList()
)

data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather
)