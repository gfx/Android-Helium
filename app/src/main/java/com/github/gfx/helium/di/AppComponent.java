package com.github.gfx.helium.di;

import com.github.gfx.helium.util.OkHttp3GlideModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(OkHttp3GlideModule okHttp3GlideModule);

    ActivityComponent plus(ActivityModule module);
}
