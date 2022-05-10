package com.example.product.data.repository

import com.example.core.source.remote.RemoteDataSource
import javax.inject.Inject

class ProductRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    fun getProductDetails(productIdentifier: String) = remoteDataSource.getProductDetails(productIdentifier)

    fun closeNetworkConnection() = remoteDataSource.disconnect()
}