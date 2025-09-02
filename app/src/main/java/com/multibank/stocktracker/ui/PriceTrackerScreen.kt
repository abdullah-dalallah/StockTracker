// In ui/PriceTrackerScreen.kt

package com.multibank.stocktracker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multibank.stocktracker.data.PriceChange
import com.multibank.stocktracker.data.Stock
import com.multibank.stocktracker.ui.theme.PriceGreen
import com.multibank.stocktracker.ui.theme.PriceRed
import com.multibank.stocktracker.ui.theme.customColors // <-- Import your custom colors accessor
import com.multibank.stocktracker.viewmodel.PriceTrackerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceTrackerScreen(viewModel: PriceTrackerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Tracker") },
                // Use theme colors for the TopAppBar
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = {
                    Button(
                        onClick = { viewModel.togglePriceFeed() },
                        // Use theme colors for the Button
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (uiState.isFeedActive) "Stop" else "Start")
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Connection Status",
                        // Use a static color here as green/red status is universal
                        tint = if (uiState.isConnected) PriceGreen else PriceRed,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background) // Use theme background
        ) {
            items(uiState.stocks, key = { it.symbol }) { stock ->
                StockRow(stock = stock)
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
        }
    }
}

// In ui/PriceTrackerScreen.kt

// In ui/PriceTrackerScreen.kt

@Composable
fun StockRow(stock: Stock) {
    // 1. Get the main background color from the theme.
    val backgroundColor = MaterialTheme.colorScheme.background
    val priceIncreaseColor = MaterialTheme.customColors.priceIncrease
    val priceDecreaseColor = MaterialTheme.customColors.priceDecrease

    // 2. Set the initial state of the row's color to the theme's background.
    var rowColor by remember { mutableStateOf(backgroundColor) }

    val animatedColor by animateColorAsState(
        targetValue = rowColor,
        label = "rowColorAnimation"
    )

    LaunchedEffect(key1 = stock.price) {
        if (stock.change != PriceChange.UNCHANGED) {
            rowColor = when (stock.change) {
                PriceChange.INCREASE -> priceIncreaseColor.copy(alpha = 0.3f)
                PriceChange.DECREASE -> priceDecreaseColor.copy(alpha = 0.3f)
                else -> backgroundColor // Should not happen, but safe to handle
            }
            delay(1000)
            // 3. Reset the color back to the main background color.
            rowColor = backgroundColor
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(animatedColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stock.symbol,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "$${"%.2f".format(stock.price)}",
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        val (icon, color) = when (stock.change) {
            PriceChange.INCREASE -> Icons.Default.ArrowUpward to priceIncreaseColor
            PriceChange.DECREASE -> Icons.Default.ArrowDownward to priceDecreaseColor
            PriceChange.UNCHANGED -> null to Color.Gray
        }

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "Price Change",
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}