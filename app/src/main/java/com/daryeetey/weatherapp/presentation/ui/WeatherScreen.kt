package com.daryeetey.weatherapp.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.daryeetey.weatherapp.data.WeatherApiConstants.API_KEY
import com.daryeetey.weatherapp.data.WeatherApiConstants.IMG_PATH
import com.daryeetey.weatherapp.presentation.viewmodels.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    var city by remember { mutableStateOf("") }
    val weatherData by viewModel.weatherData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFFBBDEFB))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Enter city") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.getWeather(city, API_KEY) }) {
                Text("Get Weather")
            }

            if (isLoading) {
                // Show a progress indicator while loading
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            weatherData?.let { result ->
                result.onSuccess { weatherResponse ->
                    Text("City: ${weatherResponse.name}")
                    Text("Temperature: ${weatherResponse.main.temp} °C")
                    Text("Description: ${weatherResponse.weather[0].description}")
                    Spacer(modifier = Modifier.height(8.dp))

                    val fullImagePath = IMG_PATH + weatherResponse.weather[0].icon + "@2x.png"
                    Image(
                        painter = rememberAsyncImagePainter(model = fullImagePath),
                        contentDescription = "Weather icon",
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                result.onFailure {
                    Text("Failed to load the weather data.")
                }
            }
        }
    }
}
