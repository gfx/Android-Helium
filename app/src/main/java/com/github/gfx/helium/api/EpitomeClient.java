package com.github.gfx.helium.api;

import com.github.gfx.helium.model.EpitomeBeam;
import com.github.gfx.helium.model.EpitomeEntry;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
@Singleton
public class EpitomeClient {

    static final String ENDPOINT = "https://ja.epitomeup.com/";

    final EpitomeService service;

    @Inject
    public EpitomeClient(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(EpitomeService.class);
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
