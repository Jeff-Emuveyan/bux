package com.example.product.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.ConnectionStatus
import com.example.core.model.NetworkResult
import com.example.core.model.network.ProductDetailResponse
import com.example.product.data.model.UIResult
import com.example.product.data.model.UIStateType
import com.example.product.data.repository.ProductRepository
import com.example.product.util.NOT_AVAILABLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(private val repository: ProductRepository): ViewModel() {

    private val _uiState = MutableStateFlow(UIResult(UIStateType.DEFAULT))
    val uiState = _uiState.asStateFlow()

    init { listenForSearchResult() }

    override fun onCleared() {
        super.onCleared()
        repository.closeNetworkConnection()
    }

    private fun listenForSearchResult() {
        repository.networkConnectionState.onEach {
            processResponse(it)
        }.launchIn(viewModelScope)
    }

    private fun processResponse(result: NetworkResult) {
        when (result.connectionStatus) {
            ConnectionStatus.DEFAULT -> { _uiState.value = UIResult(UIStateType.DEFAULT) }
            ConnectionStatus.NETWORK_ERROR -> { _uiState.value = UIResult(UIStateType.NETWORK_ERROR) }
            ConnectionStatus.NO_DATA_FOUND -> { _uiState.value = UIResult(UIStateType.NO_RESULT) }
            ConnectionStatus.DATA_AVAILABLE -> { emitAvailableData(result.productDetailResponse) }
        }
    }

    private fun emitAvailableData(productDetailResponse: ProductDetailResponse?) {
        if (productDetailResponse == null) {
            _uiState.value = UIResult(UIStateType.NETWORK_ERROR)
        } else {
            _uiState.value = UIResult(UIStateType.SUCCESS, productDetailResponse)
        }
    }

    private fun getCurrency(productDetailResponse: ProductDetailResponse): String {
        return productDetailResponse.quoteCurrency ?: ""
    }

    private fun getCurrentPrice(productDetailResponse: ProductDetailResponse): Double {
        val amountAsString = productDetailResponse.currentPrice?.amount
        return amountAsString?.toDouble() ?: 0.0
    }

    private fun getPreviousPrice(productDetailResponse: ProductDetailResponse): Double {
        val amountAsString = productDetailResponse.closingPrice?.amount
        return amountAsString?.toDouble() ?: 0.0
    }

    fun searchForProduct(productIdentifier: String) = viewModelScope.launch {
        repository.searchForProductAndUpdates(productIdentifier)
    }

    fun getProductName(productDetailResponse: ProductDetailResponse): String {
        return productDetailResponse.displayName ?: NOT_AVAILABLE
    }

    fun getProductIdentifier(productDetailResponse: ProductDetailResponse): String {
        return  productDetailResponse.securityId ?: NOT_AVAILABLE
    }

    fun getCurrentPriceForDisplay(productDetailResponse: ProductDetailResponse): String {
        val amount = getCurrentPrice(productDetailResponse)
        val currency = getCurrency(productDetailResponse)
        return "$currency $amount"
    }

    fun getPreviousPriceForDisplay(productDetailResponse: ProductDetailResponse): String {
        val amount = getPreviousPrice(productDetailResponse)
        val currency = getCurrency(productDetailResponse)
        return "$currency $amount"
    }

    fun getPercentageDifferenceOfPrices(productDetailResponse: ProductDetailResponse): String {
        val currentPrice = getCurrentPrice(productDetailResponse)
        val previousPrice = getPreviousPrice(productDetailResponse)
        val percentage = (previousPrice * 100) / currentPrice
        return if (currentPrice >= previousPrice) "$percentage%" else "-$percentage%"
    }

    fun hasProductRisen(productDetailResponse: ProductDetailResponse): Boolean {
        val currentPrice = getCurrentPrice(productDetailResponse)
        val previousPrice = getPreviousPrice(productDetailResponse)
        return currentPrice > previousPrice
    }
}