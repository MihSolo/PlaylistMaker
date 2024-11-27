package com.practicum.playlistmaker


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    var stringWatcherTextEdit: String = DEF_VALUE

    companion object {
        const val AMOUNT_KEY = "AMOUNT_KEY"
        const val DEF_VALUE = ""
    }

    val baseApiUrl = "https://itunes.apple.com"
    lateinit var iTunesAPI: ITunesSearchAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val backToMainSearch = findViewById<Button>(R.id.backToMain)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val textInputLayoutSearch = findViewById<TextInputLayout>(R.id.textInputLayoutSearch)
        var textFromTextField = DEF_VALUE
        val recyclerView = findViewById<RecyclerView>(R.id.trackList)
        val noInternetView = findViewById<LinearLayout>(R.id.no_internet)
        val noSongView = findViewById<LinearLayout>(R.id.no_song)
        val refreshButton = findViewById<Button>(R.id.refreshButton)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        backToMainSearch.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            finish()
        }

        textInputLayoutSearch.setStartIconOnClickListener {
            val text = "результат поиска по заданному значению: " + textFromTextField
            Toast.makeText(this@SearchActivity, text, Toast.LENGTH_SHORT).show()
            editText.setText("")
        }

        textInputLayoutSearch.setEndIconOnClickListener {
            editText.setText("")
            recyclerView.visibility = View.GONE
            noSongView.visibility = View.GONE
            noInternetView.visibility = View.GONE
        }

        val textInputLayout = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

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

                editText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val address: String
                        address = s?.toString() ?: "try again"
                        createAPIresponse(address)

                    }
                    if (refreshButton.isClickable) {
                        refreshButton.setOnClickListener {
                            recyclerView.visibility = View.GONE
                            noSongView.visibility = View.GONE
                            noInternetView.visibility = View.GONE

                            createAPIresponse(textFromTextField)
                            Toast.makeText(
                                this@SearchActivity,
                                "${textFromTextField} - не найдено. Проверьте доступ к интернету.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    false
                }
            }


            fun createAPIresponse(valueForRequest: String) {
                iTunesAPI = configureRetrofit()
                iTunesAPI.search(valueForRequest)
                    .enqueue(object : Callback<ITunesDTO> {

                        override fun onResponse(
                            call: Call<ITunesDTO>,
                            response: Response<ITunesDTO>
                        ) {
                            val trackList = response.body()
                            if (response.isSuccessful && trackList?.resultCount != 0) {
                                noSongView.visibility = View.GONE
                                noInternetView.visibility = View.GONE

                                Log.w("RESPONSE", "${response.body()}")
                                trackList?.let {
                                    recyclerView.adapter = TrackListAdapter(trackList.results)
                                    recyclerView.visibility = View.VISIBLE
                                }
                            } else {

                                if (response.code() != 200) {
                                    recyclerView.visibility = View.GONE
                                    noSongView.visibility = View.GONE
                                    noInternetView.visibility = View.VISIBLE

                                }
                                if (trackList?.resultCount == 0) {
                                    recyclerView.visibility = View.GONE
                                    noSongView.visibility = View.VISIBLE
                                }
                            }
                        }

                        override fun onFailure(call: Call<ITunesDTO>, t: Throwable) {
                            if (isConnected(this@SearchActivity).not()) {
                                recyclerView.visibility = View.GONE
                                noSongView.visibility = View.GONE
                                noInternetView.visibility = View.VISIBLE
                            }
                        }
                    })
            }
        }
        editText.addTextChangedListener(textInputLayout)
    }

    private fun configureRetrofit(): ITunesSearchAPI {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseApiUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(ITunesSearchAPI::class.java)

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

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}
