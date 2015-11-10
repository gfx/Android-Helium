package com.github.gfx.helium.util;

import android.view.View;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

@ParametersAreNonnullByDefault
public class LoadingAnimation {

    static long DURATION = 1000;

    @Inject
    public LoadingAnimation() {
    }

    public void start(final View view) {
        view.animate()
                .alpha(0.3f)
                .setDuration(DURATION)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .alpha(0.9f)
                                .setDuration(DURATION)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        start(view);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    public void cancel(final View view) {
        view.clearAnimation();
    }
}
