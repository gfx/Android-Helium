package com.github.gfx.helium;

import com.github.gfx.helium.util.UserAgent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
public class UserAgentTest {
    @Test
    public void testUserAgent() {
        assertThat(UserAgent.build(), startsWith(BuildConfig.APPLICATION_ID));
    }
}
