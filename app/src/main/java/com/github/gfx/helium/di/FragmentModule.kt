package com.github.gfx.helium.di

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

import dagger.Module
import dagger.Provides

@Module
class FragmentModule(internal val fragment: Fragment) {

    @Provides
    fun context(): Context {
        return fragment.context
    }

    @Provides
    fun provideFragmentManager(): FragmentManager {
        return fragment.fragmentManager
    }
}
