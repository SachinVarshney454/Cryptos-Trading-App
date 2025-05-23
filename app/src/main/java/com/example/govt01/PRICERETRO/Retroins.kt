package com.example.govt01.PRICERETRO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retroins {
    val api: Inter by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coinpaprika.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Inter::class.java)
    }
}