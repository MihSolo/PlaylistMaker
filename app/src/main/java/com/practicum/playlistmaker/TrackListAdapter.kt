package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackListAdapter(
    private val track: List<Track>
) : RecyclerView.Adapter<TrackListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackListHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListHolder, position: Int) {
        holder.bind(track[position])
    }

    override fun getItemCount(): Int {
        return track.size
    }
}