package com.github.gfx.hatebulet.api;

import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import rx.Observable;
import rx.Subscriber;

@ParametersAreNonnullByDefault
public class HatebuFeedClient {
    private static final String TAG = HatebuFeedClient.class.getSimpleName();

    public static final String ENDPOINT = "http://b.hatena.ne.jp/";

    final OkHttpClient httpClient;
    final RestAdapter restAdapter;
    final HatebuFeedService hatebuFeedService;

    public HatebuFeedClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;

        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(httpClient))
                .setEndpoint(ENDPOINT)
                .setConverter(new HatebuFeedConverter())
                .build();

        hatebuFeedService = restAdapter.create(HatebuFeedService.class);
    }

    public Observable<List<HatebuEntry>> getHotentries() {
        return Observable.create(new Observable.OnSubscribe<List<HatebuEntry>>() {
            @Override
            public void call(final Subscriber<? super List<HatebuEntry>> subscriber) {
                hatebuFeedService.getHotentries(new retrofit.Callback<List<HatebuEntry>>() {
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
}
