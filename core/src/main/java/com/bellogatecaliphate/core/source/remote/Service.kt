package com.bellogatecaliphate.core.source.remote

import com.bellogatecaliphate.core.model.response.ProductDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Service {

    @GET("products/{identifier}")
    suspend fun getProduct(@Path("identifier") identifier: String): Response<ProductDetailResponse>

}