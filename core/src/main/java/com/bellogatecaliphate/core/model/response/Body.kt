package com.bellogatecaliphate.core.model.response

data class Body(
    val clientVersion: String?,
    val pop: Pop?,
    val sessionId: String?,
    val time: Long?,
    val securityId: String?,
    val currentPrice: String?
)