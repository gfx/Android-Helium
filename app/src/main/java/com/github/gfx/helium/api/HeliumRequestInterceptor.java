package com.github.gfx.helium.api;

import com.github.gfx.helium.BuildConfig;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.RequestInterceptor;

@ParametersAreNonnullByDefault
public class HeliumRequestInterceptor implements RequestInterceptor {

    final ConnectivityManager connectivityManager;

    final String userAgent;

    public HeliumRequestInterceptor(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        userAgent = BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME
                + " ("
                + Build.DEVICE + "; "
                +  "Android/" + Build.VERSION.RELEASE
                + ")";
    }

    @Override
    public void intercept(RequestFacade request) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            int maxAge = 2 * 60;
            request.addHeader("cache-control", "public, max-age=" + maxAge);
        } else {
            int maxStale = 30 * 24 * 60 * 60; // 30 days
            request.addHeader("cache-control", "public, only-if-cached, max-stale=" + maxStale);
        }

        request.addHeader("user-agent", userAgent);
    }
}
