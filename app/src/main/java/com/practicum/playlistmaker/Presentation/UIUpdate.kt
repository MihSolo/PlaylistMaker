package com.practicum.playlistmaker.Presentation

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.practicum.playlistmaker.Data.ITunesDTO
import com.practicum.playlistmaker.Presentation.UI.SearchActivity
import retrofit2.Response


//класс для изменения пользовательского интерфейса в зависимости от полученного ответа API,
// для активити SearchActivity
class UIUpdate(
//    private val trackListRecyclerView : RecyclerView,
//    private val noSong : LinearLayout,
//    private val noInternet : LinearLayout,
//    private val progressBar: ProgressBar
) {



    fun uiRefreshOnFailureMethod(activity: Activity, isconnected: Boolean){//, response:Response<ITunesDTO>){
//           view.findViewById<Button>(R.id.backToMainArrow).visibility = View.GONE

//        if(response.isSuccessful.not() && activity is SearchActivity ){
//            activity.binding.trackListRecyclerView.visibility = View.GONE
//            activity.binding.noSong.visibility = View.GONE
//            activity.binding.noInternet.visibility = View.VISIBLE
//            activity.binding.progressBar.visibility = View.GONE
//        }
        Toast.makeText(activity,"uiRefreshOnFailureMethod", Toast.LENGTH_LONG).show()
        if (isconnected.not() && activity is SearchActivity) {

//            trackListRecyclerView.visibility = View.GONE
//            noSong.visibility = View.GONE
//            noInternet.visibility = View.VISIBLE
//            progressBar.visibility = View.GONE

            activity.binding.trackListRecyclerView.visibility = View.GONE
            activity.binding.noSong.visibility = View.GONE
            activity.binding.noInternet.visibility = View.VISIBLE
            activity.binding.progressBar.visibility = View.GONE


        }
    }


                               //избавляемся от конкретной activity,  и добавляем в параметры лямда функцию, которой задаём адаптер для конкретной активити
    fun uiRefreshOnResponseMethod(response: Response<ITunesDTO>,
                                  activity: Activity, clickDebounce:() -> Boolean){
                                   if(activity is SearchActivity){
        activity.binding.progressBar.visibility = View.GONE    //?????????????????????????????????
                                       Toast.makeText(activity, "${response.code().toString()}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(

//            activity,//this@SearchActivity,
//            "${response?.code().toString()}",
//            Toast.LENGTH_SHORT
//        ).show()
        if (response.body() != null && (response.body()!!.resultCount > 0)) {
            Toast.makeText(activity, "response not null", Toast.LENGTH_SHORT).show()
            Toast.makeText(activity, "${response.body().toString()}", Toast.LENGTH_SHORT).show()
            if (response.isSuccessful){//.results.isEmpty().not()){     //isSuccessful) {
                Toast.makeText(activity, "isSuccessful", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(activity, "clickDebounce", Toast.LENGTH_SHORT).show()
                        activity.binding.trackListRecyclerView.adapter = activity.tracksAdapter  //TrackListAdapter    -> SearchActivity
                        activity.binding.trackListRecyclerView.visibility = View.VISIBLE
                    }
                }

            } else {

                if (response.code() != 200) {//response.results.isEmpty().not()){//response.code() != 200) {
                    Toast.makeText(activity, " != 200", Toast.LENGTH_SHORT).show()

                    activity.binding.trackListRecyclerView.visibility = View.GONE
                    activity.binding.noSong.visibility = View.GONE
                    activity.binding.noInternet.visibility = View.VISIBLE

                }
                if (response.body()?.resultCount == 0 && response.code() == 200) {
                    Toast.makeText(activity, "resultCount == 0", Toast.LENGTH_SHORT).show()

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
