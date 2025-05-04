package com.practicum.playlistmaker.Domain

import java.util.Date

data class Track(  //entities
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