// In app/src/test/java/com/example/stocktracker/domain/repository/FakePriceRepository.kt

package com.multibank.stocktracker.domain.repository

import com.multibank.stocktracker.data.Stock
import kotlinx.coroutines.flow.MutableSharedFlow

class FakePriceRepository : PriceRepository {

    // VVV ADD replay = 1 HERE VVV
    private val stockUpdatesFlow = MutableSharedFlow<Stock>(replay = 1)
    var startFeedCalled = false
    var stopFeedCalled = false

    // This function allows our test to manually emit a stock update
    suspend fun emit(stock: Stock) {
        stockUpdatesFlow.emit(stock)
    }

    override fun getStockUpdates() = stockUpdatesFlow

    override fun startPriceFeed(symbols: List<String>) {
        startFeedCalled = true
    }

    override fun stopPriceFeed() {
        stopFeedCalled = true
    }

    override fun disconnect() {
        // Not needed for this test
    }
}