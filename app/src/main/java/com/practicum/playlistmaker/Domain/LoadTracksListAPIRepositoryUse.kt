package com.practicum.playlistmaker.Domain

import com.practicum.playlistmaker.Data.ITunesDTO
import com.practicum.playlistmaker.Data.TrackRepositoryImpl
import retrofit2.Call

class LoadTracksListAPIRepositoryUse(private val trackRepositoryImpl: TrackRepositoryImpl) {  //реализация UseCases класса(LoadTracksListAPI) через
// использование паттерна Repository, который реализован через интерфей Repository(у нас TrackRepositoryImpl)
//    fun execute(onSuccess: (List<List<Track>>) -> Unit, onFailure : (Call<ITunesDTO>) -> Unit){
//      trackRepositoryImpl.getTracks{
//
//      }
//    }
}