package com.github.gfx.helium.api;

import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.model.HatebuFeed;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Single;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
@Singleton
public class HatenaClient {

    public static final String FEEDBURNER_ENDPOINT = "http://feeds.feedburner.com/";

    public static final String HATEBU_ENDPOINT = "http://b.hatena.ne.jp/";

    public static final Uri HATEBU_ENTRY = Uri.parse("http://b.hatena.ne.jp/entry/");

    public static final String HATEBU_ICON = "http://cdn1.www.st-hatena.com/users/{user_prefix}/{user}/profile.gif";

    public static final String KEY_USERNAME = "hatena_username";

    final FeedburnerService feedburnerService;

    final HatebuService hatebuService;

    @Inject
    public HatenaClient(OkHttpClient client) {
        Retrofit feedburnerRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(FEEDBURNER_ENDPOINT)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        feedburnerService = feedburnerRetrofit.create(FeedburnerService.class);

        Retrofit hatebuRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(HATEBU_ENDPOINT)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        hatebuService = hatebuRetrofit.create(HatebuService.class);
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

    public Single<List<HatebuEntry>> getHotentries() {
        return feedburnerService.getHotentries().map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }

    public Single<List<HatebuEntry>> getHotentries(final String category) {
        return hatebuService.getHotentries(category).map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }

    public Single<List<HatebuEntry>> getFavotites(final String user, final int of) {
        return hatebuService.getFavorites(user, of).map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }

    public Single<List<HatebuEntry>> getBookmark(final String user, final int of) {
        return hatebuService.getBookmark(user, of).map(new Func1<HatebuFeed, List<HatebuEntry>>() {
            @Override
            public List<HatebuEntry> call(HatebuFeed hatebuFeed) {
                return hatebuFeed.items;
            }
        });
    }

    interface FeedburnerService {

        @GET("/hatena/b/hotentry")
        Single<HatebuFeed> getHotentries();
    }

    interface HatebuService {

        @GET("/hotentry/{category}.rss")
        Single<HatebuFeed> getHotentries(@Path("category") String category);

        @GET("/{user}/favorite.rss")
        Single<HatebuFeed> getFavorites(@Path("user") String user, @Query("of") int of);

        @GET("/{user}/bookmark.rss")
        Single<HatebuFeed> getBookmark(@Path("user") String user, @Query("of") int of);
    }
}
