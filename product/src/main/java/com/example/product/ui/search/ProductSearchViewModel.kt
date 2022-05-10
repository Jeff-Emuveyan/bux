package com.example.product.ui.search

import androidx.lifecycle.ViewModel
import com.example.product.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(private val repository: ProductRepository): ViewModel() {

}