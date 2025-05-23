package com.example.govt01.PRICERETRO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Call

interface Inter {
    // Get metadata for all coins (no prices)
    @GET("coins")
    fun getAllCoins(): Call<List<Coins>>

    // Get metadata for a specific coin
    @GET("coins/{id}")
    fun getCoinById(@Path("id") coinId: String): Coins

    // ✅ Best: Get all tickers (with live price, percent change, etc.)
    @GET("tickers")
    suspend fun getAllTickers(): List<Ticket>

    // Get ticker (price info) for a specific coin
    @GET("tickers/{id}")
    fun getTickerById(@Path("id") coinId: String): Call<Ticket>
}