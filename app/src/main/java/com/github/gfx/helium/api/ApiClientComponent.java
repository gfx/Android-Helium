package com.github.gfx.helium.api;

import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.activity.MainActivity;
import com.github.gfx.helium.fragment.EpitomeEntryFragment;
import com.github.gfx.helium.fragment.HatebuEntryFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApiClientModule.class })
public interface ApiClientComponent {
    void inject(HeliumApplication app);
    void inject(HatebuEntryFragment fragment);
    void inject(EpitomeEntryFragment fragment);
    void inject(MainActivity activity);
}
