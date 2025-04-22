package com.practicum.playlistmaker


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
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
        const val AMOUNT_KEY = "AMOUNT_KEY"
        const val DEF_VALUE = ""
        var textFromTextField = DEF_VALUE
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    var trackForLibraryActivity: Result? = null
    var tackForLibraryActivityHL: Int? = null
    lateinit var sharedPreferences: SharedPreferences
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        if(binding.inputEditText.text.isNotEmpty()){
            address = binding.inputEditText.text?.toString() ?: "tryagain"
            createAPIresponse(address)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }else{
            binding.progressBar.visibility = View.GONE
        }
        if(binding.refreshButton.isClickable){
            binding.refreshButton.setOnClickListener {
                binding.trackListRecyclerView.visibility = View.GONE
                binding.noSong.visibility = View.GONE
                binding.noInternet.visibility = View.GONE

                createAPIresponse(textFromTextField)
                Toast.makeText(
                    this@SearchActivity,
                    "${textFromTextField} - не найдено. Проверьте доступ к интернету.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    lateinit var tracksAdapter: TrackListAdapter
    lateinit var  address: String
    var stringWatcherTextEdit: String = DEF_VALUE
    val historyTrackLists: MutableList<Result> = mutableListOf()
    val searchHistory = SearchHistory()
    val baseApiUrl = "https://itunes.apple.com"
    lateinit var iTunesAPI: ITunesSearchAPI
    var historyListAdapters: TrackListAdapter =
        TrackListAdapter(historyTrackLists, this@SearchActivity)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.trackListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.watchHistoryList.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        searchHistory.sharedPreferencesCreated(getSharedPreferences("SEARCH_HISTORY", MODE_PRIVATE))
        historyTrackLists.addAll(searchHistory.getHistory())

        binding.buttonClearHistoryList.setOnClickListener {
            searchHistory.clearHistory()
            historyTrackLists.clear()
            historyListAdapters.updateTracks(historyTrackLists)
            binding.trackHistoryView.visibility = View.GONE
                binding.linearLayoutButton.visibility = View.GONE
        }


        binding.backToMainArrow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.textInputLayoutSearch.setStartIconOnClickListener {
            val text = "результат поиска по заданному значению: " + textFromTextField
            Toast.makeText(this@SearchActivity, text, Toast.LENGTH_SHORT).show()
            binding.inputEditText.setText("")
            binding.trackHistoryView.visibility = View.GONE
            binding.linearLayoutButton.visibility = View.GONE

            address = textFromTextField ?: "try again"
            Toast.makeText(this@SearchActivity, address, Toast.LENGTH_SHORT).show()
            createAPIresponse(address)
            if (binding.refreshButton.isClickable) {
                binding.refreshButton.setOnClickListener {
                    binding.trackListRecyclerView.visibility = View.GONE
                    binding.noSong.visibility = View.GONE
                    binding.noInternet.visibility = View.GONE

                    createAPIresponse(textFromTextField)
                    Toast.makeText(
                        this@SearchActivity,
                        "${textFromTextField} - не найдено. Проверьте доступ к интернету.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.textInputLayoutSearch.setEndIconOnClickListener {
            binding.inputEditText.setText("")
            binding.progressBar.visibility = View.GONE
            binding.trackListRecyclerView.visibility = View.GONE
            binding.noSong.visibility = View.GONE
            binding.noInternet.visibility = View.GONE
        }

        val textInputLayout = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){
                if(binding.inputEditText.text.toString() == "" && binding.inputEditText.text.toString().isEmpty()){ //|| binding.trackHistoryView.isVisible) {
                    binding.progressBar.visibility = View.GONE //---------------------------------------------------------------------
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.inputEditText.text.toString() != ""){
                    binding.progressBar.visibility = View.VISIBLE
                }
                searchDebounce()
                binding.textInputLayoutSearch.isEndIconVisible = clearButtonVisibility(s)
                binding.trackHistoryView.visibility =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && historyTrackLists.size > 0) View.VISIBLE else View.GONE
                binding.linearLayoutButton.visibility =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && historyTrackLists.size > 0) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty().not()) {
                    textFromTextField = s.toString()
                    binding.trackHistoryView.visibility = View.GONE
                    binding.linearLayoutButton.visibility = View.GONE
                    stringWatcherTextEdit = s.toString()
                } else if (s.isNullOrEmpty()) {
                    binding.trackListRecyclerView.visibility = View.GONE
                    binding.noSong.visibility = View.GONE
                    binding.noInternet.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
                if(binding.trackListRecyclerView.isVisible){ //--------------------------------------------------------------------------------
                    binding.trackHistoryView.visibility = View.GONE
                }

                if (binding.textInputLayoutSearch.isEndIconVisible.not()) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
                }

            }
        }
        binding.inputEditText.addTextChangedListener(textInputLayout)

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            binding.trackHistoryView.visibility =
                if (hasFocus && binding.inputEditText.text.isEmpty() && historyTrackLists.size > 0) View.VISIBLE else View.GONE
            binding.linearLayoutButton.visibility =
                if (hasFocus && binding.inputEditText.text.isEmpty() && historyTrackLists.size > 0) View.VISIBLE else View.GONE
        }

        historyListAdapters = TrackListAdapter(historyTrackLists, this@SearchActivity)
        if(clickDebounce()) {   //-----------------------------------------------------------------------------------------------
            binding.watchHistoryList.adapter = historyListAdapters
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
                    binding.progressBar.visibility = View.GONE
                    val trackList = response.body()
                    if (response.isSuccessful && trackList?.resultCount != 0) {
                        binding.noSong.visibility = View.GONE
                        binding.noInternet.visibility = View.GONE

                        Log.w("RESPONSE", "${response.body()}")
                        trackList?.let {

                                tracksAdapter =
                                    TrackListAdapter(trackList.results, this@SearchActivity)
                            if(clickDebounce()) {   //---------------------------------------------------------------------
                                binding.trackListRecyclerView.adapter = tracksAdapter
                                binding.trackListRecyclerView.visibility = View.VISIBLE
                            }
                        }
                    } else {

                        if (response.code() != 200) {
                            binding.trackListRecyclerView.visibility = View.GONE
                            binding.noSong.visibility = View.GONE
                            binding.noInternet.visibility = View.VISIBLE

                        }
                        if (trackList?.resultCount == 0) {
                            binding.trackListRecyclerView.visibility = View.GONE
                            binding.noSong.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<ITunesDTO>, t: Throwable) {
                    if (isConnected(this@SearchActivity).not()) {
                        binding.trackListRecyclerView.visibility = View.GONE
                        binding.noSong.visibility = View.GONE
                        binding.noInternet.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun searchDebounce() {   //------------------------------------------------searchDebounce
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    override fun OnClick(track: Result) {

        LibraryActivity.ACTIVITY = this@SearchActivity
        startActivity(Intent(this@SearchActivity, LibraryActivity::class.java))
        finish()


        tackForLibraryActivityHL = track.trackId
        sharedPreferences = getSharedPreferences("last track", MODE_PRIVATE)
        var json = Gson().toJson(track)
        sharedPreferences.edit().putString("last_track_history", json).apply()
        trackForLibraryActivity = Gson().fromJson(
            sharedPreferences.getString("last_track_history", null),
            Result::class.java
        )

        searchHistory.add(track)
        historyTrackLists.clear()
        historyTrackLists.addAll(searchHistory.getHistory())
        historyListAdapters.updateTracks(historyTrackLists)

        binding.watchHistoryList.setRecyclerListener {
            LibraryActivity.ACTIVITY = this
            startActivity(Intent(this, LibraryActivity::class.java))
            finish()
        }

        Log.w("onclick", "${Gson().toJson(historyTrackLists)}")
        Toast.makeText(this@SearchActivity, "${track.trackId}", Toast.LENGTH_SHORT).show()
        Toast.makeText(this@SearchActivity, "${historyTrackLists.size}", Toast.LENGTH_SHORT).show()
    }


}


