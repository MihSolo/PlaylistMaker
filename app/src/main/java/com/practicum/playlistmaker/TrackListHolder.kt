package com.practicum.playlistmaker

import android.content.Context
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
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
import java.util.Locale

class TrackListHolder(view:View):RecyclerView.ViewHolder(view) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val trackCover:ImageView

    init {
        trackNameView = itemView.findViewById(R.id.trackNameView)
        artistNameView = itemView.findViewById(R.id.artistNameView)
        trackTimeView = itemView.findViewById(R.id.trackTimeView)
        trackCover = itemView.findViewById(R.id.trackCover)
    }

    fun bind(track:Result){
        trackNameView.setText(track.trackName)
        artistNameView.setText(track.artistName)
        trackTimeView.setText(SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis).toString())
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable
                .connection_error)
            .fitCenter()
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius_value)))
            .into(trackCover)
    }

}
