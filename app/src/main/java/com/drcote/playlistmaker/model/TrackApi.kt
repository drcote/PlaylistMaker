package com.drcote.playlistmaker.model

data class TrackApi(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)
