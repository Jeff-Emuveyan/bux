package com.example.core.util

enum class WebServerResponseType(val type: String) {
    CONNECTED_TO_SERVER("connect.connected"),
    LIVE_STREAM_AVAILABLE("trading.quote")
}