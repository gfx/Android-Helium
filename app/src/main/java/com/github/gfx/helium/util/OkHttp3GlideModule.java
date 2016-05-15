package com.github.gfx.helium.util;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.github.gfx.helium.HeliumApplication;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

public class OkHttp3GlideModule implements GlideModule {

    @Inject
    OkHttpClient client;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        Log.d("OkHttp3GlideModule", "registerComponents");

        HeliumApplication.getComponent(context).inject(this);

        glide.register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(client));
    }
}
