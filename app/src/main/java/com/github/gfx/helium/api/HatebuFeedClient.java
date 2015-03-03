package com.github.gfx.helium.api;

import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.model.HatebuEntry;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;

@ParametersAreNonnullByDefault
public class HatebuFeedClient {
    private static final String TAG = HatebuFeedClient.class.getSimpleName();

    public static final String FEEDBURNER_ENDPOINT = "http://feeds.feedburner.com/";
    public static final String HATEBU_ENDPOINT = "http://b.hatena.ne.jp/";

    final RestAdapter feedburnerAdapter;
    final FeedburnerService feedburnerService;

    final RestAdapter hatebuAdapter;
    final HatebuService hatebuService;

    public HatebuFeedClient(OkHttpClient httpClient) {
        OkClient client = new OkClient(httpClient);

        feedburnerAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(FEEDBURNER_ENDPOINT)
                .setConverter(new HatebuFeedConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE)
                .build();

        feedburnerService = feedburnerAdapter.create(FeedburnerService.class);

        hatebuAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(HATEBU_ENDPOINT)
                .setConverter(new HatebuFeedConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE)
                .build();
        hatebuService = hatebuAdapter.create(HatebuService.class);
    }

    public Observable<List<HatebuEntry>> getHotentries() {
        return Observable.create(new Observable.OnSubscribe<List<HatebuEntry>>() {
            @Override
            public void call(final Subscriber<? super List<HatebuEntry>> subscriber) {
                feedburnerService.getHotentries(new Callback<List<HatebuEntry>>() {
                    @Override
                    public void success(List<HatebuEntry> hatebuEntries, retrofit.client.Response response) {
                        subscriber.onNext(hatebuEntries);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        subscriber.onError(error);
                    }
                });
            }
        });
    }

    public Observable<List<HatebuEntry>> getHotentries(final String category) {
        return Observable.create(new Observable.OnSubscribe<List<HatebuEntry>>() {
            @Override
            public void call(final Subscriber<? super List<HatebuEntry>> subscriber) {
                hatebuService.getHotentries(category, new Callback<List<HatebuEntry>>() {
                    @Override
                    public void success(List<HatebuEntry> hatebuEntries, retrofit.client.Response response) {
                        subscriber.onNext(hatebuEntries);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        subscriber.onError(error);
                    }
                });
            }
        });
    }


    static interface FeedburnerService {
        @GET("/hatena/b/hotentry")
        void getHotentries(Callback<List<HatebuEntry>> cb);
    }

    static interface HatebuService {
        @GET("/hotentry/{category}.rss")
        void getHotentries(@Path("category") String category, Callback<List<HatebuEntry>> cb);
    }
}
