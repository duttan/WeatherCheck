package com.example.weathercheck.domain

import com.example.weathercheck.BuildConfig
import com.example.weathercheck.data.api.ApiService
import javax.inject.Inject

class WeatherRepo
@Inject
constructor(private val apiService: ApiService) {
    suspend fun getWeather(query: String) = apiService.getWeather(query, BuildConfig.APP_ID)
}