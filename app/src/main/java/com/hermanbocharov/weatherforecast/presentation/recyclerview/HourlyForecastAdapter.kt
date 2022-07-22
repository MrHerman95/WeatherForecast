package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit.CELSIUS

class HourlyForecastAdapter :
    ListAdapter<HourlyForecast, HourlyForecastAdapter.HourlyForecastViewHolder>(
        HourlyForecastDiffCallback()
    ) {

    var onHourForecastClickListener: ((HourlyForecast) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_hourly,
            parent,
            false
        )
        return HourlyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val hourForecastItem = getItem(position)
        val temperature: String = if (hourForecastItem.tempUnit == CELSIUS) {
            holder.view.context.getString(
                R.string.str_temp_celsius,
                hourForecastItem.temp
            )
        } else {
            holder.view.context.getString(
                R.string.str_temp_fahrenheit,
                hourForecastItem.temp
            )
        }

        holder.tvDate.text = hourForecastItem.forecastDate
        holder.tvTime.text = hourForecastItem.forecastTime
        holder.tvTemp.text = temperature
        holder.ivWeather.setImageDrawable(
            ResourcesCompat.getDrawable(
                holder.view.resources,
                R.drawable.ic_test_small_3d,
                null
            )
        )

        holder.view.setOnClickListener {
            onHourForecastClickListener?.invoke(hourForecastItem)
        }
    }

    class HourlyForecastViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_hourly_date)
        val tvTime: TextView = view.findViewById(R.id.tv_hourly_time)
        val ivWeather: ImageView = view.findViewById(R.id.iv_hourly_weather)
        val tvTemp: TextView = view.findViewById(R.id.tv_hourly_temp)
    }
}