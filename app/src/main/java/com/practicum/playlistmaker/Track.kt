package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName:String,
    val artistName:String,
    val trackTime:String,
    val artworkUrl100:String,
    val trackId:Int
    )