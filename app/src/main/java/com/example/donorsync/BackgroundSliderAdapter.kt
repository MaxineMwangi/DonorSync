package com.example.donorsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class BackgroundSliderAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<BackgroundSliderAdapter.BackgroundViewHolder>() {

    class BackgroundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.backgroundImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_background_slide, parent, false)
        return BackgroundViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackgroundViewHolder, position: Int) {
        try {
            holder.imageView.setImageResource(images[position])
        } catch (e: Exception) {
            // Fallback to a default background if image loading fails
            holder.imageView.setBackgroundColor(0xFF8B0000.toInt())
        }
    }

    override fun getItemCount(): Int = images.size
}