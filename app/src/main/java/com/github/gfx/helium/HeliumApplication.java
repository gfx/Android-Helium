package com.github.gfx.helium;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.github.gfx.helium.api.ApiClientComponent;
import com.github.gfx.helium.api.ApiClientModule;
import com.github.gfx.helium.api.Dagger_ApiClientComponent;
import com.squareup.okhttp.OkHttpClient;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Inject;

public class HeliumApplication extends Application {
    Tracker tracker;

    static ApiClientComponent apiClientComponent;

    @Inject
    OkHttpClient httpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        apiClientComponent = Dagger_ApiClientComponent.builder()
                .apiClientModule(new ApiClientModule(this))
                .build();

        JodaTimeAndroid.init(this);

        if (BuildConfig.DEBUG) {
            setupDebugFeatures();
        }
    }

    private void setupDebugFeatures() {
        apiClientComponent.inject(this); // for httpClient

        GoogleAnalytics.getInstance(this)
                .getLogger()
                .setLogLevel(Logger.LogLevel.VERBOSE);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        httpClient.networkInterceptors()
                .add(new StethoInterceptor());
    }

    @NonNull
    public static ApiClientComponent getApiClientComponent() {
        assert apiClientComponent != null;
        return apiClientComponent;
    }
}
