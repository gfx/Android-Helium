package com.github.gfx.helium;

import com.github.gfx.helium.api.EpitomeFeedClient;
import com.github.gfx.helium.api.HatebuFeedClient;
import com.github.gfx.helium.api.HeliumRequestInterceptor;
import com.github.gfx.helium.model.EpitomeEntry;
import com.github.gfx.helium.model.HatebuEntry;

import org.junit.Test;

import android.test.mock.MockContext;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.github.gfx.helium.TestUtils.getAssetFileInBytes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class ApiTest {

    class MockContextWithSystemService extends MockContext {

        @Override
        public Object getSystemService(String name) {
            return null;
        }
    }

    class MockRequestInterceptor extends HeliumRequestInterceptor {

        public MockRequestInterceptor() {
            super(new MockContextWithSystemService());
        }

        @Override
        protected boolean isConnected() {
            return true;
        }
    }

    class MockClient implements Client {

        final String path;

        final String assetName;

        final String contentType;

        MockClient(String path, String assetName, String contentType) {
            this.path = path;
            this.assetName = assetName;
            this.contentType = contentType;
        }

        @Override
        public Response execute(Request request) throws IOException {
            URI uri = URI.create(request.getUrl());
            if (uri.getPath().equals(path)) {
                return resourceFoundInXml(request.getUrl());
            } else {
                return resourceNotFound(request.getUrl());
            }
        }

        Response resourceNotFound(String uri) {
            return new Response(uri, 404, "not found", new ArrayList<Header>(),
                    new TypedByteArray(contentType, new byte[]{}));
        }

        Response resourceFoundInXml(String uri) throws IOException {
            return new Response(uri, 200, "ok", new ArrayList<Header>(),
                    new TypedByteArray(contentType, getAssetFileInBytes(assetName)));

        }
    }

    @Test
    public void testRequestHotentries() throws Exception {
        HatebuFeedClient feedClient = new HatebuFeedClient(
                new MockClient("/hatena/b/hotentry", "hotentries.rss", "application/xml"),
                new MockRequestInterceptor());

        List<HatebuEntry> entry = feedClient.getHotentries().toBlocking().single();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    @Test
    public void testRequestHotentriesWithCategory() throws Exception {
        HatebuFeedClient feedClient = new HatebuFeedClient(
                new MockClient("/hotentry/it.rss", "hotentries.rss", "application/xml"),
                new MockRequestInterceptor());

        List<HatebuEntry> entry = feedClient.getHotentries("it").toBlocking().single();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    @Test
    public void testRequestFavorites() throws Exception {
        HatebuFeedClient feedClient = new HatebuFeedClient(
                new MockClient("/gfx/favorite.rss", "favorites.rss", "application/xml"),
                new MockRequestInterceptor());

        List<HatebuEntry> entry = feedClient.getFavotites("gfx").toBlocking().single();
        assertThat(entry, hasSize(greaterThan(0)));
    }

    @Test
    public void testRequestEpitome() throws Exception {
        EpitomeFeedClient feedClient = new EpitomeFeedClient(
                new MockClient("/feed/beam", "epitome.json", "application/json"),
                new MockRequestInterceptor());

        List<EpitomeEntry> entry = feedClient.getEntries().toBlocking().single();
        assertThat(entry, hasSize(greaterThan(0)));
    }

}
