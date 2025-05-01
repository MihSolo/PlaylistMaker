package com.practicum.playlistmaker

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkService {   // класс обработки ответа от API
//activity:SearchActivity   -> оможно заменить на activity:View, тогда надо переписывать функции в классе UIUpdate
    //и использовать определение View через findViewById<.......>(R.id.....) не используя binding


fun createApi(requestValue:String, ui: UIUpdate,
               activity:Activity,// SearchActivity,
              clickDebounce:() -> Boolean, retrofit:RetrofitConfiguration, isconnected: (Activity) -> Boolean ){
    retrofit.configureRetrofit().search(requestValue).enqueue(object : Callback<ITunesDTO>{
        override fun onResponse(call: Call<ITunesDTO>, response: Response<ITunesDTO>) {
            if(activity is SearchActivity) {
                response.body()?.let {
                    activity.tracksAdapter = TrackListAdapter(   //trackAdapter -> TrackListAdapter
                        it.results,
                        activity  //SearchActivity
                    )
                }
            }
            ui.uiRefreshOnResponseMethod(response, activity, clickDebounce)
        }

        override fun onFailure(call: Call<ITunesDTO>, t: Throwable) {
            ui.uiRefreshOnFailureMethod(activity,isconnected)
        }

    })
}


}