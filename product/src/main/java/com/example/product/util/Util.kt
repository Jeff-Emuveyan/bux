package com.example.product.util

import android.widget.LinearLayout

fun LinearLayout.hideChildren(hide: Boolean) {
    for (index in 0..childCount) {
        val child = getChildAt(index)
        child.isEnabled = hide
    }
}