package com.bellogatecaliphate.core.source.remote

import com.bellogatecaliphate.core.model.response.ProductDetailResponse
import com.bellogatecaliphate.core.util.DATA_NOT_FOUND
import com.neovisionaries.ws.client.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
internal class RemoteDataSourceTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private val service: Service = mock()
    private val webSocket: WebSocket = mock()
    private val testDispatcher = TestCoroutineDispatcher()
    private val dummyResponseBody = object : ResponseBody() {
        override fun contentLength(): Long {
            return 11
        }

        override fun contentType(): MediaType? {
            return null
        }

        override fun source(): BufferedSource {
            TODO("Not yet implemented")
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = RemoteDataSource(service, webSocket, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test that remoteSource can fetch data when request is successful`() = runBlockingTest {
        val productDetailResponse = ProductDetailResponse(securityId = "id-1")

        `when`(service.getProduct(any())).thenReturn(Response.success(productDetailResponse))
        val result = remoteDataSource.getProduct("id-1")

        Assert.assertEquals(productDetailResponse.securityId, (result as ProductDetailResponse).securityId)
    }

    @Test
    fun `test that remoteSource will return a string when no data was found for the productIdentifier`() = runBlockingTest {

        `when`(service.getProduct(any())).thenReturn(Response.error(404, dummyResponseBody))
        val result = remoteDataSource.getProduct("id-1")

        Assert.assertEquals(DATA_NOT_FOUND, result)
    }
}