package com.github.gfx.helium;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.github.gfx.helium.model.AppComponent;
import com.github.gfx.helium.model.AppModule;
import com.github.gfx.helium.model.Dagger_AppComponent;
import com.squareup.okhttp.OkHttpClient;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import dagger.Lazy;

public class HeliumApplication extends Application {
    static AppComponent appComponent;

    @Inject
    Lazy<OkHttpClient> httpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = Dagger_AppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);

        JodaTimeAndroid.init(this);

        if (BuildConfig.DEBUG) {
            setupDebugFeatures();
        }
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

        httpClient.get()
                .networkInterceptors()
                .add(new StethoInterceptor());
    }

    @NonNull
    public static AppComponent getAppComponent() {
        assert appComponent != null;
        return appComponent;
    }
}
