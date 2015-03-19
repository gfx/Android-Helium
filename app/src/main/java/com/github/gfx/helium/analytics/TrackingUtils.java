package com.github.gfx.helium.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class TrackingUtils {
    public static void sendScreenView(Tracker tracker, String screenName) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder()
                .build());
    }

    public static void sendEvent(Tracker tracker, String category, String action) {
        tracker.send(new HitBuilders.EventBuilder(category, action)
                .build());
    }
}
