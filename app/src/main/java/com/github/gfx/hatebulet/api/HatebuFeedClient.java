package com.github.gfx.hatebulet.api;

import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.Subscriber;

@ParametersAreNonnullByDefault
public class HatebuFeedClient {
    private static final String TAG = HatebuFeedClient.class.getSimpleName();

    public static final String FEEDBURNER_ENDPOINT = "http://feeds.feedburner.com/";
    public static final String HATENA_ENDPOINT = "http://b.hatena.ne.jp/";

    final RestAdapter feedburnerAdapter;
    final FeedburnerService feedburnerService;

    public HatebuFeedClient(OkHttpClient httpClient) {
        OkClient client = new OkClient(httpClient);

        feedburnerAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(FEEDBURNER_ENDPOINT)
                .setConverter(new HatebuFeedConverter())
                .build();

        feedburnerService = feedburnerAdapter.create(FeedburnerService.class);
    }

    public Observable<List<HatebuEntry>> getHotentries() {
        return Observable.create(new Observable.OnSubscribe<List<HatebuEntry>>() {
            @Override
            public void call(final Subscriber<? super List<HatebuEntry>> subscriber) {
                feedburnerService.getHotentries(new retrofit.Callback<List<HatebuEntry>>() {
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

    static interface HatenaService {
        @GET("/hotentry/{category}.rss")
        void getHotentry(@Path("category") String category, @Query("of") int of, Callback<List<HatebuEntry>> cb);
    }
}
