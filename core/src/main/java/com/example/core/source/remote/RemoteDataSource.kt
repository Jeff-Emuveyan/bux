package com.example.core.source.remote

import android.util.Log
import com.example.core.model.ConnectionStatus
import com.example.core.model.ServerResponse
import com.example.core.model.network.ProductDetailResponse
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val webSocket: WebSocket,
                                           private val ioDispatcher: CoroutineDispatcher) {

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

    fun observeServerResponse() = flow<ServerResponse> {
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
                Log.e("Jeff", "Message: onError: ${cause?.message}")
            }
        })
        emit(ServerResponse(ConnectionStatus.CONNECTION_ESTABLISHED))
    }.flowOn(ioDispatcher).catch {
        Log.e("Jeff", "Message: ${it.message}")
    }
}