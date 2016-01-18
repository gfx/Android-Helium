package com.github.gfx.helium;

import com.facebook.stetho.Stetho;

import android.content.Context;

import javax.inject.Inject;

public class StethoDelegator {

    @Inject
    Context context;

    public StethoDelegator(HeliumApplication application) {
        application.getComponent().inject(this);
    }

    public void setup() {
        Stetho.initializeWithDefaults(context);
    }
}
