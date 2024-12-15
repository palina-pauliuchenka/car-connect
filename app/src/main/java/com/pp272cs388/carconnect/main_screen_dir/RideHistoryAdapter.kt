package com.pp272cs388.carconnect.main_screen_dir

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pp272cs388.carconnect.R

class RideHistoryAdapter(private val rideHistoryList: List<RideHistoryItem>) :
    RecyclerView.Adapter<RideHistoryAdapter.RideHistoryViewHolder>() {

    class RideHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val destinationTextView: TextView = view.findViewById(R.id.destinationTextView)
        val etaTextView: TextView = view.findViewById(R.id.etaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ride_history, parent, false)
        return RideHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideHistoryViewHolder, position: Int) {
        val ride = rideHistoryList[position]
        holder.nameTextView.text = ride.name
        holder.destinationTextView.text = ride.destination
        holder.etaTextView.text = ride.eta
    }

    override fun getItemCount(): Int {
        return rideHistoryList.size
    }
}
