package com.hermanbocharov.weatherforecast.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.hermanbocharov.weatherforecast.domain.entities.DailyForecast

class DailyForecastDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {

    override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem.forecastTime == newItem.forecastTime
    }

    override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem == newItem
    }
}