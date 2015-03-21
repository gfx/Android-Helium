package com.github.gfx.helium.util;

import com.github.gfx.helium.BuildConfig;

import android.os.Build;

public class UserAgent {
    public static String build() {
        return BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME
                +  " Android/" + Build.VERSION.RELEASE
                + " " + Build.DEVICE
                + " " + Build.BRAND
                + " " + Build.MODEL;

    }
}
