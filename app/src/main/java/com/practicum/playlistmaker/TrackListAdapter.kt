package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class TrackListAdapter(
    private var track:List<Result>, var listener:Listener
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
        fun OnClick(track: Result)
    }

    fun updateTracks(newTracks: List<Result>) {
        track = newTracks
        notifyDataSetChanged()
    }

}