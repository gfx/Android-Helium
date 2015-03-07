package com.github.gfx.helium;

import com.github.gfx.helium.model.HatebuFeed;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileNotFoundException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class HatebuFeedTest {
    File getAssetFile(String name) throws FileNotFoundException {
        String[] appDirs = {".", "app"};
        for (String appDir : appDirs) {
            File file = new File(appDir, "src/test/assets/" + name);
            if (file.exists()) {
                return file;
            }
        }
        throw new FileNotFoundException("No resource file: " + name);
    }

    @Test
    public void parseWithSimpleXmlFramework() throws Exception {
        Serializer serializer = new Persister();
        File rss = getAssetFile("hatebu.rss");

        HatebuFeed feed = serializer.read(HatebuFeed.class, rss);

        assertThat(feed.items.size(), is(30));

        assertThat(feed.items.get(0).title, is(not("")));
        assertThat(feed.items.get(0).title, is(notNullValue()));

        assertThat(feed.items.get(0).description, is(not("")));
        assertThat(feed.items.get(0).description, is(notNullValue()));
    }
}
