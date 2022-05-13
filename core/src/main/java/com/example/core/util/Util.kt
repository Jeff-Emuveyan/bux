package com.example.core.util

import kotlin.math.round

enum class WebServerResponseType(val type: String) {
    CONNECTED_TO_SERVER("connect.connected"),
    LIVE_STREAM_AVAILABLE("trading.quote")
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}