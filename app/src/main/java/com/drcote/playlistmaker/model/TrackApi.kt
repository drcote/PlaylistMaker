package com.drcote.playlistmaker.model

import com.google.gson.annotations.SerializedName

data class TrackApi(
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    @SerializedName("trackId") val trackId: Long?
)
