package com.github.gfx.helium.model;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.api.EpitomeFeedClient;
import com.github.gfx.helium.api.HatebuFeedClient;
import com.github.gfx.helium.api.HeliumRequestInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.client.Client;
import retrofit.client.OkClient;

@Module
public class AppModule {

    static final String CACHE_FILE_NAME = "okhttp.cache";

    static final long MAX_CACHE_SIZE = 4 * 1024 * 1024;

    static final String SHARED_PREF_NAME = "preferences";

    private Context context;

    public AppModule(Application app) {
        context = app;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public Tracker providesGoogleAnalyticsTracker(Context context) {
        GoogleAnalytics ga = GoogleAnalytics.getInstance(context);
        Tracker tracker = ga.newTracker(BuildConfig.GA_TRACKING_ID);
        tracker.enableExceptionReporting(true);
        return tracker;
    }

    @Provides
    public RequestInterceptor provideRequestInterceptor(Context context) {
        return new HeliumRequestInterceptor(context);
    }

    @Singleton
    @Provides
    public OkHttpClient provideHttpClient(Context context) {
        File cacheDir = new File(context.getCacheDir(), CACHE_FILE_NAME);
        Cache cache = new Cache(cacheDir, MAX_CACHE_SIZE);

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setCache(cache);
        return httpClient;
    }

    @Provides
    public Client provideRetrofitClient(OkHttpClient httpClient) {
        return new OkClient(httpClient);
    }

    @Singleton
    @Provides
    public HatebuFeedClient provideHatebuFeedClient(Client client,
            RequestInterceptor requestInterceptor) {
        return new HatebuFeedClient(client, requestInterceptor);
    }

    @Singleton
    @Provides
    public EpitomeFeedClient provideEpitomeFeedClient(Client client,
            RequestInterceptor requestInterceptor) {
        return new EpitomeFeedClient(client, requestInterceptor);
    }

    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }
}
