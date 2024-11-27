package com.practicum.playlistmaker


import com.google.gson.annotations.SerializedName

class ITunesDTO(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<Result>
)