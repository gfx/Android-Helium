package com.github.gfx.helium.api;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.HeliumApplication;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.Context;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApiClientModule {
    static final String CACHE_FILE_NAME = "okhttp.cache";
    static final long MAX_CACHE_SIZE = 4 * 1024 * 1024;

    private HeliumApplication context;

    public ApiClientModule(HeliumApplication app) {
        context = app;
    }

    @Provides
    public Application provideApplicationContext() {
        return context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public Tracker providesGoogleAnalyticsTracker(Context context) {
        Tracker tracker = GoogleAnalytics.getInstance(context).newTracker(BuildConfig.GA_TRACKING_ID);
        tracker.enableExceptionReporting(true);
        return tracker;
    }

    @Singleton
    @Provides
    public OkHttpClient provideHttpClient(Context context) {
        File cacheDir = new File(context.getCacheDir(), CACHE_FILE_NAME);
        Cache cache = new Cache(cacheDir, MAX_CACHE_SIZE);

        OkHttpClient httpClient =  new OkHttpClient();
        httpClient.setCache(cache);
        return httpClient;
    }

    @Singleton
    @Provides
    public HatebuFeedClient privideHatebuFeedClient(Context context, OkHttpClient httpClient) {
        return new HatebuFeedClient(context, httpClient);
    }

    @Singleton
    @Provides
    public EpitomeFeedClient privideEpitomeFeedClient(Context context, OkHttpClient httpClient) {
        return new EpitomeFeedClient(context, httpClient);
    }
}
