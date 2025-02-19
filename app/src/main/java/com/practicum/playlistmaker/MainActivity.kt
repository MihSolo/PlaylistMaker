package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search)
        val settingsButton = findViewById<Button>(R.id.settings)
        val libraryButton = findViewById<Button>(R.id.library)

        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                finish()
            }
        })


        libraryButton.setOnClickListener {
            LibraryActivity.ACTIVITY = MainActivity()
            startActivity(Intent(this, LibraryActivity::class.java))
            finish()
        }
        settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
            finish()
        }
    }
}