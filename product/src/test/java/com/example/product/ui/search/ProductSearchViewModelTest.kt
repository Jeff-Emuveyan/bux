package com.example.product.ui.search

import com.example.core.model.ConnectionStatus
import com.example.core.model.NetworkResult
import com.example.core.model.response.ProductDetailResponse
import com.example.product.data.model.UIStateType
import com.example.product.data.repository.ProductRepository
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
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
internal class ProductSearchViewModelTest {

    private lateinit var viewModel: ProductSearchViewModel
    private val repository: ProductRepository = mock()
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductSearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test that viewModel can set the right ui state for the any network result from the repository` () {
        val spyViewModel = spy(viewModel)

        spyViewModel.processResponse(NetworkResult(ConnectionStatus.MARKETS_ARE_CLOSED))
        assertEquals(UIStateType.MARKETS_ARE_CLOSED, viewModel.uiState.value.type)

        spyViewModel.processResponse(NetworkResult(ConnectionStatus.DEFAULT))
        assertEquals(UIStateType.DEFAULT, viewModel.uiState.value.type)

        spyViewModel.processResponse(NetworkResult(ConnectionStatus.NETWORK_ERROR))
        assertEquals(UIStateType.NETWORK_ERROR, viewModel.uiState.value.type)

        val networkResult = NetworkResult(ConnectionStatus.DATA_AVAILABLE, ProductDetailResponse())
        spyViewModel.processResponse(networkResult)
        verify(spyViewModel).emitAvailableData(networkResult.productDetailResponse)

        spyViewModel.processResponse(NetworkResult(ConnectionStatus.NO_DATA_FOUND))
        assertEquals(UIStateType.NO_RESULT, viewModel.uiState.value.type)
    }

    @Test
    fun `test that viewModel can emit the available product response data or emit a network error` () {
        val response = ProductDetailResponse(securityId = "sec-id")

        viewModel.emitAvailableData(response)

        assertEquals(UIStateType.SUCCESS, viewModel.uiState.value.type)

        viewModel.emitAvailableData(null)

        assertEquals(UIStateType.NETWORK_ERROR, viewModel.uiState.value.type)
    }

    @Test
    fun `test that the viewModel calls the repository when it needs to search for product`() = runBlocking {
        viewModel.searchForProduct("")
        verify(repository).searchForProductAndLiveUpdates(any())
    }

    @Test
    fun `test that viewModel can get product name`() {
        val productDetailResponse = ProductDetailResponse(displayName = "apple")

        val result = viewModel.getProductName(productDetailResponse)

        assertEquals("apple", result)
    }

    @Test
    fun `test that viewModel can get product identifier`() {
        val productDetailResponse = ProductDetailResponse(securityId = "apple")

        val result = viewModel.getProductIdentifier(productDetailResponse)

        assertEquals("apple", result)
    }

    @Test
    fun `test that viewModel can get the current price for display`() {
        val productDetailResponse = ProductDetailResponse(securityId = "apple")
        val spy = spy(viewModel)

        `when`(spy.getCurrentPrice(productDetailResponse)).thenReturn(250.0)
        `when`(spy.getCurrency(productDetailResponse)).thenReturn("$")

        val result = spy.getCurrentPriceForDisplay(productDetailResponse)
        assertEquals("$ 250.0", result)
    }

    @Test
    fun `test that viewModel can get the previous price for display`() {
        val productDetailResponse = ProductDetailResponse(securityId = "apple")
        val spy = spy(viewModel)

        `when`(spy.getPreviousPrice(productDetailResponse)).thenReturn(250.0)
        `when`(spy.getCurrency(productDetailResponse)).thenReturn("$")

        val result = spy.getPreviousPriceForDisplay(productDetailResponse)
        assertEquals("$ 250.0", result)
    }

    @Test
    fun `test that viewModel can get the correct percentage when a product has gained value`() {
        val productDetailResponse = ProductDetailResponse()
        val spy = spy(viewModel)

        `when`(spy.getCurrentPrice(productDetailResponse)).thenReturn(150.0)
        `when`(spy.getPreviousPrice(productDetailResponse)).thenReturn(100.0)

        val result = spy.getPercentageDifferenceOfPrices(productDetailResponse)
        assertEquals("50.0%",result)

        `when`(spy.getCurrentPrice(productDetailResponse)).thenReturn(300.0)
        `when`(spy.getPreviousPrice(productDetailResponse)).thenReturn(100.0)

        val result2 = spy.getPercentageDifferenceOfPrices(productDetailResponse)
        assertEquals("200.0%",result2)
    }

    @Test
    fun `test that viewModel can get the correct percentage when a product has lost value`() {
        val productDetailResponse = ProductDetailResponse()
        val spy = spy(viewModel)

        `when`(spy.getCurrentPrice(productDetailResponse)).thenReturn(50.0)
        `when`(spy.getPreviousPrice(productDetailResponse)).thenReturn(100.0)

        val result = spy.getPercentageDifferenceOfPrices(productDetailResponse)
        assertEquals("-50.0%",result)

        `when`(spy.getCurrentPrice(productDetailResponse)).thenReturn(100.0)
        `when`(spy.getPreviousPrice(productDetailResponse)).thenReturn(300.0)

        val result2 = spy.getPercentageDifferenceOfPrices(productDetailResponse)
        assertEquals("-66.667%",result2)
    }

    @Test
    fun `test that viewModel can determine if the product has risen or not`() {
        val productDetailResponse = ProductDetailResponse()
        val spy = spy(viewModel)

        `when`(spy.getCurrentPrice(productDetailResponse)).thenReturn(150.0)
        `when`(spy.getPreviousPrice(productDetailResponse)).thenReturn(100.0)

        val result = spy.hasProductRisen(productDetailResponse)
        assertEquals(true, result)
    }

    @Test
    fun `test that viewModel can get description`() {
        val productDetailResponse = ProductDetailResponse(description = "apple watch")
        val result = viewModel.getDescription(productDetailResponse)

        assertEquals("apple watch", result)
    }
}