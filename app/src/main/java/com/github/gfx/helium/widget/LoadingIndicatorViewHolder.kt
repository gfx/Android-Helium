package com.github.gfx.helium.widget

import com.github.gfx.helium.util.LoadingAnimation

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.ViewGroup

class LoadingIndicatorViewHolder<T : ViewDataBinding>(context: Context, parent: ViewGroup,
                                                      @LayoutRes resource: Int) : BindingHolder<T>(context, parent, resource) {

    internal val loadingAnimation = LoadingAnimation()

    init {
        loadingAnimation.start(binding.root)
    }
}
