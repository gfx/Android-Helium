package com.github.gfx.helium

import com.github.gfx.helium.util.UserAgent

import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.runner.AndroidJUnit4

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*

@RunWith(AndroidJUnit4::class)
class UserAgentTest {

    @Test
    fun testUserAgent() {
        assertThat(UserAgent.build(), startsWith(BuildConfig.APPLICATION_ID))
    }
}
