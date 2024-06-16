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
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import java.lang.Thread.sleep
import java.time.temporal.TemporalAmount
import kotlin.concurrent.thread

class SearchActivity : AppCompatActivity() {

    var stringWatcherTextEdit: String = DEF_VALUE

    companion object {
        const val AMOUNT_KEY = "AMOUNT_KEY"
        const val DEF_VALUE = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToMainSearch = findViewById<Button>(R.id.backToMain)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val textInputLayoutSearch = findViewById<TextInputLayout>(R.id.textInputLayoutSearch)
        var textFromTextField = DEF_VALUE

        backToMainSearch.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            finish()
        }


        textInputLayoutSearch.setStartIconOnClickListener {
            val text = "результат поиска по заданному значению: " + textFromTextField
            Toast.makeText(this@SearchActivity, text, Toast.LENGTH_SHORT).show()
        }

        textInputLayoutSearch.setEndIconOnClickListener {
            editText.setText("")
        }

        val textInputLayout = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayoutSearch.isEndIconVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty().not()) {
                    textFromTextField = s.toString()
                    stringWatcherTextEdit = s.toString()
                }

                if (textInputLayoutSearch.isEndIconVisible.not()) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
                }
            }
        }
        editText.addTextChangedListener(textInputLayout)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(AMOUNT_KEY, stringWatcherTextEdit)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringWatcherTextEdit = savedInstanceState.getString(AMOUNT_KEY, DEF_VALUE)
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return if (s.isNullOrEmpty()) {
            false
        } else {
            true
        }
    }

}
