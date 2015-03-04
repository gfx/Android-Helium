package com.github.gfx.helium;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.github.gfx.helium.api.HttpClientHolder;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;

public class HeliumApplication extends Application {
    Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

        tracker = GoogleAnalytics.getInstance(this).newTracker(BuildConfig.GA_TRACKING_ID);

        if (BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this)
                    .getLogger()
                    .setLogLevel(Logger.LogLevel.VERBOSE);

            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
            HttpClientHolder.CLIENT.networkInterceptors()
                    .add(new StethoInterceptor());
        }
    }

    public Tracker getTracker() {
        return tracker;
    }
}
