package com.example.product.ui.search

import androidx.lifecycle.ViewModel
import com.example.core.model.network.ProductDetailResponse
import com.example.product.data.model.Result
import com.example.product.data.model.UIStateType
import com.example.product.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(private val repository: ProductRepository): ViewModel() {

    private val _uiState = MutableStateFlow(Result(UIStateType.DEFAULT))
    val uiState = _uiState.asStateFlow()

    fun searchForProduct(identifier: String) {

    }

    fun getProductName(productDetailResponse: ProductDetailResponse): String {

    }

    fun getProductIdentifier(productDetailResponse: ProductDetailResponse): String {

    }

    fun getCurrentPrice(productDetailResponse: ProductDetailResponse): CharSequence? {

    }

    fun getPreviousPrice(productDetailResponse: ProductDetailResponse): CharSequence? {

    }

    fun getPerentageDifferenceOfPrices(productDetailResponse: ProductDetailResponse): CharSequence? {

    }

    fun hasProductRisen(): Boolean {

    }
}