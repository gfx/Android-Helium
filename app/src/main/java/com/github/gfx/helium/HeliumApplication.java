package com.github.gfx.helium;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.github.gfx.helium.api.HttpClientHolder;

public class HeliumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
            HttpClientHolder.CLIENT.networkInterceptors()
                    .add(new StethoInterceptor());
        }
    }
}
