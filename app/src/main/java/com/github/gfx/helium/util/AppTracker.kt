package com.github.gfx.helium.util

import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

import com.github.gfx.helium.BuildConfig

import android.util.Log

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject

@ParametersAreNonnullByDefault
class AppTracker
@Inject
constructor(val tracker: Tracker) {

    fun sendScreenView(screenName: String) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", screenName)
        }
        tracker.setScreenName(screenName)
        tracker.send(HitBuilders.ScreenViewBuilder().build())
    }

    fun sendEvent(category: String, action: String) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", category + "/" + action)
        }
        tracker.send(HitBuilders.EventBuilder(category, action).build())
    }

    fun sendTiming(category: String, variable: String, timing: Long) {
        if (BuildConfig.DEBUG) {
            Log.d("TrackingUtils", category + "; timing=" + timing)
        }
        tracker.send(HitBuilders.TimingBuilder(category, variable, timing).build())
    }
}
