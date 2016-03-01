package com.github.gfx.helium.widget

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.DisplayMetrics

import javax.inject.Inject

class LayoutManagers
@Inject
constructor(activity: Activity) {

    val spanCount: Int

    init {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)

        spanCount = Math.max((metrics.widthPixels / metrics.density / ITEM_MIN_WIDTH).toInt(), 1)
    }

    fun create(): RecyclerView.LayoutManager {
        return StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }

    companion object {

        internal val ITEM_MIN_WIDTH = 280
    }
}
