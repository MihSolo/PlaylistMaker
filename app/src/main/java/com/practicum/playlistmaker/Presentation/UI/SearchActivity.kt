package com.practicum.playlistmaker.Presentation.UI


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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.Data.ITunesSearchAPI
import com.practicum.playlistmaker.Data.RetrofitConfiguration
import com.practicum.playlistmaker.Data.SearchHistory
import com.practicum.playlistmaker.Domain.LoadTracksListAPI
import com.practicum.playlistmaker.Domain.Track
import com.practicum.playlistmaker.Data.ITunesDTO
import com.practicum.playlistmaker.Presentation.TrackListAdapter
import com.practicum.playlistmaker.Presentation.UIUpdate
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

    internal val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private lateinit var loadTracksListAPI: LoadTracksListAPI  //   ispolzuem UseCases class
    lateinit var tracksAdapter: TrackListAdapter


   // private val useCase = Creator  UseCase -> execute  ......у Interactor -> play() pause() для PlayerInteractor - пример
 //  UseCase Iterator надл реализовать через интерфейс, через абстракцию.....

  //  private lateinit var networkService: NetworkService //++++++++++++++++++++++++++++++++++++=

//    private val trackListRecyclerView = findViewById<RecyclerView>(R.id.trackListRecyclerView)
//    private val noSong = findViewById<LinearLayout>(R.id.no_song)
//    private val noInternet = findViewById<LinearLayout>(R.id.no_internet)
//    private val progressBar = findViewById<ProgressBar>(R.id.progressBar)
//

//    private val uiUpdate: UIUpdate = UIUpdate(binding.trackListRecyclerView, binding.noSong, binding.noInternet, binding.progressBar)
private val uiUpdate: UIUpdate = UIUpdate()
    private val retrofitConfiguration = RetrofitConfiguration()//++++++++++++++++++++++++++++++++++++++++++++++++++=

    var trackForLibraryActivity: Track? = null
    var tackForLibraryActivityHL: Int? = null
    lateinit var sharedPreferences: SharedPreferences
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        if(binding.inputEditText.text.isNotEmpty()){
            address = binding.inputEditText.text?.toString() ?: "tryagain"



            //createAPIresponse(address)
           // if(networkService.createApi(address, responseResult) != null) {
                //response
//                    networkService = NetworkService()
            loadTracksListAPI = LoadTracksListAPI(retrofitConfiguration)//, uiUpdate, this, isConnected(this) )
                loadTracksListAPI.execute(address,{
                this.tracksAdapter = TrackListAdapter(it.results, this)
//                uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )
//                uiUpdate.uiRefreshOnFailureMethod(this, {isConnected(this)})
            }, {
//                    uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this), it)
                    uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )
//                    uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this))
                },{
                    uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this))//, it)
                })


//                    networkService.createApi(address, uiUpdate, this,
//                        { clickDebounce() }, retrofitConfiguration,  {isConnected(this)})
             //   } //+++++++++++++++++++++++++++
           // responseResult(response) //++++++++++++++++++++++++++++++++++++++


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

               // createAPIresponse(textFromTextField)
             //   if(networkService.createApi(textFromTextField) != null) {
               //     response = networkService.createApi(textFromTextField)!!



