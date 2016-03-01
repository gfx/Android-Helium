package com.github.gfx.helium.widget

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

open class BindingHolder<T : ViewDataBinding>(context: Context, parent: ViewGroup, @LayoutRes resource: Int) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(resource, parent, false)) {

    val binding: T

    init {
        binding = DataBindingUtil.bind<T>(itemView)
    }
}
