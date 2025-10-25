package com.drcote.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.drcote.playlistmaker.model.Track
import com.drcote.playlistmaker.util.dpToPixel

class AudioPlayerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var isPrepared = false
    private var isPlayingNow = false

    private lateinit var backButton: Button
    private lateinit var playButton: ImageButton
    protected lateinit var paybackDuration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backButton = findViewById(R.id.track_button_back)
        playButton = findViewById(R.id.play_button)
        paybackDuration = findViewById(R.id.playback_duration)
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
        val previewUrl = track.previewUrl

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

        updateDuration(0)
        updateUi()

        if (!previewUrl.isNullOrBlank()) {
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener {
                    isPrepared = true
                    updateUi()
                    updateDuration(0)
                }
                setOnCompletionListener {
                    isPlayingNow = false
                    seekTo(0)
                    updateDuration(0)
                    updateUi()
                }
                setOnErrorListener { _, _, _ ->
                    isPrepared = false
                    isPlayingNow = false
                    updateUi()
                    true
                }
                setDataSource(previewUrl)
                prepareAsync()
            }
        } else {
            playButton.isEnabled = false
        }

        playButton.setOnClickListener {
            when {
                !isPrepared -> Unit
                isPlayingNow -> pausePlayback()
                else -> startPlayback()
            }
        }

        backButton.setOnClickListener({ stopAndFinish() })
    }

    private fun startPlayback() {
        val mp = mediaPlayer ?: return
        isPlayingNow = true
        mp.start()
        updateUi()

        val updateRunnable = object : Runnable {
            override fun run() {
                if (isPlayingNow && mp.isPlaying) {
                    updateDuration(mp.currentPosition)
                    handler.postDelayed(this, 500L)
                }
            }
        }
        handler.post(updateRunnable)
    }

    private fun pausePlayback() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        isPlayingNow = false
        updateUi()
    }

    private fun stopAndFinish() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        isPrepared = false
        isPlayingNow = false
        finish()
    }

    private fun updateUi() {
        playButton.setImageResource(
            if (isPlayingNow) R.drawable.ic_pause_84 else R.drawable.ic_play_84
        )
        playButton.isEnabled = mediaPlayer != null && isPrepared
    }

    private fun updateDuration(ms:Int){
        paybackDuration.text = ms.toMmSs()
    }

    override fun onPause() {
        super.onPause()
        if(isPlayingNow) pausePlayback()
    }

    override fun onBackPressed() {
        stopAndFinish()
    }

    companion object {
        const val EXTRA_TRACK = "track"
    }
}

private fun Int.toMmSs(): String {
    val totalSec = this / 1000
    val m = totalSec / 60
    val s = totalSec % 60
    return "%02d:%02d".format(m, s)
}