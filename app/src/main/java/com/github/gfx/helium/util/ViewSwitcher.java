package com.github.gfx.helium.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

@ParametersAreNonnullByDefault
public class ViewSwitcher {

    final int animationTime;

    @Inject
    public ViewSwitcher(Context context) {
        animationTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public void switchViewsWithAnimation(View toShow, final View toHide, final @NonNull Runnable onComplate) {
        toHide.setVisibility(View.VISIBLE);
        toHide.animate()
                .setDuration(animationTime)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide.setVisibility(View.GONE);
                        onComplate.run();
                    }
                });

        toShow.setVisibility(View.VISIBLE);
        toShow.animate()
                .setDuration(animationTime)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onComplate.run();
                    }
                });
    }

    public void setTextOrGone(TextView view, @Nullable CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        }
    }
}
