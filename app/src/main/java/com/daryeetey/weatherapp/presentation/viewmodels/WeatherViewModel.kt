package com.daryeetey.weatherapp.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daryeetey.weatherapp.data.LocationUnavailableException
import com.daryeetey.weatherapp.data.WeatherApiConstants.API_KEY
import com.daryeetey.weatherapp.data.WeatherRepository
import com.daryeetey.weatherapp.data.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherData = MutableLiveData<Result<WeatherResponse>?>()
    val weatherData: LiveData<Result<WeatherResponse>?> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val FALLBACK_CITY = "Dallas"

    fun getWeather(city: String, apiKey: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.fetchWeather(city, apiKey)
                handleWeatherResponse(response)
                repository.saveLastSearchedCity(city)
            } catch (e: Exception) {
                _error.value = "Failed to load weather data."
                _isLoading.value = false
            }
        }
    }

    fun loadLastSearchedCityWeather() {
        val lastCity = repository.getLastSearchedCity()
        lastCity?.let { getWeather(it, API_KEY) }
    }

    fun fetchWeatherForLocation(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val location = repository.getCurrentLocation(context)
                if (location != null) {
                    val response = repository.getWeatherByCoordinates(location.latitude, location.longitude, API_KEY)
                    handleWeatherResponse(response)
                }
            } catch (e: LocationUnavailableException) {
                getWeather(FALLBACK_CITY, API_KEY)
            } catch (e: Exception) {
                _error.value = "Failed to get location or weather data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleWeatherResponse(response: Result<WeatherResponse>?) {
        _weatherData.value = response
        _isLoading.value = false
    }
}
