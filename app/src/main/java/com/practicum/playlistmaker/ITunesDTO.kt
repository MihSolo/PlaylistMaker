package com.practicum.playlistmaker


import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.Domain.Result

class ITunesDTO(      //entities?
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: List<Result> //Result
)