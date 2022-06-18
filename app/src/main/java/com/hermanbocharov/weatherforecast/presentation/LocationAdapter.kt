package com.hermanbocharov.weatherforecast.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.Location

class LocationAdapter : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    var locationList = listOf<Location>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_location,
            parent,
            false
        )
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val locationItem = locationList[position]

        if (locationItem.state.isNullOrBlank()) {
            holder.tvLocation.text = holder.view.context.getString(
                R.string.str_location_without_state,
                locationItem.name,
                locationItem.country
            )
        } else {
            holder.tvLocation.text = holder.view.context.getString(
                R.string.str_location_full,
                locationItem.name,
                locationItem.state,
                locationItem.country
            )
        }

        holder.view.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvLocation: TextView = view.findViewById(R.id.tv_location_item)
    }
}