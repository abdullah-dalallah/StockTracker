package com.multibank.stocktracker.data

data class Stock(
    val symbol: String,
    val price: Double = 0.0,
    val change: PriceChange = PriceChange.UNCHANGED
)

enum class PriceChange {
    INCREASE, DECREASE, UNCHANGED
}