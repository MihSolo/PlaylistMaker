package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Track(
    val trackName:String,
    val artistName:String,
    val trackTime:String,
    val artworkUrl100:String,
    val trackId:Int,
    val collectionName:String,
    val releaseDate: Date,
    val country:String,
    val primaryGenreName:String,
    val previewUrl:String
    )