package com.github.gfx.helium.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    ActivityComponent plus(ActivityModule module);
}
