// In domain/repository/PriceRepository.kt

package com.multibank.stocktracker.domain.repository

import com.multibank.stocktracker.data.Stock
import kotlinx.coroutines.flow.Flow

interface PriceRepository {
    /**
     * Connects to the data source and provides a real-time flow of stock updates.
     */
    fun getStockUpdates(): Flow<Stock>

    /**
     * Starts the process of sending mock price updates for a given list of symbols.
     */
    fun startPriceFeed(symbols: List<String>)

    /**
     * Stops the price feed.
     */
    fun stopPriceFeed()

    /**
     * Disconnects from the data source.
     */
    fun disconnect()
}