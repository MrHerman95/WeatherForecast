package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.entities.Location

class RecentLocationAdapter : ListAdapter<Location, RecentLocationAdapter.RecentLocationViewHolder>(
    LocationDiffCallback()
) {

    private var onRecentLocationClickListener: ((Location) -> Unit)? = null
    private var onRecentLocationLongClickListener: ((Location) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentLocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recent_location,
            parent,
            false
        )
        return RecentLocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentLocationViewHolder, position: Int) {
        val locationItem = getItem(position)

        holder.tvLocation.text = holder.view.context.getString(
            R.string.str_location_city_country,
            locationItem.name,
            locationItem.country
        )

        holder.view.setOnClickListener {
            onRecentLocationClickListener?.invoke(locationItem)
        }

        holder.view.setOnLongClickListener {
            onRecentLocationLongClickListener?.invoke(locationItem)
            true
        }
    }

    class RecentLocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvLocation: TextView = view.findViewById(R.id.tv_recent_location)
    }
}