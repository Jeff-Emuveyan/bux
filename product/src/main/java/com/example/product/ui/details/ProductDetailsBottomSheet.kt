package com.example.product.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.product.R
import com.example.product.databinding.DialogFragmentProductDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductDetailsBottomSheet: BottomSheetDialogFragment() {

    companion object{
        var isSheetOpen:Boolean = false
    }

    private var _binding: DialogFragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFragmentProductDetailsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //needed to remove the white background color of the bottom sheet.
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        isSheetOpen = false
        _binding = null
    }
}