package com.github.gfx.helium.activity;

import com.github.gfx.helium.R;
import com.github.gfx.helium.analytics.TrackingUtils;
import com.github.gfx.helium.fragment.EpitomeEntryFragment;
import com.github.gfx.helium.fragment.HatebuEntryFragment;
import com.github.gfx.helium.model.EntryTab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.github.gfx.helium.Constants.SITE_APP;
import static com.github.gfx.helium.Constants.SITE_EPITOME;
import static com.github.gfx.helium.Constants.SITE_HATEBU;
import static com.github.gfx.helium.api.HatebuFeedClient.*;

public class MainActivity extends ActionBarActivity {
    static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        // TODO: make tabs customizable
        List<EntryTab> tabs = Arrays.asList(
                new EntryTab("Epitome", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return EpitomeEntryFragment.newInstance();
                    }
                }),

                new EntryTab("gfx", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_FAVORITE, "gfx");
                    }
                }),
                new EntryTab("総合", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_HOT, "all");
                    }
                }),
                new EntryTab("テクノロジー", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "it");
                    }
                }),
                new EntryTab("一般", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "general");
                    }
                }),
                new EntryTab("世の中", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "social");
                    }
                }),
                new EntryTab("政治と経済", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "economics");
                    }
                }),
                new EntryTab("暮らし", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "life");
                    }
                }),
                new EntryTab("学び", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "knowledge");
                    }
                }),
                new EntryTab("おもしろ", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "fun");
                    }
                }),
                new EntryTab("エンタメ", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "entertainment");
                    }
                }),
                new EntryTab("アニメとゲーム", new EntryTab.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return HatebuEntryFragment.newInstance(TYPE_CATEGORY, "game");
                    }
                })
                );

        viewPager.setAdapter(new MainTabsAdapter(getSupportFragmentManager(), tabs));
        viewPager.setCurrentItem(1); // hatebu/hotentry

        TrackingUtils.sendScreenView(this, TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view_hatebu) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_HATEBU));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_view_epitome) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_EPITOME));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_view_about) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_APP));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MainTabsAdapter extends FragmentStatePagerAdapter {
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
