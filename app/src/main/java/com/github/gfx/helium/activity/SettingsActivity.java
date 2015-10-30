package com.github.gfx.helium.activity;

import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.ActivitySettingsBinding;
import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.util.AppTracker;
import com.github.gfx.helium.util.ViewSwitcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;

public class SettingsActivity extends AppCompatActivity {

    static final String TAG = SettingsActivity.class.getSimpleName();

    final AndroidCompositeSubscription subscriptions = new AndroidCompositeSubscription();

    @Inject
    HatenaClient hatenaClient;

    @Inject
    AppTracker tracker;

    @Inject
    SharedPreferences prefs;

    @Inject
    ViewSwitcher viewSwitcher;

    ActivitySettingsBinding binding;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        HeliumApplication.getComponent(this).inject(this);

        setSupportActionBar(binding.toolbar);

        binding.buttonSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        tracker.sendScreenView(TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.contains(HatenaClient.KEY_USERNAME)) {
            binding.username.setText(prefs.getString(HatenaClient.KEY_USERNAME, null));
        }
    }

    @Override
    protected void onStop() {
        subscriptions.unsubscribe();

        super.onStop();
    }

    void attemptLogin() {
        binding.username.setError(null);

        final String username = binding.username.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            binding.username.setError(getString(R.string.error_field_required));
            focusView = binding.username;
            cancel = true;
        } else if (!isValidUsername(username)) {
            binding.username.setError(getString(R.string.error_invalid_username));
            focusView = binding.username;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }
        startProgress();

        checkHatenaId(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<HatebuEntry>>() {
                    @Override
                    public void onCompleted() {
                        finishProgress();

                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(final Throwable e) {
                        finishProgress();

                        RetrofitError retrofitError = (RetrofitError) e;
                        if (retrofitError.getResponse().getStatus() == 404) {
                            binding.username.setError(getString(R.string.error_invalid_username));
                            binding.username.requestFocus();

                        } else {
                            Log.w(TAG, "errors on " + Thread.currentThread(), e);
                            binding.username.setError(getString(R.string.error_network));
                        }
                    }

                    @Override
                    public void onNext(List<HatebuEntry> hatebuEntries) {
                        prefs.edit()
                                .putString(HatenaClient.KEY_USERNAME, username)
                                .apply();
                    }
                });
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    private void startProgress() {
        binding.buttonSignIn.setEnabled(false);
    }

    private void finishProgress() {
        binding.buttonSignIn.setEnabled(true);
    }

    private Observable<List<HatebuEntry>> checkHatenaId(String username) {
        return hatenaClient.getBookmark(username, 0);
    }
}

