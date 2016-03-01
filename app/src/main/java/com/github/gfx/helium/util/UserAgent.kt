package com.github.gfx.helium.util

import android.os.Build
import com.github.gfx.helium.BuildConfig

object UserAgent {

    fun build(): String {
        return BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME +
                "; Android/" + Build.VERSION.RELEASE + "; " +
                Build.DEVICE + "; " +
                Build.BRAND + "; " +
                Build.MODEL
    }
}
