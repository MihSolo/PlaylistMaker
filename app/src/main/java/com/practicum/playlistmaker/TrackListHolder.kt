package com.practicum.playlistmaker

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.view.ViewConfiguration
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.core.net.toUri
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.textfield.TextInputLayout

class TrackListHolder(view:View):RecyclerView.ViewHolder(view) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val trackCover:ImageView

    init {
        trackNameView = view.findViewById(R.id.trackNameView)
        artistNameView = view.findViewById(R.id.artistNameView)
        trackTimeView = view.findViewById(R.id.trackTimeView)
        trackCover = view.findViewById(R.id.trackCover)
    }

    fun bind(track:Track){
            trackNameView.setText(track.trackName)
            artistNameView.setText(track.artistName)
            trackTimeView.setText(track.trackTime)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable
                .connection_error)
            .fitCenter()
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius_value)))
            .into(trackCover)
    }

}
