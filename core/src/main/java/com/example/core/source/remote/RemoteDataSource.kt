package com.example.core.source.remote

import com.example.core.model.network.ProductDetailResponse
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val service: Service,
                                           val webSocket: WebSocket,
                                           private val ioDispatcher: CoroutineDispatcher) {

    suspend fun getProduct(identifier: String): ProductDetailResponse? = withContext(ioDispatcher) {
        try {
            val result = service.getProduct(identifier)
            if (result.isSuccessful) result.body() else null
        } catch (e: Exception) {
            null
        }
    }

    @Throws(WebSocketException::class)
    suspend fun connect(): WebSocket = withContext(ioDispatcher) {
        webSocket.connect()
    }

    fun disconnect() {
        webSocket.disconnect()
    }

    fun getProductDetails(request: String) {
        webSocket.sendText(request, true)
    }
}