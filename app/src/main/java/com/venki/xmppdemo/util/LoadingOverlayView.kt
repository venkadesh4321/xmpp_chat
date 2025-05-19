package com.venki.xmppdemo.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.venki.xmppdemo.R

class LoadingOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val progressBar: ProgressBar

    init {
        inflate(context, R.layout.view_loading_overlay, this)

        isClickable = true
        isFocusable = true

        progressBar = findViewById(R.id.overlay_progress_bar)
        visibility = GONE
    }

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }
}
