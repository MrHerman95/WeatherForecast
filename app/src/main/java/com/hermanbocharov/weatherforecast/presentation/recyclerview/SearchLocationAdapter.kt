package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.entities.Location

class SearchLocationAdapter : ListAdapter<Location, SearchLocationAdapter.SearchLocationViewHolder>(
    LocationDiffCallback()
) {

    var onSearchLocationClickListener: ((Location) -> Unit)? = null
    var onSearchLocationLongClickListener: ((Location) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_location,
            parent,
            false
        )
        return SearchLocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchLocationViewHolder, position: Int) {
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
            onSearchLocationClickListener?.invoke(locationItem)
        }

        holder.view.setOnLongClickListener {
            onSearchLocationLongClickListener?.invoke(locationItem)
            true
        }
    }

    class SearchLocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvLocCityCountry: TextView = view.findViewById(R.id.tv_loc_city_country_item)
        val tvLocState: TextView = view.findViewById(R.id.tv_loc_state_item)
    }
}