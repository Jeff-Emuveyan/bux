package com.example.product.util

import android.widget.LinearLayout

const val PRODUCT_IDENTIFIER_GERMANY30 = ""
const val PRODUCT_IDENTIFIER_US500 = ""
const val PRODUCT_IDENTIFIER_EURO_US = ""

fun LinearLayout.hideChildren(hide: Boolean) {
    for (index in 0..childCount) {
        val child = getChildAt(index)
        child.isEnabled = hide
    }
}