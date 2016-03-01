package com.github.gfx.helium.fragment


import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription
import com.github.gfx.helium.HeliumApplication
import com.github.gfx.helium.R
import com.github.gfx.helium.api.HatenaClient
import com.github.gfx.helium.databinding.FragmentSettingsBinding
import com.github.gfx.helium.model.HatebuEntry
import com.github.gfx.helium.model.UsernameChangedEvent
import com.github.gfx.helium.util.AppTracker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import javax.inject.Inject

import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

class SettingsFragment : Fragment() {

    @Inject
    internal var subscriptions: AndroidCompositeSubscription

    @Inject
    internal var hatenaClient: HatenaClient

    @Inject
    internal var tracker: AppTracker

    @Inject
    internal var prefs: SharedPreferences

    @Inject
    internal var usernameChangedEventSubject: PublishSubject<UsernameChangedEvent>

    internal var binding: FragmentSettingsBinding

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        HeliumApplication.getComponent(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        HeliumApplication.getComponent(this).inject(this)

        binding.buttonSignIn.setOnClickListener { attemptLogin() }

        if (prefs.contains(HatenaClient.KEY_USERNAME)) {
            binding.username.setText(prefs.getString(HatenaClient.KEY_USERNAME, null))
        }

        return binding.root
    }

    override fun onPause() {
        subscriptions.unsubscribe()

        super.onPause()
    }

    internal fun attemptLogin() {
        binding.username.error = null

        val username = binding.username.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(username)) {
            binding.username.error = getString(R.string.error_field_required)
            focusView = binding.username
            cancel = true
        } else if (!isValidUsername(username)) {
            binding.username.error = getString(R.string.error_invalid_username)
            focusView = binding.username
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
            return
        }
        startProgress()

        checkHatenaId(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).lift(OperatorAddToCompositeSubscription<List<HatebuEntry>>(subscriptions)).subscribe(object : Subscriber<List<HatebuEntry>>() {
            override fun onCompleted() {
            }

            override fun onError(e: Throwable) {
                finishProgress()
                showError(e)
                tracker.sendEvent(TAG, "fail: set a hatena name")
            }

            override fun onNext(hatebuEntries: List<HatebuEntry>) {
                saveUsername(username)
                tracker.sendEvent(TAG, "success: set a hatena name")
            }
        })
    }

    internal fun isValidUsername(username: String): Boolean {
        return username.matches("^[a-zA-Z0-9_]+$".toRegex())
    }

    internal fun checkHatenaId(username: String): Observable<List<HatebuEntry>> {
        return hatenaClient.getBookmark(username, 0)
    }

    @MainThread
    internal fun startProgress() {
        binding.username.isEnabled = false
        binding.buttonSignIn.isEnabled = false
    }

    @MainThread
    internal fun finishProgress() {
        binding.username.isEnabled = true
        binding.buttonSignIn.isEnabled = true
    }

    internal fun saveUsername(username: String) {
        usernameChangedEventSubject.onNext(UsernameChangedEvent(username))
        prefs.edit().putString(HatenaClient.KEY_USERNAME, username).apply()

    }

    @MainThread
    internal fun showError(e: Throwable) {
        val httpException = e as HttpException
        if (httpException.code() == 404) {
            binding.username.error = getString(R.string.error_invalid_username)
            binding.username.requestFocus()

        } else {
            Log.w(TAG, "errors on " + Thread.currentThread(), e)
            binding.username.error = getString(R.string.error_network)
        }

    }

    companion object {

        internal val TAG = SettingsFragment::class.java!!.getSimpleName()
    }
}
