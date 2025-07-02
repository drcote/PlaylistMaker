package com.drcote.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.drcote.playlistmaker.util.PrefsKeys

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val findButton = findViewById<Button>(R.id.find_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        findButton.setOnClickListener {
            startActivity(Intent(this, FindActivity::class.java))
        }

        mediaButton.setOnClickListener {
            startActivity(Intent(this, AudioPlayerActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(PrefsKeys.SETTINGS_PREFS, Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean(PrefsKeys.DARK_THEME_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}