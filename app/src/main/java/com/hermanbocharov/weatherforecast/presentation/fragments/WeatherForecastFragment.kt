package com.hermanbocharov.weatherforecast.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hermanbocharov.weatherforecast.R

class WeatherForecastFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("INSTANCES", "onCreate() WeatherForecastFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_forecast, container, false)
    }

    companion object {

        fun newInstance() = WeatherForecastFragment()
    }
}