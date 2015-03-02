package com.github.gfx.hatebulet.model;

import android.support.v4.app.Fragment;

public class EntryTab {
    public static interface FragmentFactory {
        Fragment createFragment();
    }

    public final String title;
    public final FragmentFactory fragmentFactory;

    public EntryTab(String title, FragmentFactory fragmentFactory) {
        this.title = title;
        this.fragmentFactory = fragmentFactory;
    }

    public Fragment createFragment() {
        return fragmentFactory.createFragment();
    }
}
