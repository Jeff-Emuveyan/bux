package com.bellogatecaliphate.product.data.model

import com.bellogatecaliphate.core.model.response.ProductDetailResponse

class UIResult(val type: UIStateType,
               val productDetailResponse: ProductDetailResponse? = null)