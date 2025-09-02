package com.multibank.stocktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.stocktracker.data.PriceTrackerService
import com.multibank.stocktracker.data.repository.PriceRepositoryImpl
import com.multibank.stocktracker.domain.repository.PriceRepository
import com.multibank.stocktracker.domain.usecase.GetStockUpdatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PriceTrackerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PriceTrackerUiState())
    val uiState: StateFlow<PriceTrackerUiState> = _uiState.asStateFlow()

    // --- Dependencies ---
    // In a real app, this would be handled by a dependency injection framework like Hilt.
    private val priceRepository: PriceRepository = PriceRepositoryImpl(PriceTrackerService())
    private val getStockUpdatesUseCase = GetStockUpdatesUseCase(priceRepository)

    private val symbols = listOf(
        "AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "NVDA", "META", "JPM", "V", "JNJ",
        "WMT", "PG", "UNH", "HD", "MA", "BAC", "DIS", "PYPL", "ADBE", "NFLX",
        "CRM", "CSCO", "PFE", "XOM", "T"
    )

    init {
        // The ViewModel is now much simpler. It just collects the result from the use case.
        viewModelScope.launch {
            getStockUpdatesUseCase(symbols).collect { updatedStocks ->
                _uiState.update {
                    it.copy(
                        stocks = updatedStocks,
                        isConnected = true // Can be enhanced to get from repository
                    )
                }
            }
        }
    }

    fun togglePriceFeed() {
        val isActive = !_uiState.value.isFeedActive
        if (isActive) {
            getStockUpdatesUseCase.startFeed(symbols)
        } else {
            getStockUpdatesUseCase.stopFeed()
        }
        _uiState.update { it.copy(isFeedActive = isActive) }
    }

    override fun onCleared() {
        super.onCleared()
        priceRepository.disconnect()
    }
}