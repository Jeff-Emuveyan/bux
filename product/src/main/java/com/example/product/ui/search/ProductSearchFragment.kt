package com.example.product.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.product.R
import com.example.product.data.model.Result
import com.example.product.data.model.UIStateType
import com.example.product.databinding.FragmentProductSearchBinding
import com.example.product.ui.details.ProductDetailsBottomSheet
import com.example.product.util.PRODUCT_IDENTIFIER_EURO_US
import com.example.product.util.PRODUCT_IDENTIFIER_GERMANY30
import com.example.product.util.PRODUCT_IDENTIFIER_US500
import com.example.product.util.hideChildren
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProductSearchFragment : Fragment() {

    private val viewModel by viewModels<ProductSearchViewModel>()

    private var _binding: FragmentProductSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        setUpUi(Result(UIStateType.DEFAULT))
        handleClicks()
    }

    private fun observeData() {
        viewModel.uiState.onEach {
            setUpUi(it)
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
    }

    private fun setUpUi(result: Result) {
        when(result.type) {
            UIStateType.LOADING -> uiStateLoading()
            UIStateType.NETWORK_ERROR -> uiStateNetworkError()
            UIStateType.SUCCESS -> uiStateSuccess()
            UIStateType.NO_RESULT -> uiStateNoResult()
            UIStateType.DEFAULT -> uiStateDefault()
        }
    }

    private fun uiStateLoading() = with(binding) {
        button.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        linearLayout.hideChildren(true)
    }

    private fun uiStateNetworkError() = with(binding) {
        uiStateDefault()
        Snackbar.make(requireContext(), requireView(), getText(R.string.network_error), Snackbar.LENGTH_LONG).show()
    }

    private fun uiStateSuccess() = with(binding) {
        openProductDetailsDialog()
        uiStateDefault()
    }

    private fun uiStateNoResult() = with(binding) {
        uiStateDefault()
        Snackbar.make(requireContext(), requireView(), getText(R.string.not_found), Snackbar.LENGTH_LONG).show()
    }

    private fun uiStateDefault() = with(binding) {
        button.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        linearLayout.hideChildren(false)
    }

    private fun openProductDetailsDialog() {
        if (!ProductDetailsBottomSheet.isSheetOpen) {
        val action = ProductSearchFragmentDirections.actionProductSearchFragmentToProductDetailsBottomSheet()
        findNavController().navigate(action)
        }
    }

    private fun searchForProduct(identifier: String?) {
        if (identifier.isNullOrBlank() || identifier.isEmpty()) {
            Snackbar.make(requireContext(), requireView(), getText(R.string.invalid_input), Snackbar.LENGTH_LONG).show()
            return
        }
        setUpUi(Result(UIStateType.LOADING))
        viewModel.searchForProduct(identifier)
    }
}