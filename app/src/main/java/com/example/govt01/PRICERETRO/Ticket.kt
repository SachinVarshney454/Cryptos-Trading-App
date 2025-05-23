package com.example.govt01.PRICERETRO

data class Ticket(  val id: String,
                    val name: String,
                    val symbol: String,
                    val rank: Int,
                    val quotes: Map<String, Quote>
)

data class Quote(
    val price: Double,
    val volume_24h: Double,
    val market_cap: Double,
    val percent_change_1h: Double,
    val percent_change_24h: Double,
    val percent_change_7d: Double
)