package com.practicum.playlistmaker


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Result(
    @SerializedName("artistName")
    val artistName: String,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String,
    @SerializedName("trackName")
    val trackName: String,
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Int,     //-------------------------------------------
    @SerializedName("trackId")
    val trackId:Int,
    @SerializedName("collectionName")  //---------------
    val collectionName:String,
    @SerializedName("releaseDate")
    val releaseDate: Date,
    @SerializedName("country")
    val country:String,
    @SerializedName("primaryGenreName")
    val primaryGenreName:String,
    @SerializedName("previewUrl")
    val previewUrl:String
)
