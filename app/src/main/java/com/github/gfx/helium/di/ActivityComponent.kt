package com.github.gfx.helium.di

import com.github.gfx.helium.activity.MainActivity
import com.github.gfx.helium.activity.SettingsActivity
import com.github.gfx.helium.di.scope.ActivityScope

import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: SettingsActivity)

    operator fun plus(module: FragmentModule): FragmentComponent
}
