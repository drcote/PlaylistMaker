package com.drcote.playlistmaker.ui.tracklist

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.drcote.playlistmaker.R
import com.drcote.playlistmaker.model.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val artworkImage: ImageView = itemView.findViewById(R.id.track_artwork)

    fun bind(track: Track) {
        artistName.setText("")
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(dpToPixel(2f)))
            .into(artworkImage)
    }

    private fun dpToPixel(dp: Float): Int {
        val metrics: DisplayMetrics = Resources.getSystem().getDisplayMetrics()
        val px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        return Math.round(px).toInt()
    }

    companion object {
        fun create(parent: ViewGroup): TrackViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
            return TrackViewHolder(view)
        }
    }
}