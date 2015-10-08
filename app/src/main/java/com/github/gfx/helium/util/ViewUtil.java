package com.github.gfx.helium.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ViewUtil {

    public static void switchViewsWithAnimation(Context context, View toShow, final View toHide) {
        int animTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        toHide.setVisibility(View.VISIBLE);
        toHide.animate()
                .setDuration(animTime)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide.setVisibility(View.GONE);
                    }
                });

        toShow.setVisibility(View.VISIBLE);
        toShow.animate()
                .setDuration(animTime)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // nop
                    }
                });

    }
}
