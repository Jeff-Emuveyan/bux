package com.example.product.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.core.model.network.ProductDetailResponse
import com.example.product.R
import com.example.product.databinding.DialogFragmentProductDetailsBinding
import com.example.product.ui.search.ProductSearchViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.product.data.model.Result
import com.example.product.data.model.UIStateType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProductDetailsBottomSheet: BottomSheetDialogFragment() {

    companion object{
        var isSheetOpen:Boolean = false
    }

    private var _binding: DialogFragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ProductSearchViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = DialogFragmentProductDetailsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onDestroy() {
        super.onDestroy()
        isSheetOpen = false
        _binding = null
    }

    private fun observeData() {
        sharedViewModel.uiState.onEach {
            setUpUIState(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setUpUIState(result: Result) {
        when(result.type) {
            UIStateType.NETWORK_ERROR -> uiStateNetworkError()
            UIStateType.SUCCESS -> uiStateSuccess(result.productDetailResponse)
            else -> uiStateNetworkError()
        }
    }

    private fun uiStateNetworkError() = with(binding) {
        tvNetworkError.visibility = View.VISIBLE
        networkIndicator.background = ContextCompat.getDrawable(requireContext(), R.drawable.circle_red)
    }

    private fun uiStateSuccess(productDetailResponse: ProductDetailResponse?) = with(binding) {
        if (productDetailResponse == null) return
        tvNetworkError.visibility = View.GONE
        networkIndicator.background = ContextCompat.getDrawable(requireContext(), R.drawable.circle_green)

        tvProductName.text = sharedViewModel.getProductName(productDetailResponse)
        tvProductIdentifier.text = sharedViewModel.getProductIdentifier(productDetailResponse)
        tvCurrentPrice.text = sharedViewModel.getCurrentPriceForDisplay(productDetailResponse)
        tvPreviousPrice.text = sharedViewModel.getPreviousPriceForDisplay(productDetailResponse)
        tvPercentage.text = sharedViewModel.getPercentageDifferenceOfPrices(productDetailResponse)
        val hasRisen = sharedViewModel.hasProductRisen(productDetailResponse)
        if (hasRisen) {
            imageView2.setImageResource(R.drawable.arrow_up)
        } else {
            imageView2.setImageResource(R.drawable.arrow_down)
        }
    }

}