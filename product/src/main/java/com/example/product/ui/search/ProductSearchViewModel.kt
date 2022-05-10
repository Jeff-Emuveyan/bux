package com.example.product.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.product.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(private val repository: ProductRepository): ViewModel() {

    fun getProductDetails(productIdentifier: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.getProductDetails(productIdentifier).collect {
            Log.e("Jeff", "Collecting...")
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.closeNetworkConnection()
    }
}