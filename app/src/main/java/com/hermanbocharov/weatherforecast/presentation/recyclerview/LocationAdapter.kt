package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.entities.Location

class LocationAdapter : ListAdapter<Location, LocationAdapter.LocationViewHolder>(
    LocationDiffCallback()
) {

    var onLocationClickListener: ((Location) -> Unit)? = null
    var onLocationLongClickListener: ((Location) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_location,
            parent,
            false
        )
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val locationItem = getItem(position)

        holder.tvLocCityCountry.text = holder.view.context.getString(
            R.string.str_location_city_country,
            locationItem.name,
            locationItem.country
        )

        if (locationItem.state.isNullOrBlank()) {
            holder.tvLocState.text = "-"
        } else {
            holder.tvLocState.text = locationItem.state
        }

        holder.view.setOnClickListener {
            onLocationClickListener?.invoke(locationItem)
        }

        holder.view.setOnLongClickListener {
            onLocationLongClickListener?.invoke(locationItem)
            true
        }
    }

    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvLocCityCountry: TextView = view.findViewById(R.id.tv_loc_city_country_item)
        val tvLocState: TextView = view.findViewById(R.id.tv_loc_state_item)
    }
}