package com.drcote.playlistmaker.ui.tracklist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drcote.playlistmaker.model.Track

class TrackAdapter(private var tracks: List<Track>, private val onClick: (Track) -> Unit) :
    RecyclerView.Adapter<TrackViewHolder>() {

    fun updateData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size
}