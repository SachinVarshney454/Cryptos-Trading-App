package com.example.govt01.chart

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
interface Chartretro {
    @GET("coins/{coin}/market_chart")
    suspend fun getCoinChartData(
        @Path("coin") coin: String, // coin id like "bitcoin", "ethereum", etc.
        @Query("vs_currency") currency: String = "inr",
        @Query("days") days: Int = 1
    ): CHARTDATA
}