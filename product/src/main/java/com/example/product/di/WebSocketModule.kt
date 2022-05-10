package com.example.product.di

import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    private const val AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1Y" +
            "iI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIs" +
            "InNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzk" +
            "sImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ." +
            "M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg"

    @Provides
    fun provideWebSocket(): WebSocket {
        val factory = WebSocketFactory().setConnectionTimeout(5000)
        val webSocket = factory.createSocket("https://rtf.beta.getbux.com/subscriptions/me")
        webSocket.addHeader("Authorization", AUTHORIZATION)
        return webSocket
    }
}