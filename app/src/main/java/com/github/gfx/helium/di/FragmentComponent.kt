package com.github.gfx.helium.di

import com.github.gfx.helium.di.scope.FragmentScope
import com.github.gfx.helium.fragment.EpitomeEntryFragment
import com.github.gfx.helium.fragment.HatebuEntryFragment
import com.github.gfx.helium.fragment.SettingsFragment
import com.github.gfx.helium.fragment.TimelineFragment

import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(fragment: HatebuEntryFragment)

    fun inject(fragment: EpitomeEntryFragment)

    fun inject(fragment: TimelineFragment)

    fun inject(fragment: SettingsFragment)

}
