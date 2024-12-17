package com.practicum.playlistmaker


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("artistName")
    val artistName: String,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String,
    @SerializedName("trackName")
    val trackName: String,
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Int,
    @SerializedName("trackId")
    val trackId:Int
)