package com.github.gfx.helium;

import com.github.gfx.helium.util.UserAgent;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class UserAgentTest {
    @Test
    public void testUserAgent() {
        System.out.println("XXX " + UserAgent.build());
        assertThat(UserAgent.build(), startsWith(BuildConfig.APPLICATION_ID));
    }
}
