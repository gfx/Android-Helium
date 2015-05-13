package com.github.gfx.helium.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.github.gfx.helium.BuildConfig;

import android.util.Log;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class TrackingUtils {

    public static void sendScreenView(Tracker tracker, String screenName) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", screenName);
        }
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendEvent(Tracker tracker, String category, String action) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", category + "." + action);
        }
        tracker.send(new HitBuilders.EventBuilder(category, action).build());
    }

    public static void sendTiming(Tracker tracker, String category, String variable, long timing) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", category + "; timing=" + timing);
        }
        tracker.send(new HitBuilders.TimingBuilder(category, variable, timing).build());
    }
}
