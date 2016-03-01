package com.github.gfx.helium.di

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater

import dagger.Module
import dagger.Provides

@Module
class ActivityModule(internal val activity: AppCompatActivity) {

    @Provides
    fun activity(): Activity {
        return activity
    }

    @Provides
    fun context(): Context {
        return activity
    }

    @Provides
    internal fun layoutInflater(): LayoutInflater {
        return activity.layoutInflater
    }
}
