package com.practicum.playlistmaker

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.internal.http2.Http2Connection
import retrofit2.Response


//класс для изменения пользовательского интерфейса в зависимости от полученного ответа API,
// для активити SearchActivity
class UIUpdate(
//    private val trackListRecyclerView : RecyclerView,
//    private val noSong : LinearLayout,
//    private val noInternet : LinearLayout,
//    private val progressBar: ProgressBar
) {

    fun uiRefreshOnFailureMethod(activity: Activity, isconnected: (Activity) -> Boolean){
//           view.findViewById<Button>(R.id.backToMainArrow).visibility = View.GONE
        if (isconnected.invoke(activity).not() && activity is SearchActivity) {
            activity.binding.trackListRecyclerView.visibility = View.GONE
            activity.binding.noSong.visibility = View.GONE
            activity.binding.noInternet.visibility = View.VISIBLE
            activity.binding.progressBar.visibility = View.GONE
        }
    }


                               //избавляемся от конкретной activity,  и добавляем в параметры лямда функцию, которой задаём адаптер для конкретной активити
    fun uiRefreshOnResponseMethod(response: Response<ITunesDTO>,
                                  activity: Activity,clickDebounce:() -> Boolean){
                                   if(activity is SearchActivity){
        activity.binding.progressBar.visibility = View.GONE    //?????????????????????????????????
//        Toast.makeText(
//            activity,//this@SearchActivity,
//            "${response?.code().toString()}",
//            Toast.LENGTH_SHORT
//        ).show()
        if (response != null) {
            Toast.makeText(activity, "response not null", Toast.LENGTH_SHORT).show()
            if (response.isSuccessful) {
                activity.binding.noSong.visibility = View.GONE
                activity.binding.noInternet.visibility = View.GONE 
//                if(activity is SearchActivity){
//                response.body()?.let {
//                    activity.tracksAdapter = TrackListAdapter(   //trackAdapter -> TrackListAdapter
//                        it.results,
//                        activity  //SearchActivity
//                    )


//                    tracksAdapter( TrackListAdapter(
//                            it.results,
//                            activity
//                        ))
                    //:  можно добавить возврат error
                    if (clickDebounce()) {
                        activity.binding.trackListRecyclerView.adapter = activity.tracksAdapter  //TrackListAdapter    -> SearchActivity
                        activity.binding.trackListRecyclerView.visibility = View.VISIBLE
                    }
                }

            } else {

                if (response.code() != 200) {
                    activity.binding.trackListRecyclerView.visibility = View.GONE
                    activity.binding.noSong.visibility = View.GONE
                    activity.binding.noInternet.visibility = View.VISIBLE

                }
                if (response.body()?.resultCount == 0) {
                    activity.binding.trackListRecyclerView.visibility = View.GONE
                    activity.binding.noSong.visibility = View.VISIBLE
                }
            }
//        }
                                   }else{
                                       Toast.makeText(activity,"nused not correct Activity", Toast.LENGTH_LONG).show()
                                   }
    }




}
