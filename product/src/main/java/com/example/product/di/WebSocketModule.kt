package com.bellogatecaliphate.product.di

import com.bellogatecaliphate.core.util.AUTHORIZATION
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    fun provideWebSocket(): WebSocket {
        val factory = WebSocketFactory().setConnectionTimeout(5000)
        val webSocket = factory.createSocket("https://rtf.beta.getbux.com/subscriptions/me")
        webSocket.addHeader("Authorization", AUTHORIZATION)
        return webSocket
    }
}