package com.github.gfx.helium.api;

import com.squareup.okhttp.OkHttpClient;

import android.support.annotation.NonNull;

public class HttpClientHolder {
    @NonNull public static OkHttpClient CLIENT = new OkHttpClient();
}
