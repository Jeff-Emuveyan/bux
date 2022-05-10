package com.example.product.data.model

import com.example.core.model.network.ProductDetailResponse
import com.example.product.data.model.ui.UIStateType

class Result(val type: UIStateType,
             val productDetailResponse: ProductDetailResponse? = null)