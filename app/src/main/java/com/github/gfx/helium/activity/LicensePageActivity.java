package com.github.gfx.helium.activity;

import com.github.gfx.helium.R;
import com.github.gfx.helium.databinding.ActivityLicensePageBinding;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LicensePageActivity extends AppCompatActivity {

    private static final String LICENSES_HTML_PATH = "file:///android_asset/licenses.html";

    public static Intent createIntent(Context context) {
        return new Intent(context, LicensePageActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLicensePageBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_license_page);
        setSupportActionBar(binding.toolbar);

        binding.webView.loadUrl(LICENSES_HTML_PATH);
    }
}
