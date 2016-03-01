package com.github.gfx.helium.model

import android.support.v4.app.Fragment

class EntryTab(val title: String, val fragmentFactory: () -> Fragment ) {

    fun createFragment(): Fragment {
        return fragmentFactory()
    }
}
