package com.drcote.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.back_button)
        val share = findViewById<TextView>(R.id.share)
        val support = findViewById<TextView>(R.id.support)
        val userAgreement = findViewById<TextView>(R.id.user_agreement)

        backButton.setOnClickListener {
            finish()
        }

        share.setOnClickListener({
            val url = getString(R.string.share_url)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                url
            )
            startActivity(Intent.createChooser(shareIntent, ""))
        })

        support.setOnClickListener({
            val email = getString(R.string.user_email)
            val subject = getString(R.string.subject_email)
            val body = getString(R.string.body_email)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data =
                Uri.parse("mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
            startActivity(Intent.createChooser(supportIntent, ""))
        })

        userAgreement.setOnClickListener({
            val url = getString(R.string.user_agreement_url)
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(userAgreementIntent);
        })
    }
}