package com.hermanbocharov.weatherforecast.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.R


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        viewModel.getCurrentWeather().observe(this) {
            Log.d("TEST_OF_LOADING_DATA", it.cityName)
            Log.d("TEST_OF_LOADING_DATA", it.temp.toString())
            Log.d("TEST_OF_LOADING_DATA", it.feelsLike.toString())
            Log.d("TEST_OF_LOADING_DATA", it.description)
            Log.d("TEST_OF_LOADING_DATA", it.updateTime.toString())
        }
    }
}