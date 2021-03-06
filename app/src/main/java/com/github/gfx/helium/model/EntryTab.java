package com.github.gfx.helium.model;

import android.support.v4.app.Fragment;

public class EntryTab {

    public final String title;

    public final FragmentFactory fragmentFactory;

    public EntryTab(String title, FragmentFactory fragmentFactory) {
        this.title = title;
        this.fragmentFactory = fragmentFactory;
    }

    public Fragment createFragment() {
        return fragmentFactory.createFragment();
    }

    public interface FragmentFactory {

        Fragment createFragment();
    }
}
