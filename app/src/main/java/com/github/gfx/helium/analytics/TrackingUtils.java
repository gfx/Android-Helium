package com.github.gfx.helium.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.github.gfx.helium.HeliumApplication;

import android.app.Activity;
import android.support.annotation.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class TrackingUtils {

    public static void sendScreenView(@Nullable Activity activity, String screenName) {
        if (activity == null) {
            return;
        }
        Tracker tracker = ((HeliumApplication)activity.getApplication()).getTracker();

        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder()
                .build());

    }

    public static void sendEvent(@Nullable Activity activity, String category, String action) {
        if (activity == null) {
            return;
        }
        Tracker tracker = ((HeliumApplication)activity.getApplication()).getTracker();

        tracker.send(new HitBuilders.EventBuilder(category, action)
                .build());
    }
}
