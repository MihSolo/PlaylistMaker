package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false


    override fun onCreate() {
        super.onCreate()


        val sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean("night", false)


//        if (!sharedPreferences.contains("night")) {
//            val isSystemDarkTheme = resources.configuration.uiMode and
//                    android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
//            sharedPreferences.edit().putBoolean("night", isSystemDarkTheme).apply()
//        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        getSharedPreferences("Mode", MODE_PRIVATE).edit().putBoolean("night", darkTheme).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}