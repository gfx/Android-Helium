package com.github.gfx.helium;

import com.google.common.io.ByteStreams;

import android.support.test.InstrumentationRegistry;

import java.io.IOException;
import java.io.InputStream;

public class TestUtils {

    public static InputStream openAsset(String name) throws IOException {
        return InstrumentationRegistry.getContext().getAssets().open(name);
    }

    public static byte[] getAssetFileInBytes(String name) throws IOException {
        return ByteStreams.toByteArray(openAsset(name));
    }
}
