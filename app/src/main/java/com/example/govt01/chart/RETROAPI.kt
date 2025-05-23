package com.example.govt01.chart

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RETROAPI {
    val api: Chartretro by lazy {
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                            .build()
                        chain.proceed(newRequest)
                    }
                    .build()
            )
            .baseUrl("https://api.coingecko.com/api/v3/")

            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Chartretro::class.java)
    }
}