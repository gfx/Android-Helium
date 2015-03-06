package com.github.gfx.helium.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.RequestInterceptor;

@ParametersAreNonnullByDefault
public class OfflineRequestInterceptor implements RequestInterceptor {

    final ConnectivityManager connectivityManager;

    public OfflineRequestInterceptor(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void intercept(RequestFacade request) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            int maxAge = 60;
            request.addHeader("cache-control", "public, max-age=" + maxAge);
        } else {
            int maxStale = 30 * 24 * 60 * 60; // 30 days
            request.addHeader("cache-control", "public, only-if-cached, max-stale=" + maxStale);
        }
    }
}
