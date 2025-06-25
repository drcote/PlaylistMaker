package com.drcote.playlistmaker.network

import com.drcote.playlistmaker.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search?entry=song")
    fun searchSongs(@Query("term") term: String): Call<SearchResponse>
}