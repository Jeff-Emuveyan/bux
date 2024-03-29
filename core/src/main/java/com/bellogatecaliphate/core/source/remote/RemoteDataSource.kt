package com.bellogatecaliphate.core.source.remote

import com.bellogatecaliphate.core.util.DATA_NOT_FOUND
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val service: Service,
                                           val webSocket: WebSocket,
                                           private val ioDispatcher: CoroutineDispatcher) {

    suspend fun getProduct(identifier: String): Any? = withContext(ioDispatcher) {
        try {
            val result = service.getProduct(identifier)
            when {
                result.code() == 404 -> DATA_NOT_FOUND
                result.isSuccessful -> result.body()
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    @Throws(WebSocketException::class)
    suspend fun connect() = withContext(ioDispatcher) {
        if (!webSocket.isOpen) {
            webSocket.connect()
        }
    }

    fun disconnect() {
        webSocket.disconnect()
    }

    fun getProductDetails(request: String) {
        webSocket.sendText(request, true)
    }

    fun isConnected() = webSocket.state == WebSocketState.OPEN
}