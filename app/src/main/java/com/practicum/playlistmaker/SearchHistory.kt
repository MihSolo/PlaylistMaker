package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.widget.ImageView
import com.google.gson.Gson

class SearchHistory {

    private lateinit var sharedPreferences: SharedPreferences


    companion object {
        private const val HISTORY_LIST_KEY = "history_list"
    }

    fun sharedPreferencesCreated(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    fun getHistory(): List<Result> {
        val json = sharedPreferences.getString(HISTORY_LIST_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Result>::class.java).toList()
    }

    fun setHistory(history: List<Result>) {
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