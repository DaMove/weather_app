package com.daryeetey.weatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse


    @GET("weather")
    suspend fun getWeatherByLatLng(
        @Query("lat")latitude: Double,
        @Query("lon")longitude:Double,
        @Query("appid")apiKey: String
    ): WeatherResponse
}
