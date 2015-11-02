package com.github.gfx.helium.di;

import com.github.gfx.helium.di.scope.FragmentScope;
import com.github.gfx.helium.fragment.EpitomeEntryFragment;
import com.github.gfx.helium.fragment.HatebuEntryFragment;
import com.github.gfx.helium.fragment.SettingsFragment;
import com.github.gfx.helium.fragment.TimelineFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {

    void inject(HatebuEntryFragment fragment);

    void inject(EpitomeEntryFragment fragment);

    void inject(TimelineFragment fragment);

    void inject(SettingsFragment fragment);

}
