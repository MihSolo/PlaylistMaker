package com.practicum.playlistmaker


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), TrackListAdapter.Listener {

    companion object {
      //  internal var ACTIVITY: Activity? = null
        const val AMOUNT_KEY = "AMOUNT_KEY"
        const val DEF_VALUE = ""
        var trackForLibraryActivity: Result? = null //Gson().fromJson(SearchActivity.sharedPreferences.getString("last_track_history", null), Result::class.java)
        var tackForLibraryActivityHL:Int? = null  //-------------------------------------------
        lateinit var  sharedPreferences:SharedPreferences
    }


//    lateinit internal var ACTIVITY: Activity //--------------------------------------------------
//var trackForLibraryActivity: Result? = Gson().fromJson(SearchActivity.sharedPreferences.getString("last_track_history", null), Result::class.java)

    var stringWatcherTextEdit: String = DEF_VALUE
    val historyTrackLists: MutableList<Result> = mutableListOf()
    val searchHistory = SearchHistory()
    val baseApiUrl = "https://itunes.apple.com"
    lateinit var iTunesAPI: ITunesSearchAPI
    var historyListAdapters: TrackListAdapter =
        TrackListAdapter(historyTrackLists, this@SearchActivity)
    lateinit var watchHistoryList:RecyclerView
    lateinit var recyclerView:RecyclerView
  //  private val adapter =  TrackListAdapter(this)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val backToMainSearch = findViewById<Button>(R.id.backToMainArrow)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val textInputLayoutSearch = findViewById<TextInputLayout>(R.id.textInputLayoutSearch)
        var textFromTextField = DEF_VALUE
         recyclerView = findViewById<RecyclerView>(R.id.trackList)
        val noInternetView = findViewById<LinearLayout>(R.id.no_internet)
        val noSongView = findViewById<LinearLayout>(R.id.no_song)
        val refreshButton = findViewById<Button>(R.id.refreshButton)
         watchHistoryList = findViewById<RecyclerView>(R.id.watchHistoryList)
        val buttonClearHistoryList = findViewById<Button>(R.id.buttonClearHistoryList)
        val trackHistoryView = findViewById<LinearLayout>(R.id.trackHistoryView)
        val linearLayoutButtonHistoryList = findViewById<LinearLayout>(R.id.linearLayoutButton)
        lateinit var tracksAdapter: TrackListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        watchHistoryList.layoutManager =
            LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        searchHistory.sharedPreferencesCreated(getSharedPreferences("SEARCH_HISTORY", MODE_PRIVATE))
        historyTrackLists.addAll(searchHistory.getHistory())


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
                                tracksAdapter = TrackListAdapter(trackList.results, this@SearchActivity)
                                recyclerView.adapter = tracksAdapter
                                recyclerView.visibility = View.VISIBLE

//------------------
//                                trackForLibraryActivity = recyclerView.setRecyclerListener{
////                                  //  recyclerView.setOnClickListener {  }
////
////
////                                    var h = tracksAdapter.listener.OnClick()
////            startActivity(Intent(this@SearchActivity, LibraryActivity::class.java))
//                                    LibraryActivity.ACTIVITY = this@SearchActivity
//                                    startActivity(Intent(this@SearchActivity,LibraryActivity::class.java))
//                                    finish()
////            songCoverImage.setImageURI()
//                                } as Result


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

        buttonClearHistoryList.setOnClickListener {  //-----------------------------------
            searchHistory.clearHistory()
            historyTrackLists.clear()
            historyListAdapters.updateTracks(historyTrackLists)
            trackHistoryView.visibility = View.GONE
            linearLayoutButtonHistoryList.visibility = View.GONE
        }


        backToMainSearch.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

//        watchHistoryList.setRecyclerListener {
//            startActivity(Intent(this, CallbackActivityUse.backToActivity(LibraryActivity(), LibraryActivity())!!::class.java))
//            finish()
//        }



//



        textInputLayoutSearch.setStartIconOnClickListener {
            val text = "результат поиска по заданному значению: " + textFromTextField
            Toast.makeText(this@SearchActivity, text, Toast.LENGTH_SHORT).show()
            editText.setText("")
            trackHistoryView.visibility = View.GONE
            linearLayoutButtonHistoryList.visibility = View.GONE

            val address: String
            address = textFromTextField ?: "try again"
            Toast.makeText(this@SearchActivity, address, Toast.LENGTH_SHORT).show()
            createAPIresponse(address)
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
                trackHistoryView.visibility =
                    if (editText.hasFocus() && s?.isEmpty() == true && historyTrackLists.size > 0) View.VISIBLE else View.GONE
                linearLayoutButtonHistoryList.visibility =
                    if (editText.hasFocus() && s?.isEmpty() == true && historyTrackLists.size > 0) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty().not()) {
                    textFromTextField = s.toString()
                    trackHistoryView.visibility = View.GONE
                    linearLayoutButtonHistoryList.visibility = View.GONE
                    stringWatcherTextEdit = s.toString()
                } else if (s.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    noSongView.visibility = View.GONE
                    noInternetView.visibility = View.GONE
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
        }
        editText.addTextChangedListener(textInputLayout)

        editText.setOnFocusChangeListener { view, hasFocus ->
            trackHistoryView.visibility =
                if (hasFocus && editText.text.isEmpty() && historyTrackLists.size > 0) View.VISIBLE else View.GONE
            linearLayoutButtonHistoryList.visibility =
                if (hasFocus && editText.text.isEmpty() && historyTrackLists.size > 0) View.VISIBLE else View.GONE
        }

        historyListAdapters = TrackListAdapter(historyTrackLists, this@SearchActivity)
        watchHistoryList.adapter = historyListAdapters
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


//    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")  ............

    override fun OnClick(track: Result) {

        LibraryActivity.ACTIVITY = this@SearchActivity
        startActivity(Intent(this@SearchActivity,LibraryActivity::class.java))
        finish()

        tackForLibraryActivityHL = track.trackId

       // val clickTrack = track
        sharedPreferences = getSharedPreferences("last track", MODE_PRIVATE)
        var json = Gson().toJson(track)
        sharedPreferences.edit().putString("last_track_history", json).apply()
//        json = sharedPreferences.getString("last_track_history", null)// ?: return emptyList()
        trackForLibraryActivity = Gson().fromJson(sharedPreferences.getString("last_track_history", null), Result::class.java)

        searchHistory.add(track)
//        recyclerView.setRecyclerListener{
////            startActivity(Intent(this@SearchActivity, LibraryActivity::class.java))
//            LibraryActivity.ACTIVITY = this
//            startActivity(Intent(this, LibraryActivity::class.java))
//            finish()
////            songCoverImage.setImageURI()
//        }



        historyTrackLists.clear()
        historyTrackLists.addAll(searchHistory.getHistory())
        historyListAdapters.updateTracks(historyTrackLists)  //-------------

        watchHistoryList.setRecyclerListener {
            LibraryActivity.ACTIVITY = this
            //CallbackActivityUse.backToActivity(SearchActivity(), LibraryActivity())
            startActivity(Intent(this, LibraryActivity::class.java))
//            startActivity(Intent(this@SearchActivity, LibraryActivity::class.java))
            finish()
        }



        Log.w("onclick", "${Gson().toJson(historyTrackLists)}")
        Toast.makeText(this@SearchActivity, "${track.trackId}", Toast.LENGTH_SHORT).show()
        Toast.makeText(this@SearchActivity, "${historyTrackLists.size}", Toast.LENGTH_SHORT).show()
    }


}


