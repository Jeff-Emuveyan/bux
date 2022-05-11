package com.example.core.model

class ServerResponse(val connectionStatus: ConnectionStatus,
                     val productDetailResponse: ProductDetailResponse? = null)