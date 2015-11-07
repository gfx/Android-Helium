package com.github.gfx.helium.api;

import com.github.gfx.helium.util.UserAgent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;

@ParametersAreNonnullByDefault
@Singleton
public class HeliumRequestInterceptor implements RequestInterceptor {

    final ConnectivityManager connectivityManager;

    final String userAgent;

    @Inject
    public HeliumRequestInterceptor(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;

        userAgent = UserAgent.build();
    }

    @Override
    public void intercept(RequestFacade request) {
        if (isConnected()) {
            int maxAge = 2 * 60;
            request.addHeader("cache-control", "public, max-age=" + maxAge);
        } else {
            int maxStale = 30 * 24 * 60 * 60; // 30 days
            request.addHeader("cache-control", "public, only-if-cached, max-stale=" + maxStale);
        }

        request.addHeader("user-agent", userAgent);
    }

    protected boolean isConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
