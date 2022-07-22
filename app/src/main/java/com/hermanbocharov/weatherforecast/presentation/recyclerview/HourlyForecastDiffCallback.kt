package com.hermanbocharov.weatherforecast.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast

class HourlyForecastDiffCallback : DiffUtil.ItemCallback<HourlyForecast>() {

    override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem.forecastTime == newItem.forecastTime && oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem == newItem
    }
}