package com.github.gfx.helium;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import android.content.Context;

import javax.inject.Inject;

public class StethoDelegator {

    @Inject
    Context context;

    @Inject
    OkHttpClient httpClient;

    public StethoDelegator() {
        HeliumApplication.getAppComponent().inject(this);
    }

    public void setup() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());

        httpClient.networkInterceptors()
                .add(new StethoInterceptor());

    }
}
