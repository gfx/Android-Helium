package com.github.gfx.helium.util

import android.view.View

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject

@ParametersAreNonnullByDefault
class LoadingAnimation
@Inject
constructor() {

    fun start(view: View) {
        view.animate().alpha(0.3f).setDuration(DURATION).withEndAction { view.animate().alpha(0.9f).setDuration(DURATION).withEndAction { start(view) }.start() }.start()
    }

    fun cancel(view: View) {
        view.clearAnimation()
    }

    companion object {

        internal var DURATION: Long = 1000
    }
}
