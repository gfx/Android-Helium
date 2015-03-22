package com.github.gfx.helium;

import com.github.gfx.helium.model.AppComponent;
import com.github.gfx.helium.model.AppModule;
import com.github.gfx.helium.model.Dagger_AppComponent;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;
import android.support.annotation.NonNull;

public class HeliumApplication extends Application {
    static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = Dagger_AppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        new StethoDelegator().setup();

        JodaTimeAndroid.init(this);
    }

    @NonNull
    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
