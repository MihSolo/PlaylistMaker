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
    val trackTimeMillis: Int,
    @SerializedName("trackId")
    val trackId:Int,
    @SerializedName("collectionName")  //---------------
    val collectionName:String,
    @SerializedName("releaseDate")
    val releaseDate: Date,
    @SerializedName("country")
    val country:String,
    @SerializedName("primaryGenreName")
    val primaryGenreName:String
)
//    :Parcelable{
//  constructor(parcel:Parcel):this(
//      parcel.readInt(),
//      parcel.readString()!!,
//      parcel.readString()!!
//  ){
//
//  }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//      dest.writeInt(trackTimeMillis)
//        dest.writeString(artistName)
//        dest.writeString(country)
//    }
//}