package com.example.govt01

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.govt01.PRICERETRO.Ticket


object coinsingelton {
    var coins by mutableStateOf<List<Ticket>>(emptyList())
}