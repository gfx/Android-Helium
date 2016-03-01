package com.github.gfx.helium

import com.github.gfx.helium.di.ActivityComponent
import com.github.gfx.helium.di.ActivityModule
import com.github.gfx.helium.di.AppComponent
import com.github.gfx.helium.di.AppModule
import com.github.gfx.helium.di.DaggerAppComponent
import com.github.gfx.helium.di.FragmentComponent
import com.github.gfx.helium.di.FragmentModule
import com.jakewharton.threetenabp.AndroidThreeTen

import android.app.Application
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

class HeliumApplication : Application() {

    lateinit var component: AppComponent
        internal set

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        StethoDelegator(this).setup()

        AndroidThreeTen.init(this)
    }

    companion object {

        fun getComponent(fragment: Fragment): FragmentComponent {
            assert(fragment.activity != null)
            val activity = fragment.activity as AppCompatActivity
            val application = fragment.context.applicationContext as HeliumApplication
            return application.component.plus(ActivityModule(activity)).plus(FragmentModule(fragment))
        }

        fun getComponent(activity: AppCompatActivity): ActivityComponent {
            val application = activity.applicationContext as HeliumApplication
            return application.component.plus(ActivityModule(activity))
        }
    }
}
