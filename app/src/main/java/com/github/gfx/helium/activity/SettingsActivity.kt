package com.github.gfx.helium.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription
import com.github.gfx.helium.HeliumApplication
import com.github.gfx.helium.R
import com.github.gfx.helium.databinding.ActivitySettingsBinding
import com.github.gfx.helium.model.UsernameChangedEvent
import com.github.gfx.helium.util.AppTracker
import rx.subjects.PublishSubject
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    @Inject
    private lateinit var tracker: AppTracker

    @Inject
    private lateinit var usernameChangedEventSubject: PublishSubject<UsernameChangedEvent>

    @Inject
    private lateinit var subscriptions: AndroidCompositeSubscription

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        HeliumApplication.getComponent(this).inject(this)

        setSupportActionBar(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()

        usernameChangedEventSubject
                .lift(OperatorAddToCompositeSubscription<UsernameChangedEvent>(subscriptions))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
    }

    override fun onStop() {
        subscriptions.unsubscribe()

        super.onStop()
    }

    companion object {

        internal val TAG = SettingsActivity::class.java.getSimpleName()

        fun createIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}

