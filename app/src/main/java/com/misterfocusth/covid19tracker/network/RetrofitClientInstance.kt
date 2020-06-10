package com.misterfocusth.covid19tracker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientInstance {

    companion object {
        private lateinit var retrofit: Retrofit
        private val BASE_URL: String = "https://covid19.th-stat.com"
    }

    fun getRetrofitInstance(): Retrofit {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        return retrofit
    }

}