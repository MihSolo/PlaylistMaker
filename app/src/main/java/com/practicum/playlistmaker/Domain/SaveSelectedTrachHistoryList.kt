package com.practicum.playlistmaker.Domain

import com.practicum.playlistmaker.Data.SearchHistory

class SaveSelectedTrachHistoryList(  //-------------------------------------> UseCase  for history saving
    private val searchHistory: SearchHistory
) {  //UseCase
    fun execute(history:List<Track>){
        searchHistory.setHistory(history)
    }
}