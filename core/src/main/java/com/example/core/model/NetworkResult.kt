package com.example.core.model

import com.example.core.model.network.ProductDetailResponse

class NetworkResult(val connectionStatus: ConnectionStatus,
                    val productDetailResponse: ProductDetailResponse? = null)