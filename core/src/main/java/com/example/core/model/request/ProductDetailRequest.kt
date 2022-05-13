package com.example.core.model.request

data class ProductDetailRequest(
    val subscribeTo: List<String>,
    val unsubscribeFrom: List<String>
)