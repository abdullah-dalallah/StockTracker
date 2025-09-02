// In app/src/test/java/com/example/stocktracker/domain/usecase/GetStockUpdatesUseCaseTest.kt

package com.multibank.stocktracker.domain.usecase

import com.multibank.stocktracker.data.PriceChange
import com.multibank.stocktracker.data.Stock
import com.multibank.stocktracker.domain.repository.FakePriceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// VVV ADD THIS ANNOTATION TO FIX THE WARNINGS VVV
@OptIn(ExperimentalCoroutinesApi::class)
class GetStockUpdatesUseCaseTest {

    private lateinit var getStockUpdatesUseCase: GetStockUpdatesUseCase
    private lateinit var fakePriceRepository: FakePriceRepository
    private val testSymbols = listOf("AAPL", "GOOGL")

    @Before
    fun setUp() {
        fakePriceRepository = FakePriceRepository()
        getStockUpdatesUseCase = GetStockUpdatesUseCase(fakePriceRepository)
    }

    @Test
    fun `use case emits initial list with random prices`() = runTest {
        // When the use case is invoked for the first time
        val results = getStockUpdatesUseCase(testSymbols).take(1).toList()
        val initialList = results.first()

        // Then the list should not be empty and prices should not be zero
        assertEquals(2, initialList.size)
        assertTrue(initialList.all { it.price > 0.0 })
    }

    @Test
    fun `use case emits updated and sorted list`() = runTest {
        val results = mutableListOf<List<Stock>>()
        val job = launch {
            getStockUpdatesUseCase(testSymbols).take(2).toList(results)
        }

        advanceUntilIdle() // Let the collector process the initial list

        // When a new stock update is emitted that should be at the top
        fakePriceRepository.emit(Stock("AAPL", 200.0))

        // VVV THIS IS THE FIX VVV
        // Wait for the job to complete (after collecting 2 items) before asserting
        job.join()

        // Then the latest list should be sorted by price descending
        val updatedList = results.last()
        assertEquals("AAPL", updatedList.first().symbol)
        assertEquals(200.0, updatedList.first().price, 0.0)
    }

    @Test
    fun `use case correctly calculates price change`() = runTest {
        val results = mutableListOf<List<Stock>>()
        val job = launch {
            getStockUpdatesUseCase(testSymbols).take(3).toList(results)
        }

        advanceUntilIdle() // Let the collector process the initial list

        // When a price increase and then a decrease are emitted
        fakePriceRepository.emit(Stock("AAPL", 250.0))
        fakePriceRepository.emit(Stock("AAPL", 150.0))

        // VVV THIS IS THE FIX VVV
        // Wait for the job to complete (after collecting 3 items)
        job.join()

        // Then the price changes should be calculated correctly in the results
        val listAfterIncrease = results[1]
        assertEquals(PriceChange.INCREASE, listAfterIncrease.find { it.symbol == "AAPL" }?.change)

        val listAfterDecrease = results[2]
        assertEquals(PriceChange.DECREASE, listAfterDecrease.find { it.symbol == "AAPL" }?.change)
    }
}