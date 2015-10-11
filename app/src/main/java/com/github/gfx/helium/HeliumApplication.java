package com.github.gfx.helium;

import com.github.gfx.helium.model.AppComponent;
import com.github.gfx.helium.model.AppModule;
import com.github.gfx.helium.model.DaggerAppComponent;
import com.jakewharton.threetenabp.AndroidThreeTen;

import android.app.Application;
import android.support.annotation.NonNull;

public class HeliumApplication extends Application {

    static AppComponent appComponent;

    @NonNull
    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        new StethoDelegator().setup();

        AndroidThreeTen.init(this);
    }
}
