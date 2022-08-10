package com.hermanbocharov.weatherforecast.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.hermanbocharov.weatherforecast.domain.entities.Location

class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {

    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }
}