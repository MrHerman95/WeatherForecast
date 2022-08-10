package com.hermanbocharov.weatherforecast.presentation.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.domain.entities.Location

class RecentLocationAdapter : ListAdapter<Location, RecentLocationAdapter.RecentLocationViewHolder>(
    LocationDiffCallback()
) {

    var onPinLocationClickListener: ((Location) -> Unit)? = null
    var onRecentLocationClickListener: ((Location) -> Unit)? = null
    var onRecentLocationLongClickListener: ((Location) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentLocationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = when (viewType) {
            VIEW_TYPE_NOT_PINNED -> R.layout.item_recent_location
            VIEW_TYPE_PINNED -> R.layout.item_recent_location_pinned
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        return RecentLocationViewHolder(inflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecentLocationViewHolder, position: Int) {
        val locationItem = getItem(position)

        holder.tvLocation.text = holder.view.context.getString(
            R.string.str_location_city_country,
            locationItem.name,
            locationItem.country
        )

        holder.ivPin.setOnClickListener {
            onPinLocationClickListener?.invoke(locationItem)
            /*val changedList = currentList.toMutableList()
            changedList[position] = locationItem.copy(isPinned = !locationItem.isPinned)
            submitList(changedList)*/
        }

        holder.view.setOnClickListener {
            onRecentLocationClickListener?.invoke(locationItem)
        }

        holder.view.setOnLongClickListener {
            onRecentLocationLongClickListener?.invoke(locationItem)
            true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isPinned) {
            false -> VIEW_TYPE_NOT_PINNED
            true -> VIEW_TYPE_PINNED
        }
    }

    companion object {
        private const val VIEW_TYPE_NOT_PINNED = 200
        private const val VIEW_TYPE_PINNED = 201
    }

    class RecentLocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvLocation: TextView = view.findViewById(R.id.tv_recent_location)
        val ivPin: ImageView = view.findViewById(R.id.iv_pin)
    }
}