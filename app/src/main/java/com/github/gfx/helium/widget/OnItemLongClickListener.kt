package com.github.gfx.helium.widget

import android.view.View

interface OnItemLongClickListener<T> {

    fun onItemLongClick(view: View, item: T): Boolean
}
