package com.example.donorsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BloodDriveAdapter(
    private val bloodDrives: List<BloodDriveData>,
    private val onItemClick: (BloodDriveData) -> Unit
) : RecyclerView.Adapter<BloodDriveAdapter.BloodDriveViewHolder>() {

    class BloodDriveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val venueText: TextView = itemView.findViewById(R.id.venue_text)
        val dateText: TextView = itemView.findViewById(R.id.date_text)
        val timeText: TextView = itemView.findViewById(R.id.time_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodDriveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blood_drive, parent, false)
        return BloodDriveViewHolder(view)
    }

    override fun onBindViewHolder(holder: BloodDriveViewHolder, position: Int) {
        val bloodDrive = bloodDrives[position]

        holder.venueText.text = bloodDrive.venue
        holder.dateText.text = bloodDrive.date
        holder.timeText.text = bloodDrive.time

        holder.itemView.setOnClickListener {
            onItemClick(bloodDrive)
        }
    }

    override fun getItemCount(): Int = bloodDrives.size
}