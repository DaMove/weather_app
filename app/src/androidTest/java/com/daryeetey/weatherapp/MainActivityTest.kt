package com.daryeetey.weatherapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.daryeetey.weatherapp.presentation.ui.WeatherScreen
import com.daryeetey.weatherapp.presentation.viewmodels.WeatherViewModel
import com.daryeetey.weatherapp.presentation.ui.theme.WeatherAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        // I may Inject my dependencies using Hilt or manually if needed
        // viewModel = hiltViewModel() or inject manually
    }

    @Test
    fun testWeatherSearchByCity() {
        // Launch the WeatherScreen composable
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Find the text input field and type in a city name
        composeTestRule.onNodeWithText("Enter city").performTextInput("Dallas")

        // Click the "Get Weather" button
        composeTestRule.onNodeWithText("Get Weather").performClick()

        // Check that the loading spinner is visible
        composeTestRule.onNodeWithContentDescription("Progress indicator").assertIsDisplayed()

        // Simulate fetching weather data by waiting for the result
        composeTestRule.waitForIdle()

        // Check that the weather information is displayed
        composeTestRule.onNodeWithText("City: Dallas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temperature:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description:").assertIsDisplayed()

        // Verify the image is displayed (weather icon)
        composeTestRule.onNodeWithTag("weather_icon").assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        // Launch the WeatherScreen composable
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Perform the city search
        composeTestRule.onNodeWithText("Enter city").performTextInput("Dallas")
        composeTestRule.onNodeWithText("Get Weather").performClick()

        // Check that the loading spinner appears when waiting for weather data
        composeTestRule.onNodeWithContentDescription("Progress indicator").assertIsDisplayed()

        // Wait for a brief moment to simulate the loading process
        composeTestRule.waitForIdle()

        // After loading, the progress indicator should disappear
        composeTestRule.onNodeWithContentDescription("Progress indicator").assertDoesNotExist()
    }

    @Test
    fun testErrorMessageOnFailure() {
        // Simulate an error in fetching weather data (e.g., network failure)
        // I may need to mock my repository or API call for this scenario.

        // Launch the WeatherScreen composable
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Enter a city and trigger a failure
        composeTestRule.onNodeWithText("Enter city").performTextInput("InvalidCity")
        composeTestRule.onNodeWithText("Get Weather").performClick()

        // Check that an error message is displayed when data fetching fails
        composeTestRule.onNodeWithText("Failed to load the weather data.").assertIsDisplayed()
    }

    @Test
    fun testPermissionRequest() {
        // Test if the app requests permission for location
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

    }
}
