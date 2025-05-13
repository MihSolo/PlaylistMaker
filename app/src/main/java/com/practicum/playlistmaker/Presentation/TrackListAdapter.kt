package com.practicum.playlistmaker.Presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Domain.Track
import com.practicum.playlistmaker.R


class TrackListAdapter(
    private var track:List<Track>, var listener: Listener
): RecyclerView.Adapter<TrackListHolder>() {

//   private lateinit var track: List<Result>
//   private lateinit var listener: Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackListHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListHolder, position: Int) {
        holder.bind(track[position], listener)
    }

    override fun getItemCount(): Int = track.size

    interface Listener {
        fun OnClick(track: Track)
    }

    fun updateTracks(newTracks: List<Track>) {
        track = newTracks
        notifyDataSetChanged()
    }

}