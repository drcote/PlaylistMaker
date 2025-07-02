package com.drcote.playlistmaker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.drcote.playlistmaker.model.Track
import com.drcote.playlistmaker.util.dpToPixel

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val backButton = findViewById<Button>(R.id.track_button_back);
        val artworkUrl = findViewById<ImageView>(R.id.artwork_url)
        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val duration = findViewById<TextView>(R.id.duration)
        val album = findViewById<TextView>(R.id.album)
        val year = findViewById<TextView>(R.id.year)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)

        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)

        if (track == null) {
            finish()
            return
        }

        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = track.trackTime
        album.text = track.collectionName
        year.text = track.releaseDate.take(4)
        genre.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners(dpToPixel(2f)))
            .into(artworkUrl)


        backButton.setOnClickListener({
            finish()
        })
    }

    companion object {
        const val EXTRA_TRACK = "track"
    }
}