package com.github.gfx.helium

import com.github.gfx.helium.api.EpitomeClient
import com.github.gfx.helium.api.HatenaClient
import com.github.gfx.helium.api.HeliumRequestInterceptor
import com.github.gfx.helium.model.EpitomeEntry
import com.github.gfx.helium.model.HatebuEntry

import org.junit.Test
import org.junit.runner.RunWith

import android.content.Context
import android.net.ConnectivityManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import java.io.IOException

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

import com.github.gfx.helium.TestUtils.getAssetFileInBytes
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*

@RunWith(AndroidJUnit4::class)
class ApiTest {

    internal val context: Context
        get() = InstrumentationRegistry.getTargetContext()

    // FIXME: use dependency injection
    internal fun createClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

    @Test
    @Throws(Exception::class)
    fun testRequestHotentries() {
        val feedClient = HatenaClient(
                createClientBuilder().addInterceptor(MockInterceptor("/hatena/b/hotentry", "hotentries.rss", "application/xml")).build())

        val entry = feedClient.hotentries.toBlocking().single()
        assertThat(entry, hasSize<Any>(greaterThan(0)))
    }

    @Test
    @Throws(Exception::class)
    fun testRequestHotentriesWithCategory() {
        val feedClient = HatenaClient(
                createClientBuilder().addInterceptor(MockInterceptor("/hotentry/it.rss", "hotentries.rss", "application/xml")).build())

        val entry = feedClient.getHotentries("it").toBlocking().single()
        assertThat(entry, hasSize<Any>(greaterThan(0)))
    }

    @Test
    @Throws(Exception::class)
    fun testRequestFavorites() {
        val feedClient = HatenaClient(
                createClientBuilder().addInterceptor(MockInterceptor("/gfx/favorite.rss", "favorites.rss", "application/xml")).build())

        val entry = feedClient.getFavotites("gfx", 1).toBlocking().single()
        assertThat(entry, hasSize<Any>(greaterThan(0)))
    }

    @Test
    @Throws(Exception::class)
    fun testRequestEpitome() {
        val feedClient = EpitomeClient(
                createClientBuilder().addInterceptor(MockInterceptor("/feed/beam", "epitome.json", "application/json")).build())

        val entry = feedClient.entries.toBlocking().single()
        assertThat(entry, hasSize<Any>(greaterThan(0)))
    }

    internal inner class MockRequestInterceptor : HeliumRequestInterceptor(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {

        protected override val isConnected: Boolean
            get() = true
    }

    internal inner class MockInterceptor(val path: String, val assetName: String, val contentType: String) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val url = chain.request().url()
            if (url.encodedPath() == path) {
                return resourceFoundInXml(chain.request())
            } else {
                return resourceNotFound(chain.request())
            }
        }

        fun resourceNotFound(request: Request): Response {
            return Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(404).message("not found").body(ResponseBody.create(MediaType.parse(contentType), byteArrayOf())).build()
        }

        @Throws(IOException::class)
        fun resourceFoundInXml(request: Request): Response {
            return Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(200).message("ok").body(ResponseBody.create(MediaType.parse(contentType), getAssetFileInBytes(assetName))).build()
        }
    }

}
