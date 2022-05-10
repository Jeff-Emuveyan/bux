package com.example.core.source.remote

import android.util.Log
import com.example.core.model.ProductDetailResponse
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val webSocket: WebSocket,
                                           private val ioDispatcher: CoroutineDispatcher) {

    fun getProductDetails(productIdentifier: String) = flow<ProductDetailResponse?> {
        webSocket.addListener(object : WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                super.onTextMessage(websocket, text)
                Log.e("Jeff", "Message: $text")
            }

            override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onUnexpectedError(websocket, cause)
                Log.e("Jeff", "Message: onUnexpectedError")
            }

            override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onError(websocket, cause)
                Log.e("Jeff", "Message: onError")
            }
        })

        connect()
        emit(null)
    }.flowOn(ioDispatcher).catch {
        Log.e("Jeff", "Message: ${it.message}")
    }

    private fun connect() {
       webSocket.connect()
    }

    fun disconnect() {
        webSocket.disconnect()
    }
}