package com.github.gfx.helium;

import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.model.HatebuFeed;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

import static com.github.gfx.helium.TestUtils.getAssetFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class HatebuFeedTest {
    @Test
    public void parseHatebuHotentryFeed() throws Exception {
        Serializer serializer = new Persister();
        File rss = getAssetFile("hotentries.rss");

        HatebuFeed feed = serializer.read(HatebuFeed.class, rss);

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
    public void parseHatebuFavoriteFee() throws Exception {
        Serializer serializer = new Persister();
        File rss = getAssetFile("favorites.rss");

        HatebuFeed feed = serializer.read(HatebuFeed.class, rss);

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
