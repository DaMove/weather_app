package com.daryeetey.weatherapp.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.ui.Modifier

import androidx.hilt.navigation.compose.hiltViewModel
import com.daryeetey.weatherapp.presentation.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import com.daryeetey.weatherapp.presentation.viewmodels.WeatherViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: WeatherViewModel

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.fetchWeatherForLocation(this)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            viewModel = hiltViewModel<WeatherViewModel>()
            // Request location permissions
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            // Load last searched city weather
            viewModel.loadLastSearchedCityWeather()

            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(

                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

    }

}


