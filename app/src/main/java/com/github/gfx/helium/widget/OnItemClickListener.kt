package com.github.gfx.helium.widget

import android.view.View

interface OnItemClickListener<T> {

    fun onItemClick(view: View, item: T)
}
