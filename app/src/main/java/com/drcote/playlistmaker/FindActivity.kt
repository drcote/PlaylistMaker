package com.drcote.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drcote.playlistmaker.model.SearchResponse
import com.drcote.playlistmaker.model.Track
import com.drcote.playlistmaker.model.TrackApi
import com.drcote.playlistmaker.network.ITunesApiService
import com.drcote.playlistmaker.ui.tracklist.TrackAdapter
import com.drcote.playlistmaker.util.PrefsKeys
import com.drcote.playlistmaker.util.SearchHistory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class FindActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ITunesApiService
    private lateinit var editText: EditText
    private lateinit var recycleView: RecyclerView
    private lateinit var recycleViewHistory: RecyclerView
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    var searchValue: String = SEARCH_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)
        val backButton = findViewById<Button>(R.id.back_button)
        val clearButton = findViewById<ImageView>(R.id.clear)
        val clearHistoryButton = findViewById<Button>(R.id.clear_history)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        historyLayout = findViewById(R.id.history_layout)
        val retryButton = findViewById<Button>(R.id.retryButton)
        editText = findViewById(R.id.search_edit_text)
        recycleView = findViewById(R.id.recycler_view)
        recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleViewHistory = findViewById(R.id.recycler_view_history)
        recycleViewHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val prefs = getSharedPreferences(PrefsKeys.SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)

        historyAdapter = TrackAdapter(searchHistory.getHistory()) { track ->
            searchHistory.addTrack(track)
            val intent = Intent(this@FindActivity, AudioPlayerActivity::class.java)
            intent.putExtra(AudioPlayerActivity.EXTRA_TRACK, track)
            startActivity(intent)
        }

        recycleViewHistory.adapter = historyAdapter

        searchHistory.addObserver {
            val newHistory = searchHistory.getHistory()
            historyAdapter.updateData(newHistory)
            showHistoryLayout(editText.hasFocus(), editText.text.isEmpty())
        }

        editText.setText(searchValue)

        retrofit = Retrofit.Builder().baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        apiService = retrofit.create(ITunesApiService::class.java)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchValue = s.toString()
                showHistoryLayout(editText.hasFocus(), s.isNullOrEmpty())
            }

        }

        editText.addTextChangedListener(simpleTextWatcher)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchValue)
                true
            } else {
                false
            }
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            showHistoryLayout(
                hasFocus,
                editText.text.isEmpty()
            )
        }

        clearHistoryButton.setOnClickListener({
            searchHistory.clear();
            showHistoryLayout(false, false)
            (recycleViewHistory.adapter as? TrackAdapter)?.updateData(searchHistory.getHistory())
        })

        retryButton.setOnClickListener({
            search(searchValue)
        })

        backButton.setOnClickListener({
            finish()
        })
        clearButton.setOnClickListener({
            editText.setText("")
            recycleView.adapter = TrackAdapter(emptyList()) {}
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.clearFocus()
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedInstance = savedInstanceState.getString(SEARCH)
        if (savedInstance != null) {
            searchValue = savedInstance
            editText.setText(searchValue)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search(query: String) {
        apiService.searchSongs(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>, response: Response<SearchResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!.results
                    if (results.isEmpty()) {
                        showEmptyPlaceholder()
                    } else {
                        val tracks = results.map { mapTrackApiToTrack(it) }
                        showResults()
                        recycleView.adapter =
                            TrackAdapter(tracks) { track ->
                                searchHistory.addTrack(track)
                                val intent =
                                    Intent(this@FindActivity, AudioPlayerActivity::class.java)
                                intent.putExtra(AudioPlayerActivity.EXTRA_TRACK, track)
                                startActivity(intent)
                            }
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showErrorPlaceholder()
            }

        })
    }

    private fun showResults() {
        recycleView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showEmptyPlaceholder() {
        recycleView.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        recycleView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
    }

    private fun showHistoryLayout(hasFocus: Boolean, isEmpty: Boolean) {
        val show = hasFocus && isEmpty && searchHistory.getHistory().isNotEmpty()
        historyLayout.visibility = if (show) View.VISIBLE else View.GONE
        recycleView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun formatTrackTime(millis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    private fun mapTrackApiToTrack(api: TrackApi): Track {
        return Track(
            trackName = api.trackName ?: "",
            artistName = api.artistName ?: "",
            artworkUrl100 = api.artworkUrl100 ?: "",
            trackTime = formatTrackTime(api.trackTimeMillis ?: 0),
            trackId = api.trackId ?: 0,
            primaryGenreName = api.primaryGenreName ?: "",
            collectionName = api.collectionName ?: "",
            releaseDate = api.releaseDate ?: "",
            country = api.country ?: ""
        )
    }

    private companion object {
        const val SEARCH = "SEARCH"
        const val SEARCH_VALUE = ""
    }

}

