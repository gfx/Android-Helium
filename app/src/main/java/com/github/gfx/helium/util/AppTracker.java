package com.github.gfx.helium.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.github.gfx.helium.BuildConfig;

import android.util.Log;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class AppTracker {

    final Tracker tracker;

    public AppTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void sendScreenView(String screenName) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", screenName);
        }
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendEvent(String category, String action) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", category + "." + action);
        }
        tracker.send(new HitBuilders.EventBuilder(category, action).build());
    }

    public void sendTiming(String category, String variable, long timing) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", category + "; timing=" + timing);
        }
        tracker.send(new HitBuilders.TimingBuilder(category, variable, timing).build());
    }
}
