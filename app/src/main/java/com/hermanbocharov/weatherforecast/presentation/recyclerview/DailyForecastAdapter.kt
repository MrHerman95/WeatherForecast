package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.entities.DailyForecast
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit.CELSIUS
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit.FAHRENHEIT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DailyForecastAdapter :
    ListAdapter<DailyForecast, DailyForecastAdapter.DailyForecastViewHolder>(
        DailyForecastDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_daily,
            parent,
            false
        )
        return DailyForecastViewHolder(view)
    }


    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dayForecastItem = getItem(position)
        val strTempId = when (dayForecastItem.tempUnit) {
            CELSIUS -> R.string.str_temp_celsius
            FAHRENHEIT -> R.string.str_temp_fahrenheit
            else -> throw RuntimeException("Unknown temperature unit ${dayForecastItem.tempUnit}")
        }
        val tempMin = holder.view.context.getString(strTempId, dayForecastItem.minTemp)
        val tempMax = holder.view.context.getString(strTempId, dayForecastItem.maxTemp)

        holder.tvDate.text = getDateFromTimestamp(
            dayForecastItem.forecastTime.toLong(),
            dayForecastItem.timezone
        )
        holder.tvSunrise.text = getTimeFromTimestamp(
            holder.view.context,
            dayForecastItem.sunriseTime.toLong(),
            dayForecastItem.timezone
        )
        holder.tvSunset.text = getTimeFromTimestamp(
            holder.view.context,
            dayForecastItem.sunsetTime.toLong(),
            dayForecastItem.timezone
        )
        holder.tvTempMin.text = tempMin
        holder.tvTempMax.text = tempMax

        val context = holder.ivWeather.context
        val weatherIconId =
            context.resources.getIdentifier(
                dayForecastItem.weatherIcon,
                "drawable",
                context.packageName
            )
        holder.ivWeather.setImageResource(weatherIconId)
    }

    private fun getDateFromTimestamp(timestamp: Long, timezone: String): String {
        val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEE, MMM d");
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone(timezone)
        return formatter.format(Date(timestamp * 1000))
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

    private fun getTimeFromTimestamp(context: Context, timestamp: Long, timezone: String): String {
        val formatter = if (DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("h:mm a", Locale.getDefault())
        }
        formatter.timeZone = TimeZone.getTimeZone(timezone)
        return formatter.format(Date(timestamp * 1000))
    }

    class DailyForecastViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_daily_date)
        val tvSunrise: TextView = view.findViewById(R.id.tv_sunrise_time)
        val tvSunset: TextView = view.findViewById(R.id.tv_sunset_time)
        val ivWeather: ImageView = view.findViewById(R.id.iv_daily_weather)
        val tvTempMin: TextView = view.findViewById(R.id.tv_min_temp)
        val tvTempMax: TextView = view.findViewById(R.id.tv_max_temp)
    }
}