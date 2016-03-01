package com.github.gfx.helium.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject

@ParametersAreNonnullByDefault
class ViewSwitcher
@Inject
constructor(context: Context) {

    internal val animationTime: Int

    init {
        animationTime = context.resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    fun switchViewsWithAnimation(toShow: View, toHide: View, onComplate: Runnable) {
        toHide.visibility = View.VISIBLE
        toHide.animate().setDuration(animationTime.toLong()).alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                toHide.visibility = View.GONE
                onComplate.run()
            }
        })

        toShow.visibility = View.VISIBLE
        toShow.animate().setDuration(animationTime.toLong()).alpha(1f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplate.run()
            }
        })
    }

    fun setTextOrGone(view: TextView, text: CharSequence?) {
        if (TextUtils.isEmpty(text)) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = text
        }
    }
}
