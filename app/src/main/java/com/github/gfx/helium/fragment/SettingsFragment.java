package com.github.gfx.helium.fragment;


import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription;
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.FragmentSettingsBinding;
import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.model.UsernameChangedEvent;
import com.github.gfx.helium.util.AppTracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;

public class SettingsFragment extends Fragment {

    static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject
    AndroidCompositeSubscription subscriptions;

    @Inject
    HatenaClient hatenaClient;

    @Inject
    AppTracker tracker;

    @Inject
    SharedPreferences prefs;

    @Inject
    PublishSubject<UsernameChangedEvent> usernameChangedEventSubject;

    FragmentSettingsBinding binding;

    public SettingsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        HeliumApplication.getComponent(this).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        HeliumApplication.getComponent(this).inject(this);

        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        if (prefs.contains(HatenaClient.KEY_USERNAME)) {
            binding.username.setText(prefs.getString(HatenaClient.KEY_USERNAME, null));
        }

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        subscriptions.unsubscribe();

        super.onPause();
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
                .lift(new OperatorAddToCompositeSubscription<List<HatebuEntry>>(subscriptions))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<HatebuEntry>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(final Throwable e) {
                        finishProgress();
                        showError(e);
                        tracker.sendEvent(TAG, "fail: set a hatena name");
                    }

                    @Override
                    public void onNext(List<HatebuEntry> hatebuEntries) {
                        saveUsername(username);
                        tracker.sendEvent(TAG, "success: set a hatena name");
                    }
                });
    }

    boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    Observable<List<HatebuEntry>> checkHatenaId(String username) {
        return hatenaClient.getBookmark(username, 0);
    }

    @MainThread
    void startProgress() {
        binding.username.setEnabled(false);
        binding.buttonSignIn.setEnabled(false);
    }

    @MainThread
    void finishProgress() {
        binding.username.setEnabled(true);
        binding.buttonSignIn.setEnabled(true);
    }

    void saveUsername(String username) {
        usernameChangedEventSubject.onNext(new UsernameChangedEvent(username));
        prefs.edit()
                .putString(HatenaClient.KEY_USERNAME, username)
                .apply();

    }

    @MainThread
    void showError(Throwable e) {
        RetrofitError retrofitError = (RetrofitError) e;
        if (retrofitError.getResponse().getStatus() == 404) {
            binding.username.setError(getString(R.string.error_invalid_username));
            binding.username.requestFocus();

        } else {
            Log.w(TAG, "errors on " + Thread.currentThread(), e);
            binding.username.setError(getString(R.string.error_network));
        }

    }
}
