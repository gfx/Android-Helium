package com.github.gfx.helium.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ViewSwitcher {

    final int animationTime;

    public ViewSwitcher(Context context) {
        animationTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public void switchViewsWithAnimation(View toShow, final View toHide) {
        toHide.setVisibility(View.VISIBLE);
        toHide.animate()
                .setDuration(animationTime)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide.setVisibility(View.GONE);
                    }
                });

        toShow.setVisibility(View.VISIBLE);
        toShow.animate()
                .setDuration(animationTime)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // nop
                    }
                });

    }
}
