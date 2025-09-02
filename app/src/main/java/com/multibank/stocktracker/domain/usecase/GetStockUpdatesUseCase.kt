// In domain/usecase/GetStockUpdatesUseCase.kt

package com.multibank.stocktracker.domain.usecase

import com.multibank.stocktracker.data.PriceChange
import com.multibank.stocktracker.data.Stock
import com.multibank.stocktracker.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random // <-- Make sure this import is present

class GetStockUpdatesUseCase(
    private val priceRepository: PriceRepository
) {
    // The current in-memory list of stocks
    private val stocks = mutableMapOf<String, Stock>()

    operator fun invoke(symbols: List<String>): Flow<List<Stock>> {
        return priceRepository.getStockUpdates()
            .map { updatedStock ->
                val currentStock = stocks[updatedStock.symbol]

                val change = when {
                    currentStock == null -> PriceChange.UNCHANGED
                    updatedStock.price > currentStock.price -> PriceChange.INCREASE
                    updatedStock.price < currentStock.price -> PriceChange.DECREASE
                    else -> PriceChange.UNCHANGED
                }

                val newStock = updatedStock.copy(change = change)
                stocks[newStock.symbol] = newStock

                // Return the updated list, sorted by price
                stocks.values.sortedByDescending { it.price }
            }
            .onStart {
                // Initialize the list when the flow starts
                if (stocks.isEmpty()) {
                    symbols.forEach { symbol ->
                        // VVV THIS IS THE CHANGE VVV
                        // Generate a random initial price between 80 and 200
                        val initialPrice = Random.nextDouble(80.0, 200.0)
                        stocks[symbol] = Stock(symbol = symbol, price = initialPrice)
                    }
                }
                // Emit the initial, sorted list
                emit(stocks.values.sortedByDescending { it.price })
            }
    }

    fun startFeed(symbols: List<String>) {
        priceRepository.startPriceFeed(symbols)
    }

    fun stopFeed() {
        priceRepository.stopPriceFeed()
    }
}