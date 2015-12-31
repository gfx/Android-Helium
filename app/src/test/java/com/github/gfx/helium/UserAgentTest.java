package com.github.gfx.helium;

import com.github.gfx.helium.util.UserAgent;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class UserAgentTest {

    @Test
    public void testUserAgent() {
        assertThat(UserAgent.build(), startsWith(BuildConfig.APPLICATION_ID));
    }
}
