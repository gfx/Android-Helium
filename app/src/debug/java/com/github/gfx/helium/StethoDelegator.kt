package com.github.gfx.helium

import com.facebook.stetho.Stetho

import android.content.Context

import javax.inject.Inject

class StethoDelegator(application: HeliumApplication) {

    @Inject
    private lateinit var context: Context

    init {
        application.component.inject(this)
    }

    fun setup() {
        Stetho.initializeWithDefaults(context)
    }
}
