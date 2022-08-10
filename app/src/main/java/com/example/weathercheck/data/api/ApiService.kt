package com.example.weathercheck.data.api

import android.os.Build
import com.example.weathercheck.BuildConfig
import com.example.weathercheck.data.model.Weather
import com.example.weathercheck.data.model.WeatherData
import com.example.weathercheck.data.model.WeatherInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    suspend fun getWeather(@Query("q") city_name: String,
                           @Query("APPID") api_key: String = BuildConfig.APP_ID): Response<WeatherInfoResponse>
}
