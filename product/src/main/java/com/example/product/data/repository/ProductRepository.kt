package com.example.product.data.repository

import android.util.Log
import com.example.core.model.ConnectionStatus
import com.example.core.model.NetworkResult
import com.example.core.model.request.ProductDetailRequest
import com.example.core.model.response.ProductDetailResponse
import com.example.core.model.response.WebServerResponse
import com.example.core.source.remote.RemoteDataSource
import com.example.core.util.DATA_NOT_FOUND
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
    private var currentProductIdentifierShownToUser: String? = null

    suspend fun searchForProductAndUpdates(productIdentifier: String) {
        val productResponse = remoteDataSource.getProduct(productIdentifier)
        when {
            productResponse == null -> _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
            (productResponse is String && productResponse == DATA_NOT_FOUND) -> {
                _networkConnectionState.value = NetworkResult(ConnectionStatus.NO_DATA_FOUND)
            }
            productResponse is ProductDetailResponse -> searchForProductUpdates(productResponse)
        }

    }

    private suspend fun searchForProductUpdates(productResponse: ProductDetailResponse) = try {
        observeServerResponse(productResponse)
        if (remoteDataSource.isConnected()) {
            requestForLiveProductUpdates(productResponse.securityId)
        } else {
            remoteDataSource.connect()
        }
    } catch (e: WebSocketException) {
        _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
    }

    private fun observeServerResponse(productResponse: ProductDetailResponse) {
        listener = object : WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                super.onTextMessage(websocket, text)
                Log.e("JEFF", text ?: "")
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

    fun stopObservingServerResponse() {
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

        val subscribeList = listOf("trading.product.$productIdentifier")
        val unSubscribeList = listOf("trading.product.$currentProductIdentifierShownToUser")

        val request = if (currentProductIdentifierShownToUser == null ) {
            ProductDetailRequest(subscribeList, emptyList())
        } else {
            ProductDetailRequest(subscribeList, unSubscribeList)
        }
        val requestAsString = Gson().toJson(request)
        Log.e("JEFF", "\n\n\n $requestAsString")
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
        currentProductIdentifierShownToUser = productResponse.securityId

        val latestCurrentPrice = serverResponse.body?.currentPrice
        productResponse.currentPrice?.amount = latestCurrentPrice
        _networkConnectionState.value = NetworkResult(ConnectionStatus.DATA_AVAILABLE, productResponse)
    }

    fun closeNetworkConnection() = remoteDataSource.disconnect()
}