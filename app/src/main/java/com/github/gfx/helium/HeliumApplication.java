package com.github.gfx.helium;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.github.gfx.helium.api.HttpClientHolder;
import com.squareup.okhttp.Cache;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;

import java.io.File;

public class HeliumApplication extends Application {
    static final String CACHE_FILE_NAME = "okhttp.cache";
    static final long MAX_CACHE_SIZE = 4 * 1024 * 1024;

    Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

        tracker = GoogleAnalytics.getInstance(this).newTracker(BuildConfig.GA_TRACKING_ID);

        setupOkHttp();

        if (BuildConfig.DEBUG) {
            setupDebugFeatures();
        }
    }

    private void setupOkHttp() {
        File cacheDir = new File(getCacheDir(), CACHE_FILE_NAME);
        Cache cache = new Cache(cacheDir, MAX_CACHE_SIZE);
        HttpClientHolder.CLIENT.setCache(cache);
    }

    private void setupDebugFeatures() {
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

    public Tracker getTracker() {
        return tracker;
    }
}
