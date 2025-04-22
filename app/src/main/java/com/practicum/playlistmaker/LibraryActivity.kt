package com.practicum.playlistmaker

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs


class LibraryActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    companion object {
        const val MIN_DISTANCE = 150
        private const val CLICKPLAY_DEBOUNCE_DELAY = 700L
        lateinit var ACTIVITY: Activity
        private var STATE_DEFAULT = 0
        private var STATE_PREPARED = 1
        private var STATE_PLAYING = 2
        private var STATE_PAUSED = 3
        private var DEMOTIME = 30000L
        private const val ONE_SEC = 1000L
    }

    lateinit var sharedPreferences: SharedPreferences
    private var isClickAllowed = true
    lateinit var gestureDetector: GestureDetector
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    val handler = Handler(Looper.getMainLooper())
    var x2: Float = 0.0f
    var x1: Float = 0.0f
    var y2: Float = 0.0f
    var y1: Float = 0.0f

    private val binding: ActivityLibraryBinding by lazy {
        ActivityLibraryBinding.inflate(layoutInflater)
    }

    private val bindingSettingsActivity: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var marker: Boolean = true
        if (marker) {
            sharedPreferences = getSharedPreferences("last track", MODE_PRIVATE)
            marker = false
        }
        var trackForLibraryActivity = Gson().fromJson(
            sharedPreferences.getString("last_track_history", null),
            Result::class.java
        )

        if (trackForLibraryActivity != null) {
            Log.w(
                "trackForLibraryActivity.previewUrl", "${trackForLibraryActivity.previewUrl}"
            )
            preparePlayer(trackForLibraryActivity)

            binding.playButtonCenter.setOnClickListener {

                if (clickDebounce()) {
                    if (!marker) {
                        DEMOTIME = 30000L
                        marker = true
                    }
                    playbackControl()
                }
            }

            Log.w("trackForLibraryActivity", "${trackForLibraryActivity.toString()}")
            binding.trackContryValueView.setText(trackForLibraryActivity.country)
            binding.trackStyleValueView.setText(trackForLibraryActivity.primaryGenreName)  // жанр
            binding.trackYearValueView.setText(
                trackForLibraryActivity.releaseDate.toString()
                    .reversed().subSequence(0, 4).reversed().toString()
            )
            if (trackForLibraryActivity.collectionName.isNullOrEmpty().not()) {
                binding.trackAlbumValueView?.setText(trackForLibraryActivity.collectionName)
                binding.albumViewVisibilityControl.onVisibilityAggregated(true)
            } else {
                binding.albumViewVisibilityControl.onVisibilityAggregated(false)
            }
            binding.trackDurationValueView.setText(
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(trackForLibraryActivity.trackTimeMillis).toString()
            )
            binding.activePlayTime.setText("0.00")   // активеое время проигрывания
            binding.artistNameView.setText(trackForLibraryActivity.artistName)
            binding.trackNameView.setText(trackForLibraryActivity.trackName)
            Glide.with(binding.songCoverImage)
                .load(
                    trackForLibraryActivity.artworkUrl100.replaceAfterLast(
                        '/',
                        "512x512bb.jpg"
                    )
                )
                .placeholder(
                    R.drawable
                        .error_light_label_big
                )
                .fitCenter()
                .transform(
                    RoundedCorners(
                        binding.songCoverImage.context.resources.getDimensionPixelSize(
                            R.dimen.trackLabelRadius
                        )
                    )
                )
                .into(binding.songCoverImage)
        } else {
            if (!App().darkTheme) {
                binding.likeButtonLeft.setImageDrawable(getDrawable(R.drawable.like_button_light_plh))
                binding.addButtonRight.setImageDrawable(getDrawable(R.drawable.add_button_right_light_plh))
                binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.play_button_center_light_plh))
                binding.trackContryValueView.setText("inactive")
                binding.trackStyleValueView.setText("inactive")  // жанр
                binding.trackYearValueView.setText("inactive")
                binding.trackAlbumValueView?.setText("inactive")
                binding.trackDurationValueView.setText("0:00")
                binding.activePlayTime.setText("0.00")
                binding.artistNameView.setText("inactive")
                binding.trackNameView.setText("inactive")
                Glide.with(binding.songCoverImage)
                    .load(
                        R.drawable
                            .error_light_label_big
                    )
                    .placeholder(
                        R.drawable
                            .error_light_label_big
                    )
                    .fitCenter()
                    .transform(
                        RoundedCorners(
                            binding.songCoverImage.context.resources.getDimensionPixelSize(
                                R.dimen.trackLabelRadius
                            )
                        )
                    )
                    .into(binding.songCoverImage)
            } else {
                binding.likeButtonLeft.setImageDrawable(getDrawable(R.drawable.like_button_night_plh))
                binding.addButtonRight.setImageDrawable(getDrawable(R.drawable.add_button_right_light_plh))
                binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.play_button_center_night_plh))
                binding.trackContryValueView.setText("")
                binding.trackStyleValueView.setText("")  // жанр
                binding.trackYearValueView.setText("")
                binding.trackAlbumValueView?.setText("")
                binding.trackDurationValueView.setText("0:00")
                binding.activePlayTime.setText("0.00")   //---------------------------------text
                binding.artistNameView.setText("")
                binding.trackNameView.setText("")
                Glide.with(binding.songCoverImage)
                    .load(
                        R.drawable
                            .error_light_label_big
                    )
                    .placeholder(
                        R.drawable
                            .error_light_label_big
                    )
                    .fitCenter()
                    .transform(
                        RoundedCorners(
                            binding.songCoverImage.context.resources.getDimensionPixelSize(
                                R.dimen.trackLabelRadius
                            )
                        )
                    )
                    .into(binding.songCoverImage)
            }

        }

        val backToActivity =
            findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.backToMainArrow) //-----------------------

        backToActivity.setNavigationOnClickListener {
            startActivity(
                Intent(this, ACTIVITY::class.java)
            )
            finish()
        }

        gestureDetector = GestureDetector(this, this)

        backToActivity.setOnClickListener {
            val backToMainIntent = Intent(
                this, ACTIVITY::class.java
            )
            startActivity(backToMainIntent)
            finish()
        }


    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event!!)

        when (event?.action) {
            0 -> {
                x1 = event.x
                y1 = event.y
            }

            1 -> {
                x2 = event.x
                y2 = event.y

                val valueX = x2 - x1
                val valueY = y2 - y1

                if (abs(valueX) > MIN_DISTANCE) {
                    if (x2 > x1) {
                        //LSW
                        Toast.makeText(this@LibraryActivity, "LSW", Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(
                                this, ACTIVITY::class.java
                            )
                        )
                        finish()
                    } else {
                        //RSW
                    }
                } else if (abs(valueY) > MIN_DISTANCE) {
                    if (y2 > y1) {
                        //BSW
                    } else {
                        //TSW
                    }
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean = false

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean = false

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean = false

    override fun onLongPress(e: MotionEvent) = Unit

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false


    private fun preparePlayer(trackForLibraryActivity: Result) {
        mediaPlayer.setDataSource(trackForLibraryActivity.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

    private fun startPlayer() {
        mediaPlayer.start()
        startTimer(DEMOTIME)
        playerState = STATE_PLAYING
        if (isDarkModeOn()) {
            binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.play_button_center_night_plh))
        } else {
            binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.play_button_center_light_plh))
        }
    }


    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        if (isDarkModeOn()) {
            binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.button_night))
        } else {
            binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.button_light_play))
        }
    }


    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun startTimer(secondsDuration: Long) {
        val startTime = System.currentTimeMillis()

        handler?.post(
            createUpdateTimerTask(startTime, secondsDuration)
        )
    }


    private fun createUpdateTimerTask(startTime: Long, secondsDuration: Long): Runnable {
        return object : Runnable {
            override fun run() {
                // Сколько прошло времени с момента запуска таймера
                var timeAfterStart = System.currentTimeMillis() - startTime
                // Сколько осталось до конца
                var remainingTime = secondsDuration - timeAfterStart

                if (remainingTime > 0 && playerState == 3 || playerState == 1) {
                    DEMOTIME = remainingTime
                } else {
                    DEMOTIME = 30000L
                }

                var seconds = remainingTime / ONE_SEC

                if (remainingTime > 0 && playerState == 2) {  //pause
                    binding.activePlayTime?.text =
                        String.format("%d:%02d", (seconds) / 60, (seconds) % 60)
                    handler?.postDelayed(this, ONE_SEC)
                } else {
                    if (isDarkModeOn()) {
                        binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.button_night))
                    } else {
                        binding.playButtonCenter.setImageDrawable(getDrawable(R.drawable.button_light_play))
                    }
                }
                if (remainingTime <= 0) {
                    onDestroy()
                }

                if (remainingTime <= 0 && playerState == 0) {
                    DEMOTIME = 30000L
                }
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICKPLAY_DEBOUNCE_DELAY)
        }
        return current
    }


}