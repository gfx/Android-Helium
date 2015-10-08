package com.github.gfx.helium.activity;

import com.google.android.gms.analytics.Tracker;

import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.ActivitySettingsBinding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    final AndroidCompositeSubscription subscriptions = new AndroidCompositeSubscription();

    @Inject
    HatenaClient hatenaClient;

    @Inject
    Tracker tracker;

    @Inject
    SharedPreferences prefs;

    ActivitySettingsBinding binding;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        HeliumApplication.getAppComponent().inject(this);

        binding.buttonSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
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
        // Reset errors.
        binding.username.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.username.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            binding.username.setError(getString(R.string.error_field_required));
            focusView = binding.username;
            cancel = true;
        } else if (!isEmailValid(email)) {
            binding.username.setError(getString(R.string.error_invalid_username));
            focusView = binding.username;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            // TODO: validate username with Hatena API
            new UserLoginTask(email).execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        binding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.loginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        String username;

        UserLoginTask(String username) {
            this.username = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                prefs.edit()
                        .putString(HatenaClient.KEY_USERNAME, username)
                        .apply();

                setResult(RESULT_OK);
                finish();
            } else {
                binding.username.setError(getString(R.string.error_invalid_username));
                binding.username.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

