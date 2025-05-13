package com.practicum.playlistmaker.Domain


import com.google.gson.annotations.SerializedName
import java.util.Date

data class Track(   //entities
   // @SerializedName("artistName")
    val artistName: String,
    //@SerializedName("artworkUrl100")
    val artworkUrl100: String,
    //@SerializedName("trackName")
    val trackName: String,
    //@SerializedName("trackTimeMillis")
    val trackTimeMillis: Int,   //String  должно преобразовывать в строку при передачи от слоя Data -> Domain
    //@SerializedName("trackId")
    val trackId:Int,
    //@SerializedName("collectionName")  //---------------
    val collectionName:String,
    //@SerializedName("releaseDate")
    val releaseDate: Date,
    //@SerializedName("country")
    val country:String,
    //@SerializedName("primaryGenreName")
    val primaryGenreName:String,
    //@SerializedName("previewUrl")
    val previewUrl:String
)



//class Track
//package com.practicum.playlistmaker.Domain
//
//import java.util.Date
//
//data class Track(  //entities
//    val trackName:String,
//    val artistName:String,
//    val trackTime:String,
//    val artworkUrl100:String,
//    val trackId:Int,
//    val collectionName:String,
//    val releaseDate: Date,
//    val country:String,
//    val primaryGenreName:String,
//    val previewUrl:String
//)