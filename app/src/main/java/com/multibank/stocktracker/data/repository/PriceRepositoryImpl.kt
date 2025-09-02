// In data/repository/PriceRepositoryImpl.kt

package com.multibank.stocktracker.data.repository

import android.util.Log
import com.multibank.stocktracker.data.PriceChange
import com.multibank.stocktracker.data.PriceTrackerService
import com.multibank.stocktracker.data.Stock
import com.multibank.stocktracker.domain.repository.PriceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random

class PriceRepositoryImpl(
    private val priceTrackerService: PriceTrackerService
) : PriceRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)
    private var priceUpdateJob: Job? = null

    // This flow will receive parsed updates from the service and emit them to the use case.
    private val _stockUpdates = MutableSharedFlow<Stock>()

    init {
        // Connect to the WebSocket service immediately
        priceTrackerService.connect("wss://ws.postman-echo.com/raw")

        // Start listening to incoming messages and parse them
        priceTrackerService.messages
            .onEach { message ->
                val json = JSONObject(message)
                val symbol = json.getString("symbol")
                val newPrice = json.getDouble("price")
                // For this implementation, we can't determine price change here,
                // so we'll pass it up to the use case.
                _stockUpdates.emit(Stock(symbol, newPrice, PriceChange.UNCHANGED))
            }
            .launchIn(repositoryScope)
    }

    override fun getStockUpdates(): Flow<Stock> = _stockUpdates

    override fun startPriceFeed(symbols: List<String>) {
        stopPriceFeed() // Ensure only one job is running
        priceUpdateJob = repositoryScope.launch {
            while (isActive) {
                symbols.forEach { symbol ->
                    // Generate a random price. The actual price logic will be in the use case.
                    val price = 100.0 + Random.nextDouble(-10.0, 10.0)
                    val message = """{"symbol": "$symbol", "price": ${"%.2f".format(price)}}"""
                    Log.d("StockTrackerDebug", "--> Sending: $message")
                    priceTrackerService.sendMessage(message)
                }
                delay(2000)
            }
        }
    }

    override fun stopPriceFeed() {
        priceUpdateJob?.cancel()
    }

    override fun disconnect() {
        priceTrackerService.disconnect()
    }
}