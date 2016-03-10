package com.github.gfx.helium.activity;

import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription;
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.databinding.ActivitySettingsBinding;
import com.github.gfx.helium.model.UsernameChangedEvent;
import com.github.gfx.helium.util.AppTracker;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class SettingsActivity extends AppCompatActivity {

    static final String TAG = SettingsActivity.class.getSimpleName();

    @Inject
    AppTracker tracker;

    @Inject
    PublishSubject<UsernameChangedEvent> usernameChangedEventSubject;

    @Inject
    AndroidCompositeSubscription subscriptions;

    ActivitySettingsBinding binding;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        HeliumApplication.getComponent(this).inject(this);

        setSupportActionBar(binding.toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        usernameChangedEventSubject
                .lift(new OperatorAddToCompositeSubscription<UsernameChangedEvent>(subscriptions))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UsernameChangedEvent>() {
                    @Override
                    public void call(UsernameChangedEvent usernameChangedEvent) {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    }

    @Override
    protected void onStop() {
        subscriptions.unsubscribe();

        super.onStop();
    }
}

