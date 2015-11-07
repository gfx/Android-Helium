package com.github.gfx.helium.api;

import com.google.gson.Gson;

import com.github.gfx.helium.BuildConfig;
import com.github.gfx.helium.model.EpitomeBeam;
import com.github.gfx.helium.model.EpitomeEntry;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import rx.Observable;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
@Singleton
public class EpitomeClient {

    static final String ENDPOINT = "https://ja.epitomeup.com/";

    final RestAdapter adapter;

    final EpitomeService service;

    @Inject
    public EpitomeClient(Client client, RequestInterceptor requestInterceptor) {
        adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .setConverter(new GsonConverter(new Gson()))
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(
                        BuildConfig.DEBUG ? RestAdapter.LogLevel.BASIC : RestAdapter.LogLevel.NONE)

                .build();

        service = adapter.create(EpitomeService.class);
    }

    public Observable<List<EpitomeEntry>> getEntries() {
        return service.getBeam().map(new Func1<EpitomeBeam, List<EpitomeEntry>>() {
            @Override
            public List<EpitomeEntry> call(EpitomeBeam epitomeBeam) {
                return epitomeBeam.sources;
            }
        });
    }

    interface EpitomeService {

        @GET("/feed/beam")
        Observable<EpitomeBeam> getBeam();
    }
}
