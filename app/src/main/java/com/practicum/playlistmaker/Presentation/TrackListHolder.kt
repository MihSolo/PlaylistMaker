package com.practicum.playlistmaker.Presentation

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Domain.Result
import com.practicum.playlistmaker.R
import java.util.Locale

class TrackListHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val trackCover: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.track_name_view)
        artistNameView = itemView.findViewById(R.id.artist_name_view)
        trackTimeView = itemView.findViewById(R.id.trackTimeView)
        trackCover = itemView.findViewById(R.id.trackCover)
    }

    fun bind(track: Result, listener: TrackListAdapter.Listener? = null) {
        trackNameView.setText(track.trackName)
        artistNameView.setText(track.artistName)
        trackTimeView.setText(
            SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(track.trackTimeMillis).toString()
        )
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(
                R.drawable.connection_error
            )
            .fitCenter()
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius_value)))
            .into(trackCover)

        itemView.setOnClickListener {
            listener?.OnClick(track)
        }

    }

}
