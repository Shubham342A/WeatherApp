@file:Suppress("OverrideDeprecatedMigration")

package com.example.myapplication

import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.WeatherApp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    val CITY: String = "Delhi,India"
    val API: String = "88a63278d761c46e0f74f5dff8a66daa"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Main) {
            showLoader()

            val response = withContext(Dispatchers.IO) {
                fetchWeatherData()
            }

            if (response != null) {
                val weatherData = parseWeatherData(response)
                updateUI(weatherData)
                hideLoader()
            } else {
                showError()
            }
        }
    }

    private suspend fun fetchWeatherData(): String? {
        return try {
            URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                .readText(Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseWeatherData(response: String): WeatherData {
        val jsonobj = JSONObject(response)
        val main = jsonobj.getJSONObject("main")
        val sys = jsonobj.getJSONObject("sys")
        val wind = jsonobj.getJSONObject("wind")
        val weather = jsonobj.getJSONArray("weather").getJSONObject(0)
        val updatedAt: Long = jsonobj.getLong("dt")
        val updatedAtText =
            "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
                .format(Date(updatedAt * 1000))
        val temp = main.getString("temp") + "°C"
        val tempMin = "Min Temp : " + main.getString("temp_min") + "°C"
        val tempMax = "Max Temp : " + main.getString("temp_max") + "°C"
        val pressure = main.getString("pressure")
        val humidity = main.getString("humidity")
        val sunrise: Long = sys.getLong("sunrise")
        val sunset: Long = sys.getLong("sunset")
        val windSpeed: Long = wind.getLong("speed")
        val weatherDescription: String = weather.getString("description")
        val address = jsonobj.getString("name") + ", " + sys.getString("country")

        return WeatherData(
            address,
            updatedAtText,
            temp,
            tempMin,
            tempMax,
            sunrise,
            sunset,
            windSpeed,
            pressure,
            humidity,
            weatherDescription
        )
    }

    private fun updateUI(weatherData: WeatherData) {
        findViewById<TextView>(R.id.adress).text = weatherData.address
        findViewById<TextView>(R.id.update).text = weatherData.updatedAtText
        findViewById<TextView>(R.id.temp).text = weatherData.temp
        findViewById<TextView>(R.id.min_temp).text = weatherData.tempMin
        findViewById<TextView>(R.id.max_temp).text = weatherData.tempMax
        findViewById<TextView>(R.id.sunrise).text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                .format(Date(weatherData.sunrise * 1000))
        findViewById<TextView>(R.id.sunset).text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                .format(Date(weatherData.sunset * 1000))
        findViewById<TextView>(R.id.wind).text = weatherData.windSpeed.toString()
        findViewById<TextView>(R.id.pressure).text = weatherData.pressure
        findViewById<TextView>(R.id.humidity).text = weatherData.humidity
        findViewById<TextView>(R.id.sky).text = weatherData.weatherDescription
    }

    private fun showLoader() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.First).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE
    }

    private fun hideLoader() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.First).visibility = View.VISIBLE
    }

    private fun showError() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
    }
}

data class WeatherData(
    val address: String,
    val updatedAtText: String,
    val temp: String,
    val tempMin: String,
    val tempMax: String,
    val sunrise: Long,
    val sunset: Long,
    val windSpeed: Long,
    val pressure: String,
    val humidity: String,
    val weatherDescription: String
)


