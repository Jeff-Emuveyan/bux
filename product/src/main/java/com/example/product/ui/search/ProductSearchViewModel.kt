package com.example.product.ui.search

import androidx.lifecycle.ViewModel
import com.example.core.model.network.ProductDetailResponse
import com.example.product.data.model.Result
import com.example.product.data.model.UIStateType
import com.example.product.data.repository.ProductRepository
import com.example.product.util.NOT_AVAILABLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(private val repository: ProductRepository): ViewModel() {

    private val _uiState = MutableStateFlow(Result(UIStateType.DEFAULT))
    val uiState = _uiState.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        repository.closeNetworkConnection()
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

    fun searchForProduct(identifier: String) {

    }

    fun getProductName(productDetailResponse: ProductDetailResponse): String {
        return  productDetailResponse.displayName ?: NOT_AVAILABLE
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