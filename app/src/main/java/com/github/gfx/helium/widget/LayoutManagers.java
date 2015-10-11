package com.github.gfx.helium.widget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;

public class LayoutManagers {

    static final int ITEM_MIN_WIDTH = 280;

    final int spanCount;

    public LayoutManagers(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        spanCount = Math.max((int) ((metrics.widthPixels / metrics.density) / ITEM_MIN_WIDTH), 1);
    }

    public int getSpanCount() {
        return spanCount;
    }

    public RecyclerView.LayoutManager create() {
        return new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
    }
}
