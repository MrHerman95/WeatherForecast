package com.hermanbocharov.weatherforecast.presentation

import androidx.recyclerview.widget.DiffUtil
import com.hermanbocharov.weatherforecast.domain.Location

class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {

    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }
}