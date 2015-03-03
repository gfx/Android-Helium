package com.github.gfx.helium.api;

import com.github.gfx.helium.model.EpitomeBeam;
import com.github.gfx.helium.model.EpitomeEntry;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import rx.Observable;
import rx.Subscriber;

public class EpitomeFeedClient {
    final String ENDPOINT = "https://ja.epitomeup.com/";

    final RestAdapter adapter;

    final EpitomeService service;

    public EpitomeFeedClient(OkHttpClient httpClient) {
        adapter = new RestAdapter.Builder()
                .setClient(new OkClient(httpClient))
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(new Gson()))
                .build();

        service = adapter.create(EpitomeService.class);
    }

    public Observable<List<EpitomeEntry>> getEntries() {
        return Observable.create(new Observable.OnSubscribe<List<EpitomeEntry>>() {
            @Override
            public void call(final Subscriber<? super List<EpitomeEntry>> subscriber) {
                service.getBeam(new Callback<EpitomeBeam>() {
                    @Override
                    public void success(EpitomeBeam beam, Response response) {
                        subscriber.onNext(beam.sources);
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

    static interface EpitomeService {
        @GET("/feed/beam")
        void getBeam(Callback<EpitomeBeam> cb);
    }
}
