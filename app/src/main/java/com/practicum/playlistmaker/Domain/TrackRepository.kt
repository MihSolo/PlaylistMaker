package com.practicum.playlistmaker.Domain

import com.practicum.playlistmaker.Data.ITunesDTO
import retrofit2.Call
import retrofit2.Response

interface TrackRepository {  //Repository template realesation
    fun getTracks(callback_: (Result<List<Track>>) -> Unit, callString:String, callback: (ITunesDTO) -> Unit,
                  onResponse:(Response<ITunesDTO>)->Unit, onFailure:(Call<ITunesDTO>)->Unit)
}