package com.daryeetey.weatherapp

import android.content.SharedPreferences
import android.location.Location
import com.daryeetey.weatherapp.data.Main
import com.daryeetey.weatherapp.data.WeatherApiConstants
import com.daryeetey.weatherapp.data.WeatherApiService
import com.daryeetey.weatherapp.data.WeatherDataItem
import com.daryeetey.weatherapp.data.WeatherRepository
import com.daryeetey.weatherapp.data.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.TaskCompletionSource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    @Mock
    private lateinit var weatherApiService: WeatherApiService

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @InjectMocks
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        // This is a great place to setup the mocks and inject them into the repository
    }

    @Test
    fun `getWeatherByCoordinates should fetch weather for the current location`() = runBlocking {
        // Mock location data
        val mockLocation = Location("provider").apply {
            latitude = 40.7128
            longitude = -74.0060
        }

        // Mock weather response
        val mockWeatherResponse = WeatherResponse(
            name = "New York",
            main = Main(temp = 22.0, humidity = 60),
            weather = listOf(WeatherDataItem(description = "Clear sky", icon = "01d"))
        )

        // Create a TaskCompletionSource to mock the Task<Location>
        val taskCompletionSource = TaskCompletionSource<Location>()
        taskCompletionSource.setResult(mockLocation)

        // Mock location fetch
        whenever(fusedLocationProviderClient.lastLocation).thenReturn(taskCompletionSource.task)

        // Mock API response
        whenever(weatherApiService.getWeatherByLatLng(40.7128, -74.0060,
            WeatherApiConstants.API_KEY
        ))
            .thenReturn(mockWeatherResponse)

        // Test the method
        val result = weatherRepository.getWeatherByCoordinates(40.7128, -74.0060,
            WeatherApiConstants.API_KEY
        )

        // Verify the result
        assertTrue(result?.isSuccess == true)
        assertEquals("New York", result?.getOrNull()?.name)
        assertEquals(22.0, result?.getOrNull()?.main?.temp)
        assertEquals("Clear sky", result?.getOrNull()?.weather?.first()?.description)
    }

    @Test
    fun `getWeatherByCoordinates should handle failure when location fetch fails`() = runBlocking {
        // Mock location fetch failure by throwing an exception
        val taskCompletionSource = TaskCompletionSource<Location>()
        taskCompletionSource.setException(SecurityException("Permission denied"))

        whenever(fusedLocationProviderClient.lastLocation).thenReturn(taskCompletionSource.task)

        // Test the getWeatherByCoordinates() method
        val result = weatherRepository.getWeatherByCoordinates(40.7128, -74.0060,
            WeatherApiConstants.API_KEY
        )

        // Verify the result - it should be a failure
        assertTrue(result?.isFailure == true)
    }

    @Test
    fun `getWeatherByCoordinates should handle failure when API response fails`() = runBlocking {
        // Mock location data
        val mockLocation = Location("provider").apply {
            latitude = 40.7128
            longitude = -74.0060
        }

        // Create a TaskCompletionSource to mock the Task<Location>
        val taskCompletionSource = TaskCompletionSource<Location>()
        taskCompletionSource.setResult(mockLocation)

        // Mock location fetch
        whenever(fusedLocationProviderClient.lastLocation).thenReturn(taskCompletionSource.task)

        // Mock API response failure
        whenever(weatherApiService.getWeatherByLatLng(40.7128, -74.0060,
            WeatherApiConstants.API_KEY
        ))
            .thenThrow(RuntimeException("API call failed"))

        // Test the method
        val result = weatherRepository.getWeatherByCoordinates(40.7128, -74.0060,
            WeatherApiConstants.API_KEY
        )

        // Verify the result - it should be a failure
        assertTrue(result?.isFailure == true)
    }
}
