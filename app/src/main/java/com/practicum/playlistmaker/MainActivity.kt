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
         //   Log.w("MainActivity", CallbackActivityUse.v.toString())

            //CallbackActivityUse
//            if(SearchActivity.trackForLibraryActivity == null) {
//                SearchActivity.trackForLibraryActivity = Gson().fromJson(
//                    SearchActivity.sharedPreferences.getString(
//                        "last_track_history",
//                        null
//                    ), Result::class.java
//                )
//            }


            //SearchActivity.trackForLibraryActivity
//            Log.w("ma2","${Gson().fromJson(SearchActivity.sharedPreferences.getString("last_track_history", null), Result::class.java)}")
          //  Log.w("ma1","${SearchActivity.trackForLibraryActivity.toString()}")
//            Log.w("ma2","${Gson().fromJson(SearchActivity.sharedPreferences.getString("last_track_history", null), Result::class.java)}")
            LibraryActivity.ACTIVITY = MainActivity()
            startActivity(Intent(this, LibraryActivity::class.java))//CallbackActivityUse.backToActivity(MainActivity(), LibraryActivity())!!::class.java)) //LibraryActivity ActivityForMe
        finish()
        }
        settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
            finish()
        }
    }
}