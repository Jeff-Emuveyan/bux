package com.example.core.model.network

data class ProductDetailRequest(
    val subscribeTo: List<String>,
    val unsubscribeFrom: List<String>
)