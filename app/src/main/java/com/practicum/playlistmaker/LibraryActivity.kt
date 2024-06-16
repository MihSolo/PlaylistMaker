package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val backToMainLibrary = findViewById<Button>(R.id.backToMain)

        backToMainLibrary.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            finish()
        }

    }

}