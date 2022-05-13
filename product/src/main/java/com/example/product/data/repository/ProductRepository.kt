package com.example.product.data.repository

import android.util.Log
import com.example.core.model.ConnectionStatus
import com.example.core.model.NetworkResult
import com.example.core.model.network.ProductDetailResponse
import com.example.core.source.remote.RemoteDataSource
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    private val _networkConnectionState = MutableStateFlow(NetworkResult(ConnectionStatus.DEFAULT))
    val networkConnectionState = _networkConnectionState.asStateFlow()

    /*fun searchForProduct(productIdentifier: String) {
        val request = ProductDetailRequest(listOf(productIdentifier), emptyList())
        val requestAsString = Gson().toJson(request)
        remoteDataSource.getProductDetails(requestAsString)
    }*/

    suspend fun searchForProductAndUpdates(productIdentifier: String) {
        val productResponse = remoteDataSource.getProduct(productIdentifier)
        if (productResponse == null) {
            _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
        } else {
            searchForProductUpdates(productResponse)
        }
    }

    private suspend fun searchForProductUpdates(productResponse: ProductDetailResponse) {
        observeServerResponse()
        remoteDataSource.connect()
    }

    private fun observeServerResponse() {
        remoteDataSource.webSocket.addListener(object : WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                super.onTextMessage(websocket, text)
                processResponse(text)
            }

            override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onUnexpectedError(websocket, cause)
                _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
            }

            override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onError(websocket, cause)
                _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
            }
        })
    }

    private fun processResponse(serverResponse: String?) {

    }

    fun closeNetworkConnection() = remoteDataSource.disconnect()
}