package com.practicum.playlistmaker.Data

import com.practicum.playlistmaker.Domain.LoadTracksListAPI
import com.practicum.playlistmaker.Domain.Track
import com.practicum.playlistmaker.Domain.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(
    private val retrofitConfiguration : RetrofitConfiguration
    //private val searchHistory: SearchHistory  // если надо сохранения
): TrackRepository {   //interface Repository using
//        override fun getTracks(
//        callback_: (Result<List<List<Track>>>) -> Unit,
//        callString: String,
//        callback: (ITunesDTO) -> Unit,
//        onResponse: (Response<ITunesDTO>) -> Unit,
//        onFailure:(Call<ITunesDTO>)->Unit
//    ) {
//        retrofitConfiguration.loadResults(
//            retrofitConfiguration.configureRetrofit(), callString, object : Callback<List<ITunesDTO>> {
//                override fun onResponse(call: Call<ITunesDTO>, response: Response<List<ITunesDTO>>) {
////                    response.body()?.let {
////                        val trackDTO = it
////                        callback(trackDTO)
////                        onResponse(response)
////                    }
//                val trackDTO = response.body() ?: emptyList()
//                val track = trackDTO.map{
////                    Track(it.results.artistName, it.results.artworkUrl100, it.results.trackName,it.results.trackTimeMillis,
////                        it.results.trackId, it.results.collectionName, it.results.releaseDate, it.results.country,
////                        it.results.primaryGenreName, it.results.previewUrl)
//                    it.results
//                }
//                    callback_(Result.success(track))
//                }
//
//                override fun onFailure(call: Call<ITunesDTO>, t: Throwable) {
//                    onFailure(call)
//                }
//            })
//    }

    override fun getTracks(
        callback_: (Result<List<Track>>) -> Unit,
        callString: String,
        callback: (ITunesDTO) -> Unit,
        onResponse: (Response<ITunesDTO>) -> Unit,
        onFailure: (Call<ITunesDTO>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}