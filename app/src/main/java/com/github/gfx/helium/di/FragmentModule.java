package com.github.gfx.helium.di;

import com.github.gfx.helium.di.scope.FragmentScope;

import android.content.Context;
import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    final Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @FragmentScope
    @Provides
    public Context context() {
        return fragment.getContext();
    }

}
