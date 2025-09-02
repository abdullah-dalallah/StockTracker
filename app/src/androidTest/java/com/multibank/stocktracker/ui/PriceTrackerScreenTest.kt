// In app/src/androidTest/java/com/example/stocktracker/ui/PriceTrackerScreenTest.kt

package com.multibank.stocktracker.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.multibank.stocktracker.data.PriceChange
import com.multibank.stocktracker.data.Stock
import com.multibank.stocktracker.ui.theme.StockTrackerTheme
import org.junit.Rule
import org.junit.Test

class PriceTrackerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockRow_displaysCorrectData_forPriceIncrease() {
        // Given a stock with a price increase
        val stock = Stock("AAPL", 150.55, PriceChange.INCREASE)

        // When the StockRow is composed
        composeTestRule.setContent {
            StockTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then the correct data and icon are displayed
        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithText("$150.55").assertIsDisplayed()

        // THIS LINE IS NOW CORRECTED
        composeTestRule.onNodeWithContentDescription("Price Change").assertIsDisplayed()
    }

    @Test
    fun stockRow_displaysCorrectData_forPriceDecrease() {
        // Given a stock with a price decrease
        val stock = Stock("GOOGL", 120.22, PriceChange.DECREASE)

        // When the StockRow is composed
        composeTestRule.setContent {
            StockTrackerTheme {
                StockRow(stock = stock)
            }
        }

        // Then the correct data is displayed
        composeTestRule.onNodeWithText("GOOGL").assertIsDisplayed()
        composeTestRule.onNodeWithText("$120.22").assertIsDisplayed()

        // THIS LINE IS ALSO CORRECTED
        composeTestRule.onNodeWithContentDescription("Price Change").assertIsDisplayed()
    }
}