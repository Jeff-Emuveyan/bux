package com.example.product.data.model

import com.example.core.model.network.ProductDetailResponse

class Result(val type: UIStateType,
             val productDetailResponse: ProductDetailResponse? = null)