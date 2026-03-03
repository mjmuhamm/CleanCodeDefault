package com.example.sharedflow.di

import com.example.sharedflow.data.WeatherApi
import com.example.sharedflow.data.WeatherRepositoryImpl
import com.example.sharedflow.domain.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApi(): WeatherApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    @Provides
    fun provideRepo(api: WeatherApi)
    : WeatherRepository = WeatherRepositoryImpl(api)
}