package com.practicum.playlistmaker.Data

import com.practicum.playlistmaker.ITunesDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchAPI {  //entities?
    @GET("./search?entity=song")
    fun search(@Query("term") text: String): Call<ITunesDTO>
}