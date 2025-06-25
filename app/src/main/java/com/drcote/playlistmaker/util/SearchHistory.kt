package com.drcote.playlistmaker.util

import android.content.SharedPreferences
import androidx.lifecycle.LifecycleEventObserver
import com.drcote.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val prefs: SharedPreferences) {
    private val gson = Gson()
    private val observers = mutableListOf<() -> Unit>()

    fun getHistory(): ArrayList<Track> {
        val json = prefs.getString(PrefsKeys.SEARCH_HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTrack(track: Track) {
        val history = getHistory()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > PrefsKeys.SEARCH_HISTORY_LIMIT) {
            history.removeLast()
        }
        prefs.edit().putString(PrefsKeys.SEARCH_HISTORY_KEY, gson.toJson(history)).apply()
        notifyObservers()
    }

    fun clear() {
        prefs.edit().remove(PrefsKeys.SEARCH_HISTORY_KEY).apply()
        notifyObservers()
    }

    fun addObserver(observer: () -> Unit) {
        observers.add(observer)
    }

    private fun notifyObservers() {
        observers.forEach { it.invoke() }
    }
}