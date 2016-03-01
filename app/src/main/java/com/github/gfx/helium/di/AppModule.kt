package com.github.gfx.helium.di

import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker

import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription
import com.github.gfx.helium.BuildConfig
import com.github.gfx.helium.api.HeliumRequestInterceptor
import com.github.gfx.helium.model.OrmaDatabase
import com.github.gfx.helium.model.UsernameChangedEvent

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager

import java.io.File

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import rx.subjects.PublishSubject

@Module
class AppModule(app: Application) {

    private val context: Context

    init {
        context = app
    }

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun providesGoogleAnalyticsTracker(context: Context): Tracker {
        val ga = GoogleAnalytics.getInstance(context)
        val tracker = ga.newTracker(BuildConfig.GA_TRACKING_ID)
        tracker.enableAdvertisingIdCollection(true)
        tracker.enableExceptionReporting(true)
        return tracker
    }

    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Singleton
    @Provides
    fun provideHttpClient(context: Context, interceptor: Interceptor): OkHttpClient {
        val cacheDir = File(context.cacheDir, CACHE_FILE_NAME)
        val cache = Cache(cacheDir, MAX_CACHE_SIZE)

        val c = OkHttpClient.Builder().cache(cache).addInterceptor(interceptor)
        if (BuildConfig.DEBUG) {
            c.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }
        return c.build()
    }

    @Provides
    fun provideRequestInterceptor(connectivityManager: ConnectivityManager): Interceptor {
        return HeliumRequestInterceptor(connectivityManager)
    }

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideAndroidCompositeSubscription(): AndroidCompositeSubscription {
        return AndroidCompositeSubscription()
    }

    @Singleton
    @Provides
    fun provideUsernameChangedEventSubject(): PublishSubject<UsernameChangedEvent> {
        return PublishSubject.create<UsernameChangedEvent>()
    }

    @Singleton
    @Provides
    fun provideOrmaDatabase(context: Context): OrmaDatabase {
        return OrmaDatabase.builder(context).build()
    }

    companion object {

        internal val CACHE_FILE_NAME = "okhttp.cache"

        internal val MAX_CACHE_SIZE = 4 * 1024 * 1024.toLong()

        internal val SHARED_PREF_NAME = "preferences"
    }
}
