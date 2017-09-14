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
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

import hugo.weaving.DebugLog;

public class HeliumApplication extends Application {

    AppComponent appComponent;

    @NonNull
    public AppComponent getComponent() {
        return appComponent;
    }

    @NonNull public static AppComponent getComponent(Context context) {
        return ((HeliumApplication)context.getApplicationContext()).appComponent;
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

        StethoDelegator.setup(this);

        AndroidThreeTen.init(this);

        updateLanguage(Locale.JAPANESE);
    }

    public void updateLanguage(Locale locale) {
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = new Configuration();
            configuration.setLocale(locale);
            getResources().updateConfiguration(configuration, null);
        }
    }
}
