package com.practicum.playlistmaker.Data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.Domain.Result
import com.practicum.playlistmaker.Domain.Track

class SearchHistory { //use cases

    //class SearchHistoryManager(private val context : Context){
    //private val  sharedPreferences: SharedPreferences = context.getSharedPreferences("history_list", Context.MODE_PRIVATE)
    // private val gson = Gson()


    private lateinit var sharedPreferences: SharedPreferences   // net neobhodimosti


    companion object {   //  srazu net neobhodimosti
        private const val HISTORY_LIST_KEY = "history_list"
    }

    fun sharedPreferencesCreated(sharedPreferences: SharedPreferences) {   //net neobhodimosti
        this.sharedPreferences = sharedPreferences
    }

    fun getHistory(): List<Result> {   //get
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Result>::class.java).toList()
    }

    fun setHistory(history: List<Result>) {   //save
        val json = Gson().toJson(history)
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, json)
            .apply()
    }


    fun convertResultToTrack(result: Result): Track {   ////////////////////////////////////////----------------для логов
        val json = Gson().toJson(result)
        return Gson().fromJson(json, Track::class.java)
    }

    fun add(track: Result) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) {
            history.removeLast()
        }
        setHistory(history)
    }


    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_LIST_KEY).apply()
    }

}