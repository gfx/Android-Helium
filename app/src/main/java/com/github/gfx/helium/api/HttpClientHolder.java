package com.github.gfx.helium.api;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;

public class HttpClientHolder {
    @NonNull public static OkHttpClient CLIENT = new OkHttpClient();
}
