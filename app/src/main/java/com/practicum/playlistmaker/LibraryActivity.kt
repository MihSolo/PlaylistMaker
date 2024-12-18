package com.practicum.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs


class LibraryActivity : AppCompatActivity(), GestureDetector.OnGestureListener {


    lateinit var gestureDetector: GestureDetector

    var x2: Float = 0.0f
    var x1: Float = 0.0f
    var y2: Float = 0.0f
    var y1: Float = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
        lateinit var ACTIVITY: Activity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val songCoverImage: ImageView = findViewById(R.id.songCoverImage)
        val track_name_view: TextView = findViewById(R.id.track_name_view)
        val artist_name_view: TextView = findViewById(R.id.artist_name_view)
        val active_play_time: TextView = findViewById(R.id.active_play_time)
        val track_durationValue_view: TextView = findViewById(R.id.track_durationValue_view)
        var track_albumValue_view: TextView? = findViewById(R.id.track_albumValue_view)
        val track_yearValue_view: TextView = findViewById(R.id.track_yearValue_view)
        val track_styleValue_view: TextView = findViewById(R.id.track_styleValue_view)
        val track_contryValue_view: TextView = findViewById(R.id.track_contryValue_view)
        val album_view_visibility_control: Group = findViewById(R.id.album_view_visibility_control)
        val play_button_center_view: ImageButton = findViewById(R.id.play_button_center)
        val add_button_right_button: ImageButton = findViewById(R.id.add_button_right)
        val like_button_left_button: ImageButton = findViewById(R.id.like_button_left)
        val sharedPreferences = getSharedPreferences("last track", MODE_PRIVATE)
        var trackForLibraryActivity = Gson().fromJson(
            sharedPreferences.getString("last_track_history", null),
            Result::class.java
        )

        if (trackForLibraryActivity != null) {
            Log.w("trackForLibraryActivity", "${trackForLibraryActivity.toString()}")
            track_contryValue_view.setText(trackForLibraryActivity.country)
            track_styleValue_view.setText(trackForLibraryActivity.primaryGenreName)  // жанр
            track_yearValue_view.setText(
                trackForLibraryActivity.releaseDate.toString()
                    .reversed().subSequence(0, 4).reversed().toString()
            )
            if (trackForLibraryActivity.collectionName.isNullOrEmpty().not()) {
                track_albumValue_view?.setText(trackForLibraryActivity.collectionName)
                album_view_visibility_control.onVisibilityAggregated(true)
            } else {
                album_view_visibility_control.onVisibilityAggregated(false)
            }
            track_durationValue_view.setText(
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(trackForLibraryActivity.trackTimeMillis).toString()
            )
            active_play_time.setText("0.00")   // активеое время проигрывания
            artist_name_view.setText(trackForLibraryActivity.artistName)
            track_name_view.setText(trackForLibraryActivity.trackName)
            Glide.with(songCoverImage)
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
                .transform(RoundedCorners(songCoverImage.context.resources.getDimensionPixelSize(R.dimen.trackLabelRadius)))
                .into(songCoverImage)
        } else {
            if (!App().darkTheme) {
                like_button_left_button.setImageDrawable(getDrawable(R.drawable.like_button_light_plh))
                add_button_right_button.setImageDrawable(getDrawable(R.drawable.add_button_right_light_plh))
                play_button_center_view.setImageDrawable(getDrawable(R.drawable.play_button_center_light_plh))
                track_contryValue_view.setText("inactive")
                track_styleValue_view.setText("inactive")  // жанр
                track_yearValue_view.setText("inactive")
                track_albumValue_view?.setText("inactive")
                track_durationValue_view.setText("00:00")
                active_play_time.setText("0.00")
                artist_name_view.setText("inactive")
                track_name_view.setText("inactive")
                Glide.with(songCoverImage)
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
                            songCoverImage.context.resources.getDimensionPixelSize(
                                R.dimen.trackLabelRadius
                            )
                        )
                    )
                    .into(songCoverImage)
            } else {
                like_button_left_button.setImageDrawable(getDrawable(R.drawable.like_button_night_plh))
                add_button_right_button.setImageDrawable(getDrawable(R.drawable.add_button_right_light_plh))
                play_button_center_view.setImageDrawable(getDrawable(R.drawable.play_button_center_night_plh))
                track_contryValue_view.setText("")
                track_styleValue_view.setText("")  // жанр
                track_yearValue_view.setText("")
                track_albumValue_view?.setText("")
                track_durationValue_view.setText("00:00")
                active_play_time.setText("0.00")
                artist_name_view.setText("")
                track_name_view.setText("")
                Glide.with(songCoverImage)
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
                            songCoverImage.context.resources.getDimensionPixelSize(
                                R.dimen.trackLabelRadius
                            )
                        )
                    )
                    .into(songCoverImage)
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


}