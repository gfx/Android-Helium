package com.github.gfx.helium.model;


import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class HatebuEntryTest {

    @Test
    public void testLooksLikeImageUrl() throws Exception {

        HatebuEntry entry = new HatebuEntry();

        entry.link = "http://example.com/foo.png";

        assertThat(entry.looksLikeImageUrl(), is(true));
    }
}
