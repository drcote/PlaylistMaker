package com.drcote.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FindActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    var searchValue: String = SEARCH_VALUE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)
        val backButton = findViewById<Button>(R.id.back_button)
        val clearButton = findViewById<ImageView>(R.id.clear)
        editText = findViewById<EditText>(R.id.search_edit_text)

        editText.setText(searchValue)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchValue = s.toString()
            }

        }

        editText.addTextChangedListener(simpleTextWatcher)

        backButton.setOnClickListener({
            finish()
        })
        clearButton.setOnClickListener({
            editText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    private companion object {
        const val SEARCH = "SEARCH"
        const val SEARCH_VALUE = ""
    }
}

