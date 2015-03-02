package com.github.gfx.hatebulet.activity;

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

import com.github.gfx.hatebulet.R;
import com.github.gfx.hatebulet.fragment.EpitomeEntryFragment;
import com.github.gfx.hatebulet.fragment.HatebuEntryFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.github.gfx.hatebulet.Constants.SITE_APP;
import static com.github.gfx.hatebulet.Constants.SITE_HATEBU;


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

        viewPager.setAdapter(new MainTabsAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1); // hatebu/hotentry
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
        } else if (id == R.id.action_view_about) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_APP));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MainTabsAdapter  extends FragmentStatePagerAdapter {
        public MainTabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new EpitomeEntryFragment();
                case 1:
                    return new HatebuEntryFragment();
            }
            throw new IllegalStateException("not reached");
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            Fragment fragment = (Fragment) object;
            return view == fragment.getView();
        }
    }
}
