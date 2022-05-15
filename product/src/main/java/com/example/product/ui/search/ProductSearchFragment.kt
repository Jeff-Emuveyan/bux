package com.example.product.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.core.util.showSnackMessage
import com.example.product.R
import com.example.product.data.model.UIResult
import com.example.product.data.model.UIStateType
import com.example.product.databinding.FragmentProductSearchBinding
import com.example.product.ui.details.ProductDetailsBottomSheet
import com.example.product.util.PRODUCT_IDENTIFIER_EURO_US
import com.example.product.util.PRODUCT_IDENTIFIER_GERMANY30
import com.example.product.util.PRODUCT_IDENTIFIER_US500
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProductSearchFragment : Fragment() {

    private val viewModel by viewModels<ProductSearchViewModel>()

    private var _binding: FragmentProductSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentProductSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpUIState(UIResult(UIStateType.DEFAULT))
        handleClicks()
    }

    private fun observeData() {
        viewModel.uiState.onEach {
            setUpUIState(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun handleClicks() = with(binding) {
        button.setOnClickListener {
            val identifier = tvSearch.text
            searchForProduct(identifier?.toString())
        }

        layoutGermany.setOnClickListener {
            searchForProduct(PRODUCT_IDENTIFIER_GERMANY30)
        }

        layoutUS500.setOnClickListener {
            searchForProduct(PRODUCT_IDENTIFIER_US500)
        }

        layoutEuro.setOnClickListener {
            searchForProduct(PRODUCT_IDENTIFIER_EURO_US)
        }

        editTextTextLayout.setEndIconOnClickListener {
            tvSearch.text?.clear()
        }
    }

    private fun setUpUIState(UIResult: UIResult) {
        when(UIResult.type) {
            UIStateType.LOADING -> uiStateLoading()
            UIStateType.NETWORK_ERROR -> uiStateNetworkError()
            UIStateType.SUCCESS -> uiStateSuccess()
            UIStateType.NO_RESULT -> uiStateNoResult()
            UIStateType.MARKETS_ARE_CLOSED -> uiStateMarketsAreClosed()
            UIStateType.DEFAULT -> uiStateDefault()
        }
    }

    private fun uiStateLoading() = with(binding) {
        viewModel.isViewLoadingData = true
        button.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun uiStateNetworkError() = with(binding) {
        viewModel.isViewLoadingData = false
        uiStateDefault()
        showSnackMessage(requireContext(), requireView(), getText(R.string.network_error).toString())
    }

    private fun uiStateSuccess() = with(binding) {
        viewModel.isViewLoadingData = false
        openProductDetailsDialog()
        uiStateDefault()
    }

    private fun uiStateNoResult() = with(binding) {
        viewModel.isViewLoadingData = false
        uiStateDefault()
        showSnackMessage(requireContext(), requireView(), getText(R.string.not_found).toString())
    }

    private fun uiStateDefault() = with(binding) {
        viewModel.isViewLoadingData = false
        button.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    private fun uiStateMarketsAreClosed() {
        viewModel.isViewLoadingData = false
        uiStateDefault()
        showSnackMessage(requireContext(), requireView(), getText(R.string.markets_are_closed).toString())
    }

    private fun openProductDetailsDialog() {
        if (!ProductDetailsBottomSheet.isSheetOpen) {
            ProductDetailsBottomSheet().show(childFragmentManager, ProductDetailsBottomSheet.TAG)
        }
    }

    private fun searchForProduct(productIdentifier: String?) {
        if (productIdentifier.isNullOrBlank() || productIdentifier.isEmpty()) {
            showSnackMessage(requireContext(), requireView(), getText(R.string.invalid_input).toString())
            return
        }
        if (!viewModel.isViewLoadingData) {
            setUpUIState(UIResult(UIStateType.LOADING))
            viewModel.searchForProduct(productIdentifier)
        } else {
            Toast.makeText(requireContext(), getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
        }
    }
}