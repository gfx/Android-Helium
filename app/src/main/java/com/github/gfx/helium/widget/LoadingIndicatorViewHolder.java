package com.github.gfx.helium.widget;

import com.github.gfx.helium.util.LoadingAnimation;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

public class LoadingIndicatorViewHolder<T extends ViewDataBinding> extends BindingHolder<T> {

    final LoadingAnimation loadingAnimation = new LoadingAnimation();

    public LoadingIndicatorViewHolder(@NonNull Context context, @NonNull ViewGroup parent,
            @LayoutRes int resource) {
        super(context, parent, resource);

        loadingAnimation.start(binding.getRoot());
    }
}
