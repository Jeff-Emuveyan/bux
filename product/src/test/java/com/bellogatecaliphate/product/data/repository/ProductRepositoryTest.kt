package com.bellogatecaliphate.product.data.repository

import com.bellogatecaliphate.core.model.ConnectionStatus
import com.bellogatecaliphate.core.model.request.ProductDetailRequest
import com.bellogatecaliphate.core.model.response.ProductDetailResponse
import com.bellogatecaliphate.core.model.response.WebServerResponse
import com.bellogatecaliphate.core.source.remote.RemoteDataSource
import com.bellogatecaliphate.core.util.DATA_NOT_FOUND
import com.bellogatecaliphate.product.CONNECTED_TO_SEVER_SUCCESSFULLY_JSON
import com.bellogatecaliphate.product.DATA_AVAILABLE_JSON
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
internal class ProductRepositoryTest {

    private lateinit var repository: ProductRepository
    private val remoteDataSource: RemoteDataSource = mock()
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = ProductRepository(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test that the repository performs the right action for each type of remoteDataSource response`():
            Unit = runBlocking {
        val spy = spy(repository)
        `when`(spy.isTodayWeekEnd()).thenReturn(false)

        `when`(remoteDataSource.getProduct(any())).thenReturn(null)
        spy.searchForProductAndLiveUpdates("id-1")
        assertEquals(ConnectionStatus.NETWORK_ERROR, spy.networkConnectionState.value.connectionStatus)

        `when`(remoteDataSource.getProduct(any())).thenReturn(DATA_NOT_FOUND)
        spy.searchForProductAndLiveUpdates("id-1")
        assertEquals(ConnectionStatus.NO_DATA_FOUND, spy.networkConnectionState.value.connectionStatus)

        val productDetailResponse = ProductDetailResponse()
        doNothing().`when`(spy).observeServerResponse(productDetailResponse)
        `when`(remoteDataSource.getProduct(any())).thenReturn(productDetailResponse)
        spy.searchForProductAndLiveUpdates("id-1")
        verify(spy).doWork(productDetailResponse)

        `when`(spy.isTodayWeekEnd()).thenReturn(true)
        spy.searchForProductAndLiveUpdates("id-1")
        assertEquals(ConnectionStatus.MARKETS_ARE_CLOSED, spy.networkConnectionState.value.connectionStatus)
    }

    @Test
    fun `test that the repository will request for live updates or attempt to connect to sever depending on response`():
            Unit = runBlocking {
        val productDetailResponse = ProductDetailResponse(securityId = "id-1")
        val spy = spy(repository)
        doNothing().`when`(spy).observeServerResponse(productDetailResponse)

        `when`(remoteDataSource.isConnected()).thenReturn(true)
        spy.doWork(productDetailResponse)
        verify(spy).requestForLiveProductUpdates("id-1")

        `when`(remoteDataSource.isConnected()).thenReturn(false)
        spy.doWork(productDetailResponse)
        verify(remoteDataSource).connect()
    }

    @Test
    fun `test that viewModel can process response from the webserver correctly`() {
        val spy = spy(repository)
        val productDetailResponse = ProductDetailResponse(securityId = "id-1")
        val dataAvailable = Gson().fromJson(DATA_AVAILABLE_JSON, WebServerResponse::class.java)

        spy.processResponse(productDetailResponse, CONNECTED_TO_SEVER_SUCCESSFULLY_JSON)
        verify(spy).requestForLiveProductUpdates("id-1")

        spy.processResponse(productDetailResponse, DATA_AVAILABLE_JSON)
        verify(spy).emitRealTimeUpdate(productDetailResponse, dataAvailable)
    }

    @Test
    fun `test that the right subscribe message is sent to the server`() {
        val productIdentifier = "A-1"
        val subscribeList =  listOf("trading.product.$productIdentifier")
        var unSubscribeList = emptyList<String>()
        val expectedRequest = ProductDetailRequest(subscribeList, unSubscribeList)

        repository.requestForLiveProductUpdates(productIdentifier)

        verify(remoteDataSource).getProductDetails(Gson().toJson(expectedRequest))
    }

    @Test
    fun `test that the right change of subscription message is sent to the server`() {
        val currentProductIdentifier = "A-1"
        val productDetailResponse = ProductDetailResponse(securityId = currentProductIdentifier)
        val serverResponse = Gson().fromJson(DATA_AVAILABLE_JSON, WebServerResponse::class.java)
        repository.emitRealTimeUpdate(productDetailResponse, serverResponse)

        val newProductIdentifier = "A-2"
        val subscribeList =  listOf("trading.product.$newProductIdentifier")
        var unSubscribeList =  listOf("trading.product.$currentProductIdentifier")
        val expectedRequest = ProductDetailRequest(subscribeList, unSubscribeList)
        repository.requestForLiveProductUpdates(newProductIdentifier)

        verify(remoteDataSource).getProductDetails(Gson().toJson(expectedRequest))
    }

    @Test
    fun `test that the repository can interpret a successful sever connection json response`() {
        val serverResponse = Gson().fromJson(CONNECTED_TO_SEVER_SUCCESSFULLY_JSON, WebServerResponse::class.java)
        val result = repository.isConnectedToServer(serverResponse)
        assertEquals(true, result)

        val serverResponseB = Gson().fromJson(DATA_AVAILABLE_JSON, WebServerResponse::class.java)
        val resultB = repository.isConnectedToServer(serverResponseB)
        assertEquals(false, resultB)
    }

    @Test
    fun `test that the repository can interpret a successful data fetch from server`() {
        val serverResponse = Gson().fromJson(CONNECTED_TO_SEVER_SUCCESSFULLY_JSON, WebServerResponse::class.java)
        val result = repository.isLiveDataStreamAvailable(serverResponse)
        assertEquals(false, result)

        val serverResponseB = Gson().fromJson(DATA_AVAILABLE_JSON, WebServerResponse::class.java)
        val resultB = repository.isLiveDataStreamAvailable(serverResponseB)
        assertEquals(true, resultB)
    }
}