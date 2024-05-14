package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.time.temporal.TemporalAmount
import kotlin.concurrent.thread

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToMainSearch = findViewById<Button>(R.id.backToMain)
        val searchButton = findViewById<ImageView>(R.id.searchButton)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val linearLayoutGroupBorder = findViewById<LinearLayout>(R.id.LLGroup)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        var textFromTextField = ""

        backToMainSearch.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainIntent)
        }

        clearButton.setOnClickListener {
            editText.setText("")
        }

        this.addOnContextAvailableListener {
            editText.setHint("")
            loadHint {
                editText.setHint(it)
            }
        }

        searchButton.setOnClickListener {
            val text = "результат поиска по заданному значению: " + textFromTextField
            Toast.makeText(this@SearchActivity, text, Toast.LENGTH_SHORT).show()
        }

        val resetTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                if (clearButton.isVisible.not()) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
                }
            }
        }
        editText.addTextChangedListener(resetTextWatcher)

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty().not()) {
                    textFromTextField = s.toString()
                }

            }
        }
        editText.addTextChangedListener(searchTextWatcher)

        val textFieldWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                stringWatcherTextEdit = s.toString()
            }
        }
        editText.addTextChangedListener(textFieldWatcher)


    }

    var stringWatcherTextEdit: String = DEF_VALUE

    companion object {
        const val AMOUNT_KEY = "AMOUNT_KEY"
        const val DEF_VALUE = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(AMOUNT_KEY, stringWatcherTextEdit)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringWatcherTextEdit = savedInstanceState.getString(AMOUNT_KEY, DEF_VALUE)
    }

    //------------------

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun loadHint(callback: (String) -> Unit) {
        thread {
            Thread.sleep(5000)
            callback.invoke("Поиск")
        }
    }

}
