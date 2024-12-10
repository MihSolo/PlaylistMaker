package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainSettings = findViewById<Button>(R.id.backToMain)
        val sharing = findViewById<FrameLayout>(R.id.sharing)
        val support = findViewById<FrameLayout>(R.id.support)
        val customerAgreement = findViewById<FrameLayout>(R.id.customerAgreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        backToMainSettings.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            finish()
        }

        sharing.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.androidDeveloper))
                type = "text/plain"
            }
            val chooser: Intent = Intent.createChooser(sendIntent, title)
            startActivity(chooser)
        }

        support.setOnClickListener {
            val shareInt = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                type = "ext/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("mihailsologubs@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_for_support))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_for_support))
            }
            startActivity(createChooser(shareInt, title))
        }

        customerAgreement.setOnClickListener {
            val agreementIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.customer_agreement))
            }
            startActivity(agreementIntent)
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}

