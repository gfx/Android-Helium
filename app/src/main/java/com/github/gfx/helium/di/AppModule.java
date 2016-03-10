package com.github.gfx.helium.di;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.api.HeliumRequestInterceptor;
import com.github.gfx.helium.model.OrmaDatabase;
import com.github.gfx.helium.model.UsernameChangedEvent;
import com.github.gfx.static_gson.StaticGsonTypeAdapterFactory;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.subjects.PublishSubject;

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
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableExceptionReporting(true);
        return tracker;
    }

    @Provides
    public ConnectivityManager provideConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .create();
    }

    @Singleton
    @Provides
    public OkHttpClient provideHttpClient(Context context, Interceptor interceptor) {
        File cacheDir = new File(context.getCacheDir(), CACHE_FILE_NAME);
        Cache cache = new Cache(cacheDir, MAX_CACHE_SIZE);

        OkHttpClient.Builder c = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor);
        if (BuildConfig.DEBUG) {
            c.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
        }
        return c.build();
    }

    @Provides
    public Interceptor provideRequestInterceptor(ConnectivityManager connectivityManager) {
        return new HeliumRequestInterceptor(connectivityManager);
    }

    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    public AndroidCompositeSubscription provideAndroidCompositeSubscription() {
        return new AndroidCompositeSubscription();
    }

    @Singleton
    @Provides
    public PublishSubject<UsernameChangedEvent> provideUsernameChangedEventSubject() {
        return PublishSubject.create();
    }

    @Singleton
    @Provides
    public OrmaDatabase provideOrmaDatabase(Context context) {
        return OrmaDatabase.builder(context)
                .build();
    }
}
