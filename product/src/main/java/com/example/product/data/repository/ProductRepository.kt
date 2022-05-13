package com.example.product.data.repository

import com.example.core.model.ConnectionStatus
import com.example.core.model.NetworkResult
import com.example.core.model.request.ProductDetailRequest
import com.example.core.model.response.ProductDetailResponse
import com.example.core.model.response.WebServerResponse
import com.example.core.source.remote.RemoteDataSource
import com.example.core.util.WebServerResponseType
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    private val _networkConnectionState = MutableStateFlow(NetworkResult(ConnectionStatus.DEFAULT))
    val networkConnectionState = _networkConnectionState.asStateFlow()
    private lateinit var listener: WebSocketAdapter

    suspend fun searchForProductAndUpdates(productIdentifier: String) {
        val productResponse = remoteDataSource.getProduct(productIdentifier)
        if (productResponse == null) {
            _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
        } else {
            searchForProductUpdates(productResponse)
        }
    }

    private suspend fun searchForProductUpdates(productResponse: ProductDetailResponse) = try {
        observeServerResponse(productResponse)
        remoteDataSource.connect()
    } catch (e: WebSocketException) {
        _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
    }

    private fun observeServerResponse(productResponse: ProductDetailResponse) {
        listener = object : WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                super.onTextMessage(websocket, text)
                processResponse(productResponse, text)
            }

            override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onUnexpectedError(websocket, cause)
                _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
            }

            override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onError(websocket, cause)
                _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
            }
        }
        remoteDataSource.webSocket.addListener(listener)
    }

    fun stopObserving() {
        remoteDataSource.webSocket.removeListener(listener)
    }

    private fun processResponse(productResponse: ProductDetailResponse, serverResponseText: String?) {
        try {
            val serverResponse = Gson().fromJson(serverResponseText, WebServerResponse::class.java)
            val productIdentifier = productResponse.securityId
            when {
                isConnectedToServer(serverResponse) -> requestForLiveProductUpdates(productIdentifier)
                isLiveDataStreamAvailable(serverResponse) -> { emitRealTimeUpdate(productResponse, serverResponse) }
            }
        } catch (e: JsonSyntaxException) {
            _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
        }
    }

    private fun requestForLiveProductUpdates(productIdentifier: String?) {
        if (productIdentifier == null) return

        val request = ProductDetailRequest(listOf("trading.product.$productIdentifier"), emptyList())
        val requestAsString = Gson().toJson(request)
        remoteDataSource.getProductDetails(requestAsString)
    }

    private fun isConnectedToServer(serverResponse: WebServerResponse?): Boolean {
        if (serverResponse == null) return false
        val t = serverResponse.t
        return t == WebServerResponseType.CONNECTED_TO_SERVER.type
    }

    private fun isLiveDataStreamAvailable(serverResponse: WebServerResponse?): Boolean {
        if (serverResponse == null) return false
        val t = serverResponse.t
        return t == WebServerResponseType.LIVE_STREAM_AVAILABLE.type
    }

    private fun emitRealTimeUpdate(productResponse: ProductDetailResponse,
                                   serverResponse: WebServerResponse?) {
        if (serverResponse == null) return

        val latestCurrentPrice = serverResponse.body?.currentPrice
        productResponse.currentPrice?.amount = latestCurrentPrice
        _networkConnectionState.value = NetworkResult(ConnectionStatus.DATA_AVAILABLE, productResponse)
    }

    fun closeNetworkConnection() = remoteDataSource.disconnect()
}