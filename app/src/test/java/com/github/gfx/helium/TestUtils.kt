package com.github.gfx.helium

import com.google.common.io.ByteStreams

import android.support.test.InstrumentationRegistry

import java.io.IOException
import java.io.InputStream

object TestUtils {

    @Throws(IOException::class)
    fun openAsset(name: String): InputStream {
        return InstrumentationRegistry.getContext().assets.open(name)
    }

    @Throws(IOException::class)
    fun getAssetFileInBytes(name: String): ByteArray {
        return ByteStreams.toByteArray(openAsset(name))
    }
}
