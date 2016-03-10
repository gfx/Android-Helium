package com.github.gfx.helium;

import com.github.gfx.helium.di.ActivityComponent;
import com.github.gfx.helium.di.ActivityModule;
import com.github.gfx.helium.di.AppComponent;
import com.github.gfx.helium.di.AppModule;
import com.github.gfx.helium.di.DaggerAppComponent;
import com.github.gfx.helium.di.FragmentComponent;
import com.github.gfx.helium.di.FragmentModule;
import com.jakewharton.threetenabp.AndroidThreeTen;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import hugo.weaving.DebugLog;

public class HeliumApplication extends Application {

    AppComponent appComponent;

    @NonNull
    public AppComponent getComponent() {
        return appComponent;
    }

    @NonNull
    public static FragmentComponent getComponent(Fragment fragment) {
        assert fragment.getActivity() != null;
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        HeliumApplication application = (HeliumApplication) fragment.getContext().getApplicationContext();
        return application.appComponent
                .plus(new ActivityModule(activity))
                .plus(new FragmentModule(fragment));
    }

    @NonNull
    public static ActivityComponent getComponent(AppCompatActivity activity) {
        HeliumApplication application = (HeliumApplication) activity.getApplicationContext();
        return application.appComponent
                .plus(new ActivityModule(activity));
    }

    @DebugLog
    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        new StethoDelegator(this).setup();

        AndroidThreeTen.init(this);
    }
}
