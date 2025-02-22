package com.ph.notestash.common.ui

import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import androidx.core.view.updatePadding

fun View.applyWindowInsets(
    @InsetsType typeMask: Int = WindowInsetsCompat.Type.systemBars(),
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,
) = doOnApplyWindowInsets { view, windowInsetsCompat, initialPadding ->
    val insets = windowInsetsCompat.getInsets(typeMask)

    val insetsStart = if (start) insets.left else 0
    val insetsTop = if (top) insets.top else 0
    val insetsEnd = if (end) insets.right else 0
    val insetsBottom = if (bottom) insets.bottom else 0

    view.updatePadding(
        left = initialPadding.start + insetsStart,
        top = initialPadding.top + insetsTop,
        right = initialPadding.end + insetsEnd,
        bottom = initialPadding.bottom + insetsBottom
    )
}

private inline fun View.doOnApplyWindowInsets(
    crossinline onApplyWindowInsets: (
        view: View,
        windowInsetsCompat: WindowInsetsCompat,
        initialPadding: PaddingHolder,
    ) -> Unit
) {
    val initialPadding = PaddingHolder(
        start = paddingStart,
        top = paddingTop,
        end = paddingEnd,
        bottom = paddingBottom,
    )

    setOnApplyWindowInsetsListener { view, windowInsets ->
        val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(windowInsets, view)

        onApplyWindowInsets(view, windowInsetsCompat, initialPadding)

        windowInsets
    }

    requestApplyInsets()
}

data class PaddingHolder(
    val start: Int,
    val top: Int,
    val end: Int,
    val bottom: Int,
)