package com.github.gfx.helium.api;

import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.model.HatebuFeed;
import com.squareup.okhttp.OkHttpClient;

import android.content.Context;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
public class HatebuFeedClient {

    private static final String TAG = HatebuFeedClient.class.getSimpleName();

    public static final String FEEDBURNER_ENDPOINT = "http://feeds.feedburner.com/";

    public static final String HATEBU_ENDPOINT = "http://b.hatena.ne.jp/";

    final RestAdapter feedburnerAdapter;

    final FeedburnerService feedburnerService;

    final RestAdapter hatebuAdapter;

    final HatebuService hatebuService;

    public HatebuFeedClient(Context context, OkHttpClient httpClient) {
        feedburnerAdapter = createCommonBuilder(context, httpClient)
                .setEndpoint(FEEDBURNER_ENDPOINT)
                .build();
        feedburnerService = feedburnerAdapter.create(FeedburnerService.class);

        hatebuAdapter = createCommonBuilder(context, httpClient)
                .setEndpoint(HATEBU_ENDPOINT)
                .build();
        hatebuService = hatebuAdapter.create(HatebuService.class);
    }

    static RestAdapter.Builder createCommonBuilder(Context context, OkHttpClient httpClient) {
        OkClient client = new OkClient(httpClient);

        return new RestAdapter.Builder()
                .setClient(client)
                .setConverter(new SimpleXMLConverter())
                .setRequestInterceptor(new OfflineRequestInterceptor(context))
                .setLogLevel(
                        BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE);
    }

    public Observable<List<HatebuEntry>> getHotentries() {
        return feedburnerService.getHotentries().map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }

    public Observable<List<HatebuEntry>> getHotentries(final String category) {
        return hatebuService.getHotentries(category).map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }


    static interface FeedburnerService {

        @GET("/hatena/b/hotentry")
        Observable<HatebuFeed> getHotentries();
    }

    static interface HatebuService {

        @GET("/hotentry/{category}.rss")
        Observable<HatebuFeed> getHotentries(@Path("category") String category);
    }
}
