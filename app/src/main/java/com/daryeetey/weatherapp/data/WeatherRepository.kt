package com.daryeetey.weatherapp.data

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val sharedPreferences: SharedPreferences,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    suspend fun fetchWeather(city: String, apiKey: String): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeather(city, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val KEY_LAST_SEARCHED_CITY = "last_searched_city"
    }

    fun saveLastSearchedCity(city: String) {
        sharedPreferences.edit().putString(KEY_LAST_SEARCHED_CITY, city).apply()
    }

    fun getLastSearchedCity(): String? {
        return sharedPreferences.getString(KEY_LAST_SEARCHED_CITY, null)
    }


    suspend fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<WeatherResponse>? {
        return try {
            val response = apiService.getWeatherByLatLng(latitude, longitude, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentLocation(context: Context, fallbackCity: String = "Dallas"): Location? {
        return suspendCancellableCoroutine { continuation ->
            // Check if permissions are granted
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Handle permission issue: Consider requesting permissions or notifying the user
                continuation.resumeWithException(SecurityException("Location permission not granted"))
                return@suspendCancellableCoroutine
            }

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // If location is available, return it
                    continuation.resume(location)
                } else {
                    // Fallback if location is null
                    // For now, you can return a default city or log this issue and allow the user to retry
                    Log.w(
                        "LocationFallback",
                        "Location is unavailable, using fallback city: $fallbackCity"
                    )

                    // Example: Here we simply log the issue. You might want to return a default location instead.
                    continuation.resumeWithException(LocationUnavailableException("Location unavailable, using fallback: $fallbackCity"))
                }
            }.addOnFailureListener { exception ->
                // If location request fails, provide a fallback (retry logic or default city)
                Log.e("LocationError", "Failed to fetch location", exception)
                continuation.resumeWithException(exception)
            }
        }
    }
}
    // Custom Exception for fallback handling
    class LocationUnavailableException(message: String) : Exception(message)

