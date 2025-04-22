package com.practicum.playlistmaker

import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backToMainArrow.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainIntent)
            finish()
        }

        binding.sharing.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.androidDeveloper))
                type = "text/plain"
            }
            val chooser: Intent = Intent.createChooser(sendIntent, title)
            startActivity(chooser)
        }

        binding.support.setOnClickListener {
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

        binding.customerAgreement.setOnClickListener {
            val agreementIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.customer_agreement))
            }
            startActivity(agreementIntent)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}

