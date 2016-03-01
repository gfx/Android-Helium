package com.github.gfx.helium

import com.github.gfx.helium.model.HatebuEntry
import com.github.gfx.helium.model.HatebuFeed

import org.junit.Test
import org.junit.runner.RunWith
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

import android.support.test.runner.AndroidJUnit4

import java.io.InputStream

import com.github.gfx.helium.TestUtils.openAsset
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*

@RunWith(AndroidJUnit4::class)
class HatebuFeedTest {

    @Test
    @Throws(Exception::class)
    fun parseHatebuHotentryFeed() {
        val serializer = Persister()
        val stream = openAsset("hotentries.rss")
        val feed = serializer.read<HatebuFeed>(HatebuFeed::class.java, stream)
        stream.close()

        assertThat(feed.items.size, `is`(30))

        val entry = feed.items[0]

        assertThat(entry.title, `is`(not("")))
        assertThat(entry.title, `is`(notNullValue()))

        assertThat(entry.description, `is`(not("")))
        assertThat(entry.description, `is`(notNullValue()))

        assertThat(entry.subject, hasSize<Any>(1))

        assertThat(entry.timestamp, `is`(notNullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun parseHatebuFavoriteFeed() {
        val serializer = Persister()
        val stream = openAsset("favorites.rss")
        val feed = serializer.read<HatebuFeed>(HatebuFeed::class.java, stream)
        stream.close()

        assertThat(feed.items.size, `is`(25))

        val entry = feed.items[0]

        assertThat(entry.title, `is`(not("")))
        assertThat(entry.title, `is`(notNullValue()))

        assertThat(entry.description, `is`(not("")))
        assertThat(entry.description, `is`(notNullValue()))

        assertThat(entry.subject, hasSize<Any>(0))

        assertThat(entry.timestamp, `is`(notNullValue()))

        assertThat(entry.creator, `is`(notNullValue()))
    }
}
