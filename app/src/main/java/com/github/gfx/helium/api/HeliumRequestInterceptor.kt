package com.github.gfx.helium.api

import com.github.gfx.helium.util.UserAgent

import android.net.ConnectivityManager
import android.net.NetworkInfo

import java.io.IOException

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject
import javax.inject.Singleton

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

@ParametersAreNonnullByDefault
@Singleton
open class HeliumRequestInterceptor
@Inject
constructor(internal val connectivityManager: ConnectivityManager) : Interceptor {

    internal val userAgent: String

    init {

        userAgent = UserAgent.build()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val r = chain.request().newBuilder()
        if (isConnected) {
            val maxAge = 2 * 60
            r.addHeader("cache-control", "public, max-age=" + maxAge)
        } else {
            val maxStale = 30 * 24 * 60 * 60 // 30 days
            r.addHeader("cache-control", "public, only-if-cached, max-stale=" + maxStale)
        }
        r.addHeader("user-agent", userAgent)

        return chain.proceed(r.build())
    }

    protected open val isConnected: Boolean
        get() {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
}
