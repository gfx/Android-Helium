package com.github.gfx.helium.widget;

import android.support.annotation.NonNull;
import android.view.View;

public interface OnItemLongClickListener<T> {

    boolean onItemLongClick(@NonNull View view, T item);
}
