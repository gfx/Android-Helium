package com.github.gfx.helium.widget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;

public class LayoutManagers {

    static final int ITEM_MIN_WIDTH = 280;

    public static RecyclerView.LayoutManager create(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int spanCount = (int) ((metrics.widthPixels / metrics.density) / ITEM_MIN_WIDTH);
        return new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
    }
}
