package com.bellogatecaliphate.core.model

import com.bellogatecaliphate.core.model.response.ProductDetailResponse

class NetworkResult(val connectionStatus: ConnectionStatus,
                    val productDetailResponse: ProductDetailResponse? = null)