package com.bellogatecaliphate.core.model.response

data class CurrentPrice(
    var amount: String?,
    val currency: String?,
    val decimals: Int?
)