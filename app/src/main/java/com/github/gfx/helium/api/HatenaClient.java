package com.github.gfx.helium.api;

import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.model.HatebuFeed;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
public class HatenaClient {

    public static final String FEEDBURNER_ENDPOINT = "http://feeds.feedburner.com/";

    public static final String HATEBU_ENDPOINT = "http://b.hatena.ne.jp/";

    public static final Uri HATEBU_ENTRY = Uri.parse("http://b.hatena.ne.jp/entry/");

    public static final String HATEBU_ICON = "http://cdn1.www.st-hatena.com/users/{user_prefix}/{user}/profile.gif";

    public static final String KEY_USERNAME = "hatena_username";

    private static final String TAG = HatenaClient.class.getSimpleName();

    final RestAdapter feedburnerAdapter;

    final FeedburnerService feedburnerService;

    final RestAdapter hatebuAdapter;

    final HatebuService hatebuService;

    public HatenaClient(Client client, RequestInterceptor requestInterceptor) {
        feedburnerAdapter = createCommonBuilder(client, requestInterceptor)
                .setEndpoint(FEEDBURNER_ENDPOINT)
                .build();
        feedburnerService = feedburnerAdapter.create(FeedburnerService.class);

        hatebuAdapter = createCommonBuilder(client, requestInterceptor)
                .setEndpoint(HATEBU_ENDPOINT)
                .build();
        hatebuService = hatebuAdapter.create(HatebuService.class);
    }

    static RestAdapter.Builder createCommonBuilder(Client client, RequestInterceptor requestIntercepter) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setConverter(new SimpleXMLConverter())
                .setRequestInterceptor(requestIntercepter)
                .setLogLevel(
                        BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE);
    }

    @NonNull
    public Uri buildHatebuEntryUri(String path) {
        return HATEBU_ENTRY.buildUpon()
                .appendPath(path)
                .build();
    }

    @NonNull
    public Uri buildHatebuIconUri(String username) {
        String uri = HATEBU_ICON
                .replace("{user_prefix}", username.substring(0, 2))
                .replace("{user}", username);
        return Uri.parse(uri);
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

    public Observable<List<HatebuEntry>> getFavotites(final String user) {
        return hatebuService.getFavorites(user).map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }


    interface FeedburnerService {

        @GET("/hatena/b/hotentry")
        Observable<HatebuFeed> getHotentries();
    }

    interface HatebuService {

        @GET("/hotentry/{category}.rss")
        Observable<HatebuFeed> getHotentries(@Path("category") String category);

        @GET("/{user}/favorite.rss")
        Observable<HatebuFeed> getFavorites(@Path("user") String user);
    }
}
