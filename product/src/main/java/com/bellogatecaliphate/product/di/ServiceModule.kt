package com.bellogatecaliphate.product.di
import com.bellogatecaliphate.core.model.response.ProductDetailResponse
import com.bellogatecaliphate.core.source.remote.Service
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun bindService(serviceImpl: ServiceImpl): Service
}

class ServiceImpl @Inject constructor(var retrofit: Retrofit): Service {

    override suspend fun getProduct(identifier: String): Response<ProductDetailResponse> {
        return retrofit.create(Service::class.java).getProduct(identifier)
    }
}