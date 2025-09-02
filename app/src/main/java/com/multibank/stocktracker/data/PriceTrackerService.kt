// In the file: data/PriceTrackerService.kt

package com.multibank.stocktracker.data

import android.util.Log // 1. Make sure you have this import
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*

class PriceTrackerService {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    private val _messages = MutableSharedFlow<String>(replay = 1)

    val messages = _messages.asSharedFlow()

    fun connect(url: String) {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            // 2. Add the log inside this onMessage function
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("StockTrackerDebug", "<-- Received: $text") // <-- ADD THIS LINE
                _messages.tryEmit(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("PriceTrackerService", "WebSocket connection failed!", t)
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
    }
}