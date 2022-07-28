package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.content.Context
import android.text.format.DateFormat
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
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit.FAHRENHEIT
import java.text.SimpleDateFormat
import java.util.*

class HourlyForecastAdapter :
    ListAdapter<HourlyForecast, HourlyForecastAdapter.HourlyForecastViewHolder>(
        HourlyForecastDiffCallback()
    ) {

    var onHourForecastClickListener: ((HourlyForecast) -> Unit)? = null
    private var selectedItemPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = when (viewType) {
            VIEW_TYPE_NOT_SELECTED -> R.layout.item_hourly
            VIEW_TYPE_SELECTED -> R.layout.item_hourly_selected
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        return HourlyForecastViewHolder(inflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val hourForecastItem = getItem(position)
        val temperature: String = when (hourForecastItem.tempUnit) {
            CELSIUS -> holder.view.context.getString(
                R.string.str_temp_celsius,
                hourForecastItem.temp
            )
            FAHRENHEIT -> holder.view.context.getString(
                R.string.str_temp_fahrenheit,
                hourForecastItem.temp
            )
            else -> throw RuntimeException("Unknown temperature unit ${hourForecastItem.tempUnit}")
        }

        holder.tvDate.text = getDateFromTimestamp(
            hourForecastItem.forecastTime.toLong(),
            hourForecastItem.timezone
        )
        holder.tvTime.text = getTimeFromTimestamp(
            holder.view.context,
            hourForecastItem.forecastTime.toLong(),
            hourForecastItem.timezone
        )
        holder.tvTemp.text = temperature
        holder.ivWeather.setImageDrawable(
            ResourcesCompat.getDrawable(
                holder.view.resources,
                R.drawable.ic_test_small_3d,
                null
            )
        )

        holder.view.setOnClickListener {
            getItem(selectedItemPos).isSelected = false
            notifyItemChanged(selectedItemPos)
            onHourForecastClickListener?.invoke(hourForecastItem)
            selectedItemPos = holder.bindingAdapterPosition
            notifyItemChanged(selectedItemPos)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isSelected) {
            false -> VIEW_TYPE_NOT_SELECTED
            true -> VIEW_TYPE_SELECTED
        }
    }

    private fun getDateFromTimestamp(timestamp: Long, timezone: String): String {
        val formatter = SimpleDateFormat("MMM. d", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone(timezone)
        return formatter.format(Date(timestamp * 1000))
    }

    private fun getTimeFromTimestamp(context: Context, timestamp: Long, timezone: String): String {
        val formatter = if (DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("HH:mm", Locale.ENGLISH)
        } else {
            SimpleDateFormat("h a", Locale.ENGLISH)
        }
        formatter.timeZone = TimeZone.getTimeZone(timezone)
        return formatter.format(Date(timestamp * 1000))
    }

    companion object {
        private const val VIEW_TYPE_NOT_SELECTED = 100
        private const val VIEW_TYPE_SELECTED = 101
    }

    class HourlyForecastViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_hourly_date)
        val tvTime: TextView = view.findViewById(R.id.tv_hourly_time)
        val ivWeather: ImageView = view.findViewById(R.id.iv_hourly_weather)
        val tvTemp: TextView = view.findViewById(R.id.tv_hourly_temp)
    }
}