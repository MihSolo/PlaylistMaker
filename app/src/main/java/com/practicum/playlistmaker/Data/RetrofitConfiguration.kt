package com.practicum.playlistmaker.Data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


//рэтрофит
class RetrofitConfiguration {
    val baseApiUrl = "https://itunes.apple.com"

    fun configureRetrofit(): ITunesSearchAPI {             //to ITunesNetworkClient
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

    fun loadResults(iTunesSearchAPI: ITunesSearchAPI, callString:String, callback: Callback<ITunesDTO>){
        iTunesSearchAPI.search(callString).enqueue(callback)
    }
}