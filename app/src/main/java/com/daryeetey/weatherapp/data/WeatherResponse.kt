package com.daryeetey.weatherapp.data

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherDataItem>,
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class WeatherDataItem(
    val description: String,
    val icon: String
)
