package com.bellogatecaliphate.product.data.repository

import com.bellogatecaliphate.core.model.ConnectionStatus
import com.bellogatecaliphate.core.model.NetworkResult
import com.bellogatecaliphate.core.model.request.ProductDetailRequest
import com.bellogatecaliphate.core.model.response.ProductDetailResponse
import com.bellogatecaliphate.core.model.response.WebServerResponse
import com.bellogatecaliphate.core.source.remote.RemoteDataSource
import com.bellogatecaliphate.core.util.DATA_NOT_FOUND
import com.bellogatecaliphate.core.util.WebServerResponseType
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProductRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    private val _networkConnectionState = MutableStateFlow(NetworkResult(ConnectionStatus.DEFAULT))
    val networkConnectionState = _networkConnectionState.asStateFlow()
    private lateinit var listener: WebSocketAdapter
    private var currentProductIdentifierShownToUser: String? = null

    suspend fun searchForProductAndLiveUpdates(productIdentifier: String) {
        if (isTodayWeekEnd()) {
            _networkConnectionState.value = NetworkResult(ConnectionStatus.MARKETS_ARE_CLOSED)
            return
        }
        val productResponse = remoteDataSource.getProduct(productIdentifier)
        when {
            productResponse == null -> _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
            (productResponse is String && productResponse == DATA_NOT_FOUND) ->
                    { _networkConnectionState.value = NetworkResult(ConnectionStatus.NO_DATA_FOUND) }
            productResponse is ProductDetailResponse -> doWork(productResponse)
        }
    }

    suspend fun doWork(productResponse: ProductDetailResponse) = try {
        observeServerResponse(productResponse)
        if (remoteDataSource.isConnected()) {
            requestForLiveProductUpdates(productResponse.securityId)
        } else {
            remoteDataSource.connect()
        }
    } catch (e: WebSocketException) {
        _networkConnectionState.value = NetworkResult(ConnectionStatus.NETWORK_ERROR)
    }

    fun observeServerResponse(productResponse: ProductDetailResponse) {
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

    fun stopObservingServerResponse() {
        remoteDataSource.webSocket.removeListener(listener)
    }

    fun processResponse(productResponse: ProductDetailResponse, serverResponseText: String?) {
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

    fun requestForLiveProductUpdates(productIdentifier: String?) {
        if (productIdentifier == null) return

        val subscribeList = listOf("trading.product.$productIdentifier")
        val unSubscribeList = listOf("trading.product.$currentProductIdentifierShownToUser")

        val request = if (currentProductIdentifierShownToUser == null ) {
            ProductDetailRequest(subscribeList, emptyList())
        } else {
            ProductDetailRequest(subscribeList, unSubscribeList)
        }
        val requestAsString = Gson().toJson(request)
        remoteDataSource.getProductDetails(requestAsString)
    }

    fun isConnectedToServer(serverResponse: WebServerResponse?): Boolean {
        if (serverResponse == null) return false
        val t = serverResponse.t
        return t == WebServerResponseType.CONNECTED_TO_SERVER.type
    }

    fun isLiveDataStreamAvailable(serverResponse: WebServerResponse?): Boolean {
        if (serverResponse == null) return false
        val t = serverResponse.t
        return t == WebServerResponseType.LIVE_STREAM_AVAILABLE.type
    }

    fun emitRealTimeUpdate(productResponse: ProductDetailResponse,
                                   serverResponse: WebServerResponse?) {
        if (serverResponse == null) return
        currentProductIdentifierShownToUser = productResponse.securityId

        val latestCurrentPrice = serverResponse.body?.currentPrice
        productResponse.currentPrice?.amount = latestCurrentPrice
        _networkConnectionState.value = NetworkResult(ConnectionStatus.DATA_AVAILABLE, productResponse)
    }

    fun closeNetworkConnection() = remoteDataSource.disconnect()

    fun isTodayWeekEnd(): Boolean {
        return getTodayDate().contains("Sat") || getTodayDate().contains("Sun")
    }

    private fun getTodayDate(): String {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("EEE MMM dd,yyyy HH:mm")
        val resultDate = Date(currentTime)
        return sdf.format(resultDate)
    }
}