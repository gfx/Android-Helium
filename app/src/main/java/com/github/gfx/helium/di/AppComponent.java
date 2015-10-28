package com.github.gfx.helium.di;

import com.github.gfx.helium.StethoDelegator;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(StethoDelegator stethoDelegator);

    ActivityComponent plus(ActivityModule module);
}
