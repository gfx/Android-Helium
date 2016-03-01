package com.github.gfx.helium.model


import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.runner.AndroidJUnit4

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*

@RunWith(AndroidJUnit4::class)
class HatebuEntryTest {

    @Test
    @Throws(Exception::class)
    fun testLooksLikeImageUrl() {

        val entry = HatebuEntry()

        entry.link = "http://example.com/foo.png"

        assertThat(entry.looksLikeImageUrl(), `is`(true))
    }
}
