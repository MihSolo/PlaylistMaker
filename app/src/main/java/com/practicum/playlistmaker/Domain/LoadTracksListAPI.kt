package com.practicum.playlistmaker.Domain

import com.practicum.playlistmaker.Data.RetrofitConfiguration
import com.practicum.playlistmaker.Data.ITunesDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadTracksListAPI(   //большие вопросы насчёт данного итератора.....// -------------------------> //UseCase
    //тут возможно надо добавить объект для сохранения полученного респонса с треками, мы же их куда-то сохраняем, перед тем, как вывести....
    //возможно это(сохранение) можно можно сделать, через данный класс
    private val retrofitConfiguration: RetrofitConfiguration
   // private val ui : UIUpdate,
   // private val activity: Activity,
 //   private val clickDebounce : () -> Boolean,
  //  private val isconnected : Boolean
) {
    fun execute(callString:String, callback: (ITunesDTO) -> Unit, onResponse:(Response<ITunesDTO>)->Unit, onFailure:(Call<ITunesDTO>)->Unit){//, ui:UIUpdate){
        //чтобы изолировать класс DATA от DOMAIN надо в передавать в execute класс Track вместо ITunesDTO класса, далее в методе
        //retrofitConfiguration.loadResults реализующем метод enqueue в котором переопределяютя методы onResponse и onFailure
        // в onResponse произведём преобразование полученного ответа от сервера из ITunesDTO в Track
       retrofitConfiguration.loadResults(retrofitConfiguration.configureRetrofit(), callString, object : Callback<ITunesDTO>{
           override fun onResponse(call: Call<ITunesDTO>, response: Response<ITunesDTO>) {
                   response.body()?.let {
                       val track = it
                       callback(track)
                       onResponse(response)
                   }
           }

           override fun onFailure(call: Call<ITunesDTO>, t: Throwable) {
              onFailure(call)
           }
       })


    }
}