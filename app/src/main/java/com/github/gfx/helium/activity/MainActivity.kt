package com.github.gfx.helium.activity

import android.app.Activity
import com.github.gfx.helium.HeliumApplication
import com.github.gfx.helium.R
import com.github.gfx.helium.api.HatenaClient
import com.github.gfx.helium.databinding.ActivityMainBinding
import com.github.gfx.helium.fragment.EpitomeEntryFragment
import com.github.gfx.helium.fragment.HatebuEntryFragment
import com.github.gfx.helium.fragment.TimelineFragment
import com.github.gfx.helium.model.EntryTab
import com.github.gfx.helium.util.AppTracker

import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import java.util.ArrayList
import java.util.Arrays

import javax.inject.Inject

import com.github.gfx.helium.Constants.SITE_APP
import com.github.gfx.helium.Constants.SITE_EPITOME
import com.github.gfx.helium.Constants.SITE_HATEBU

class MainActivity : AppCompatActivity() {

    @Inject
    private lateinit var tracker: AppTracker

    @Inject
    private lateinit var prefs: SharedPreferences

    private var hatenaUsername: String? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val t0 = System.currentTimeMillis()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        HeliumApplication.getComponent(this).inject(this)

        setSupportActionBar(binding.toolbar)

        hatenaUsername = prefs.getString(HatenaClient.KEY_USERNAME, null)

        binding.viewPager.adapter = MainTabsAdapter(supportFragmentManager, buildTabs())

        tracker.sendTiming(TAG, "onCreate", System.currentTimeMillis() - t0)
    }

    internal fun buildTabs(): List<EntryTab> {
        // TODO: make tabs customizable
        val tabs = ArrayList<EntryTab>()

        tabs.add(EntryTab("Epitome",  { EpitomeEntryFragment.newInstance() }))

        if (hatenaUsername != null) {
            val name = hatenaUsername!!
            tabs.add(EntryTab(name + "のタイムライン", { TimelineFragment.newInstance(name) }))
        }

        tabs.addAll(Arrays.asList(
                EntryTab("総合", { HatebuEntryFragment.newInstance() }),
                EntryTab("テクノロジー", { HatebuEntryFragment.newInstance("it") }),
                EntryTab("一般",  { HatebuEntryFragment.newInstance("general") }),
                EntryTab("世の中", { HatebuEntryFragment.newInstance("social") }),
                EntryTab("政治と経済",  { HatebuEntryFragment.newInstance("economics") }),
                EntryTab("暮らし",  { HatebuEntryFragment.newInstance("life") }),
                EntryTab("学び",  { HatebuEntryFragment.newInstance("knowledge") }),
                EntryTab("おもしろ",  { HatebuEntryFragment.newInstance("fun") }),
                EntryTab("エンタメ", { HatebuEntryFragment.newInstance("entertainment") }),
                EntryTab("アニメとゲーム", { HatebuEntryFragment.newInstance("game") })))
        return tabs
    }

    override fun onResume() {
        super.onResume()
        binding.viewPager.currentItem = prefs.getInt(KEY_SELECTED_TAB, DEFAULT_SELECTED_TAB)
    }

    override fun onPause() {
        prefs.edit().putInt(KEY_SELECTED_TAB, binding.viewPager.currentItem).apply()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_configure_username -> {
                val intent = SettingsActivity.createIntent(this)
                startActivityForResult(intent, REQUEST_CONFIGURE)
                return true
            }
            R.id.action_view_hatebu -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SITE_HATEBU))
                startActivity(intent)
                return true

            }
            R.id.action_view_epitome -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SITE_EPITOME))
                startActivity(intent)
                return true

            }
            R.id.action_view_about -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SITE_APP))
                startActivity(intent)
                return true

            }
            R.id.action_view_setting -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + packageName))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return true

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d(TAG, "onActivityResult with $requestCode $resultCode")

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CONFIGURE -> recreate()
            else -> Log.w(TAG, "unknown result code: " + resultCode)
        }
    }

    internal class MainTabsAdapter(fm: FragmentManager, val tabs: List<EntryTab>) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return tabs.size
        }

        override fun getItem(position: Int): Fragment {
            return tabs[position].createFragment()
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabs[position].title
        }

        override fun isViewFromObject(view: View?, `object`: Any): Boolean {
            val fragment = `object` as Fragment
            return view === fragment.view
        }
    }

    companion object {

        internal val TAG = MainActivity::class.java.simpleName

        internal val REQUEST_CONFIGURE = 1

        internal var KEY_SELECTED_TAB = "activity_main_selected_tab"

        internal var DEFAULT_SELECTED_TAB = 1
    }
}
