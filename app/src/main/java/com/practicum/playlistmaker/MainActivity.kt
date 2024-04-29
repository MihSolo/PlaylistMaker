package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val libraryButton = findViewById<Button>(R.id.library)
        val searchButton = findViewById<Button>(R.id.search)
        val settingButton = findViewById<Button>(R.id.settings)
        val settingButtonClickListener:View.OnClickListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "сработал Toast из настроек", Toast.LENGTH_SHORT).show()
            }
        }
        settingButton.setOnClickListener(settingButtonClickListener)
        searchButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "сработал Toast из поиска", Toast.LENGTH_SHORT).show()
        }
        libraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "сработал Toast из библиотеки", Toast.LENGTH_SHORT).show()
        }
    }
}