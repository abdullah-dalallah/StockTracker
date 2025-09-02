// viewmodel/PriceTrackerUiState.kt

package com.multibank.stocktracker.viewmodel

import com.multibank.stocktracker.data.Stock

data class PriceTrackerUiState(
    val stocks: List<Stock> = emptyList(),
    val isConnected: Boolean = false,
    val isFeedActive: Boolean = false
)