package com.example.core.model

import com.example.core.model.response.ProductDetailResponse

class NetworkResult(val connectionStatus: ConnectionStatus,
                    val productDetailResponse: ProductDetailResponse? = null)