//                networkService.createApi(textFromTextField, uiUpdate, this, {clickDebounce()}, retrofitConfiguration, {isConnected(this)})

                loadTracksListAPI = LoadTracksListAPI(retrofitConfiguration)//, uiUpdate, this, isConnected(this))
                loadTracksListAPI.execute(textFromTextField,{
                    this.tracksAdapter = TrackListAdapter(it.results, this)
//                    uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )
//                    uiUpdate.uiRefreshOnFailureMethod(this, {isConnected(this)})
                }, {
//                    uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this), it)
                    uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )

                }, {
                    uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this))//, it)
                })
                //} //+++++++++++++++++++++++++++
                //responseResult(response)//++++++++++++++++++++++++++++++


                Toast.makeText(
                    this@SearchActivity,
                    "$textFromTextField - не найдено. Проверьте доступ к интернету.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



//    internal val binding: ActivitySearchBinding by lazy {
//        ActivitySearchBinding.inflate(layoutInflater)
//    }

//    lateinit var tracksAdapter: TrackListAdapter
    lateinit var  address: String
    var stringWatcherTextEdit: String = DEF_VALUE
    val historyTrackLists: MutableList<Track> = mutableListOf()
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
        searchHistory.sharedPreferencesCreated(getSharedPreferences("SEARCH_HISTORY", MODE_PRIVATE)) // создаём файл для локального хранения
        historyTrackLists.addAll(searchHistory.getHistory())  // сохраняем пустой список....

     //   networkService = NetworkService()  //+++++++++++++++++++++++++++++++++++
       // uiUpdate = UIUpdate(binding.trackListRecyclerView, binding.noSong,binding.noInternet, binding.progressBar) //++++++++

        binding.buttonClearHistoryList.setOnClickListener {
            searchHistory.clearHistory()            // очишаем созданный список истории...
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

//            createAPIresponse(address)
           // if(networkService.createApi(address) != null) {
             //   response = networkService.createApi(address)!!
            //} //+++++++++++++++++++++++++++


           // networkService.createApi(address, uiUpdate, this, {clickDebounce()}, retrofitConfiguration,  {isConnected(this)} )

            loadTracksListAPI = LoadTracksListAPI(retrofitConfiguration)//, uiUpdate, this, isConnected(this))
            loadTracksListAPI.execute(address,{
                this.tracksAdapter = TrackListAdapter(it.results, this)
//                uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )
//                uiUpdate.uiRefreshOnFailureMethod(this, {isConnected(this)})
            },{
//                uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this), it)
                uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )

            },{
                uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this))// it)
            })
            //responseResult(response)//+++++++++++++++++++++++++++++++++++
            if (binding.refreshButton.isClickable) {
                binding.refreshButton.setOnClickListener {
                    binding.trackListRecyclerView.visibility = View.GONE
                    binding.noSong.visibility = View.GONE
                    binding.noInternet.visibility = View.GONE

//                                               //+++++++++++++++++++++++++++
                   // networkService.createApi(textFromTextField, uiUpdate, this, {clickDebounce()}, retrofitConfiguration,  {isConnected(this)} )

                    loadTracksListAPI = LoadTracksListAPI(retrofitConfiguration)//, uiUpdate, this, isConnected(this))
                    loadTracksListAPI.execute(textFromTextField,{
                        this.tracksAdapter = TrackListAdapter(it.results, this)
//                        uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )
//                        uiUpdate.uiRefreshOnFailureMethod(this, {isConnected(this)})
                    }, {
//                        uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this), it)
                        uiUpdate.uiRefreshOnResponseMethod(it, this, {clickDebounce()} )

                    }, {
                        uiUpdate.uiRefreshOnFailureMethod(this, isConnected(this))//, it)
                    })
                    //responseResult(response)//++++++++++++++++++++++++++++++==
                    Toast.makeText(
                        this@SearchActivity,
                        "$textFromTextField - не найдено. Проверьте доступ к интернету.",
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
                // тут надо прописать, что если былл ввод
                // и потом один из символов был стёрт
                // progresBar не появляется
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




    } //OnCreayte(.........)


    fun createAPIrespons(valueForRequest: String) {
        iTunesAPI = configureRetrofit()
        iTunesAPI.search(valueForRequest)
            .enqueue(object : Callback<ITunesDTO> {

                override fun onResponse(
                    call: Call<ITunesDTO>,
                    response: Response<ITunesDTO>
                ) {
                    binding.progressBar.visibility = View.GONE    //?????????????????????????????????

                   // val trackList = response.body()

                   // if (response.isSuccessful && trackList?.resultCount != 0) {
                    if(response.isSuccessful){
                        binding.noSong.visibility = View.GONE
                        binding.noInternet.visibility = View.GONE

                        Log.w("RESPONSE", "${response.body()}")
//                        trackList?.let {
//
//                                tracksAdapter =
//                                    TrackListAdapter(trackList.results, this@SearchActivity)
//                            if(clickDebounce()) {   //---------------------------------------------------------------------
//                                binding.trackListRecyclerView.adapter = tracksAdapter
//                                binding.trackListRecyclerView.visibility = View.VISIBLE
//                            }
//                        }
                        response.body()?.let {
                            tracksAdapter = TrackListAdapter(it.results, this@SearchActivity) //?:  можно добавить возврат error
                            if(clickDebounce()){
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
                       // if (trackList?.resultCount == 0) {
                        if(response.body()?.resultCount == 0) {
                            binding.trackListRecyclerView.visibility = View.GONE
                            binding.noSong.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<ITunesDTO>, t: Throwable) {
//                    if (isConnected(this@SearchActivity).not()) {
//                        binding.trackListRecyclerView.visibility = View.GONE
//                        binding.noSong.visibility = View.GONE
//                        binding.noInternet.visibility = View.VISIBLE
//                    }
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
//        Toast.makeText(context, "in isConnected", Toast.LENGTH_SHORT).show()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
//                    Toast.makeText(context, "TRANSPORT_CELLULAR", Toast.LENGTH_SHORT).show()
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
//                    Toast.makeText(context, "TRANSPORT_WIFI", Toast.LENGTH_SHORT).show()
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
//                    Toast.makeText(context, "TRANSPORT_ETHERNET", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
        return false
    }

    override fun OnClick(track: Track) {

        LibraryActivity.ACTIVITY = this@SearchActivity
        startActivity(Intent(this@SearchActivity, LibraryActivity::class.java))
        finish()


        tackForLibraryActivityHL = track.trackId
        sharedPreferences = getSharedPreferences("last track", MODE_PRIVATE)
        var json = Gson().toJson(track)
        sharedPreferences.edit().putString("last_track_history", json).apply()
        trackForLibraryActivity = Gson().fromJson(
            sharedPreferences.getString("last_track_history", null),
            Track::class.java
        )

        searchHistory.add(track)   //  добавляем трек в список истории
        historyTrackLists.clear()
        historyTrackLists.addAll(searchHistory.getHistory())    // добавляет список песен
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


