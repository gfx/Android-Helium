package com.github.gfx.helium.activity;

import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.ActivityMainBinding;
import com.github.gfx.helium.fragment.EpitomeEntryFragment;
import com.github.gfx.helium.fragment.HatebuEntryFragment;
import com.github.gfx.helium.fragment.TimelineFragment;
import com.github.gfx.helium.model.EntryTab;
import com.github.gfx.helium.util.AppTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.github.gfx.helium.Constants.SITE_APP;
import static com.github.gfx.helium.Constants.SITE_EPITOME;
import static com.github.gfx.helium.Constants.SITE_HATEBU;

public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();

    static final int REQUEST_CONFIGURE = 1;

    static String KEY_SELECTED_TAB = "activity_main_selected_tab";

    static int DEFAULT_SELECTED_TAB = 1;

    @Inject
    AppTracker tracker;

    @Inject
    SharedPreferences prefs;

    String hatenaUsername;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long t0 = System.currentTimeMillis();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        HeliumApplication.getAppComponent().inject(this);

        setSupportActionBar(binding.toolbar);

        hatenaUsername = prefs.getString(HatenaClient.KEY_USERNAME, null);

        binding.viewPager.setAdapter(new MainTabsAdapter(getSupportFragmentManager(), buildTabs()));

        tracker.sendTiming(TAG, "onCreate", System.currentTimeMillis() - t0);
    }

    List<EntryTab> buildTabs() {
        // TODO: make tabs customizable
        List<EntryTab> tabs = new ArrayList<>();

        tabs.add(new EntryTab("Epitome", new EntryTab.FragmentFactory() {
            @Override
            public Fragment createFragment() {
                return EpitomeEntryFragment.newInstance();
            }
        }));

        if (hatenaUsername != null) {
            tabs.add(new EntryTab(hatenaUsername + "のお気に入り", new EntryTab.FragmentFactory() {
                @Override
                public Fragment createFragment() {
                    return TimelineFragment.newInstance(hatenaUsername);
                }
            }));
        }

        tabs.addAll(Arrays.asList(
                new EntryTab("総合", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance();
                    }
                }),
                new EntryTab("テクノロジー", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("it");
                    }
                }),
                new EntryTab("一般", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("general");
                    }
                }),
                new EntryTab("世の中", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("social");
                    }
                }),
                new EntryTab("政治と経済", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("economics");
                    }
                }),
                new EntryTab("暮らし", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("life");
                    }
                }),
                new EntryTab("学び", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("knowledge");
                    }
                }),
                new EntryTab("おもしろ", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("fun");
                    }
                }),
                new EntryTab("エンタメ", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("entertainment");
                    }
                }),
                new EntryTab("アニメとゲーム", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance("game");
                    }
                })));
        return tabs;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.viewPager.setCurrentItem(prefs.getInt(KEY_SELECTED_TAB, DEFAULT_SELECTED_TAB));
    }

    @Override
    protected void onPause() {
        prefs.edit()
                .putInt(KEY_SELECTED_TAB, binding.viewPager.getCurrentItem())
                .apply();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_configure_username: {
                Intent intent = SettingsActivity.createIntent(this);
                startActivityForResult(intent, REQUEST_CONFIGURE);
                return true;
            }
            case R.id.action_view_hatebu: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_HATEBU));
                startActivity(intent);
                return true;

            }
            case R.id.action_view_epitome: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_EPITOME));
                startActivity(intent);
                return true;

            }
            case R.id.action_view_about: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_APP));
                startActivity(intent);
                return true;

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult with " + requestCode + " " + resultCode);

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CONFIGURE:
                recreate();
                break;
            default:
                Log.w(TAG, "unknown result code: " + resultCode);
        }
    }

    static class MainTabsAdapter extends FragmentStatePagerAdapter {

        final List<EntryTab> tabs;

        public MainTabsAdapter(FragmentManager fm, List<EntryTab> tabs) {
            super(fm);
            this.tabs = tabs;
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return tabs.get(position).createFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position).title;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            Fragment fragment = (Fragment) object;
            return view == fragment.getView();
        }
    }
}
