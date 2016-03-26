package com.github.gfx.helium;

import com.github.gfx.helium.api.EpitomeClient;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.api.HeliumRequestInterceptor;
import com.github.gfx.helium.di.AppModule;
import com.github.gfx.helium.model.EpitomeEntry;
import com.github.gfx.helium.model.HatebuEntry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.github.gfx.helium.TestUtils.getAssetFileInBytes;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class ApiTest {

    Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    AppModule appModle;

    // FIXME: use dependency injection
    OkHttpClient.Builder createClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Before
    public void setUp() throws Exception {
        appModle = new AppModule((Application) getContext().getApplicationContext());
    }

    @Test
    public void testRequestHotentries() throws Exception {
        HatenaClient feedClient = new HatenaClient(
                createClientBuilder()
                        .addInterceptor(new MockInterceptor("/hatena/b/hotentry", "hotentries.rss", "application/xml"))
                        .build());

        List<HatebuEntry> entry = feedClient.getHotentries().toBlocking().value();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    @Test
    public void testRequestHotentriesWithCategory() throws Exception {
        HatenaClient feedClient = new HatenaClient(
                createClientBuilder()
                        .addInterceptor(new MockInterceptor("/hotentry/it.rss", "hotentries.rss", "application/xml"))
                        .build());

        List<HatebuEntry> entry = feedClient.getHotentries("it").toBlocking().value();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    @Test
    public void testRequestFavorites() throws Exception {
        HatenaClient feedClient = new HatenaClient(
                createClientBuilder()
                        .addInterceptor(new MockInterceptor("/gfx/favorite.rss", "favorites.rss", "application/xml"))
                        .build());

        List<HatebuEntry> entry = feedClient.getFavotites("gfx", 1).toBlocking().value();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    @Test
    public void testRequestEpitome() throws Exception {
        EpitomeClient feedClient = new EpitomeClient(
                createClientBuilder()
                        .addInterceptor(new MockInterceptor("/feed/beam", "epitome.json", "application/json"))
                        .build(),
                appModle.provideGson()
        );

        List<EpitomeEntry> entry = feedClient.getEntries().toBlocking().value();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    class MockRequestInterceptor extends HeliumRequestInterceptor {

        public MockRequestInterceptor() {
            super((ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        }

        @Override
        protected boolean isConnected() {
            return true;
        }
    }

    class MockInterceptor implements Interceptor {

        final String path;

        final String assetName;

        final String contentType;

        MockInterceptor(String path, String assetName, String contentType) {
            this.path = path;
            this.assetName = assetName;
            this.contentType = contentType;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            HttpUrl url = chain.request().url();
            if (url.encodedPath().equals(path)) {
                return resourceFoundInXml(chain.request());
            } else {
                return resourceNotFound(chain.request());
            }
        }

        Response resourceNotFound(Request request) {
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(404)
                    .message("not found")
                    .body(ResponseBody.create(MediaType.parse(contentType), new byte[]{}))
                    .build();
        }

        Response resourceFoundInXml(Request request) throws IOException {
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("ok")
                    .body(ResponseBody.create(MediaType.parse(contentType), getAssetFileInBytes(assetName)))
                    .build();
        }
    }

}
