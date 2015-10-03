package com.github.gfx.helium.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final T binding;

    public BindingHolder(@NonNull Context context, @NonNull ViewGroup parent, @LayoutRes int resource) {
        super(LayoutInflater.from(context).inflate(resource, parent, false));
        binding = DataBindingUtil.bind(itemView);
    }
}
