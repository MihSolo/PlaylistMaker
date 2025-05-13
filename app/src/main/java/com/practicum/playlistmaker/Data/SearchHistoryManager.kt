package com.practicum.playlistmaker.Data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.Domain.Track

class SearchHistoryManager(private val context : Context) { //замена  for SearchHistory
    private val  sharedPreferences: SharedPreferences
    = context.getSharedPreferences("SEARCH_HISTORY", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun getHistory(): List<Track> {   //get
        val json = sharedPreferences.getString("history_list", null) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    fun setHistory(history: List<Track>) {   //save
        val json = Gson().toJson(history)
        sharedPreferences.edit()
            .putString("history_list", json)
            .apply()
    }

    fun add(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) {
            history.removeLast()
        }
        setHistory(history)
    }


    fun clearHistory() {
        sharedPreferences.edit().remove("history_list").apply()
    }
}