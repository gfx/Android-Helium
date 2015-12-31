package com.github.gfx.helium;

import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.model.HatebuFeed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.support.test.runner.AndroidJUnit4;

import java.io.InputStream;

import static com.github.gfx.helium.TestUtils.openAsset;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class HatebuFeedTest {

    @Test
    public void parseHatebuHotentryFeed() throws Exception {
        Serializer serializer = new Persister();
        InputStream stream = openAsset("hotentries.rss");
        HatebuFeed feed = serializer.read(HatebuFeed.class, stream);
        stream.close();

        assertThat(feed.items.size(), is(30));

        HatebuEntry entry = feed.items.get(0);

        assertThat(entry.title, is(not("")));
        assertThat(entry.title, is(notNullValue()));

        assertThat(entry.description, is(not("")));
        assertThat(entry.description, is(notNullValue()));

        assertThat(entry.subject, hasSize(1));

        assertThat(entry.getTimestamp(), is(notNullValue()));
    }

    @Test
    public void parseHatebuFavoriteFeed() throws Exception {
        Serializer serializer = new Persister();
        InputStream stream = openAsset("favorites.rss");
        HatebuFeed feed = serializer.read(HatebuFeed.class, stream);
        stream.close();

        assertThat(feed.items.size(), is(25));

        HatebuEntry entry = feed.items.get(0);

        assertThat(entry.title, is(not("")));
        assertThat(entry.title, is(notNullValue()));

        assertThat(entry.description, is(not("")));
        assertThat(entry.description, is(notNullValue()));

        assertThat(entry.subject, hasSize(0));

        assertThat(entry.getTimestamp(), is(notNullValue()));

        assertThat(entry.creator, is(notNullValue()));
    }
}
