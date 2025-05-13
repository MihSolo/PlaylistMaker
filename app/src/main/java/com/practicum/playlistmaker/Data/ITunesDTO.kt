package com.practicum.playlistmaker.Data


import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.Domain.Track

class ITunesDTO(      //Data
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<Track>  //TrackDTO
)