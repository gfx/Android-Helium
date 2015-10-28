package com.github.gfx.helium.di;

import com.github.gfx.helium.activity.MainActivity;
import com.github.gfx.helium.activity.SettingsActivity;
import com.github.gfx.helium.di.scope.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(SettingsActivity activity);

    FragmentComponent plus(FragmentModule module);
}
