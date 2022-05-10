package com.example.product.data.repository

import com.example.core.model.network.ProductDetailRequest
import com.example.core.source.remote.RemoteDataSource
import com.google.gson.Gson
import javax.inject.Inject

class ProductRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    fun getProductDetails(productIdentifier: String) {
        val request = ProductDetailRequest(listOf(productIdentifier), emptyList())
        val requestAsString = Gson().toJson(request)
        remoteDataSource.getProductDetails(requestAsString)
    }

    suspend fun establishServerConnection() = remoteDataSource.connect()

    fun observeServerResponse() = remoteDataSource.observeServerResponse()

    fun closeNetworkConnection() = remoteDataSource.disconnect()
}