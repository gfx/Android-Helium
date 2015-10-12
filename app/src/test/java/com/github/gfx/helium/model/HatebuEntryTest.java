package com.github.gfx.helium.model;


import com.github.gfx.helium.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
public class HatebuEntryTest {

    @Test
    public void testLooksLikeImageUrl() throws Exception {

        HatebuEntry entry = new HatebuEntry();

        entry.link = "http://example.com/foo.png";

        assertThat(entry.looksLikeImageUrl(), is(true));
    }
}