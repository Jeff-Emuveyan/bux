package com.example.product.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.core.model.network.ProductDetailResponse
import com.example.product.R
import com.example.product.data.model.Result
import com.example.product.data.model.UIStateType
import com.example.product.databinding.FragmentProductSearchBinding
import com.example.product.ui.details.ProductDetailsBottomSheet
import com.example.product.util.hideChildren
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

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

        binding.button.setOnClickListener {

        }
    }

    private fun setUpUi(result: Result) {
        when(result.type) {
            UIStateType.LOADING -> uiStateLoading()
            UIStateType.NETWORK_ERROR -> uiStateNetworkError()
            UIStateType.SUCCESS -> uiStateSuccess(result.productDetailResponse)
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

    private fun uiStateSuccess(productDetailResponse: ProductDetailResponse?) = with(binding) {
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
}