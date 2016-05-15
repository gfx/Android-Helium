package com.github.gfx.helium;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import android.app.Application;

import okhttp3.Interceptor;

public class StethoDelegator {

    public static void setup(Application context) {
        Stetho.initializeWithDefaults(context);
    }

    public static Interceptor createNetworkInterceptor() {
        return new StethoInterceptor();
    }
}
