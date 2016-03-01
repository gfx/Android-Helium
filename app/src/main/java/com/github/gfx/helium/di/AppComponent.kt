package com.github.gfx.helium.di

import com.github.gfx.helium.StethoDelegator

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(stethoDelegator: StethoDelegator)

    operator fun plus(module: ActivityModule): ActivityComponent
}
