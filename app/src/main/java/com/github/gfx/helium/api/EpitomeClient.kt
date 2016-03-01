package com.github.gfx.helium.api

import com.github.gfx.helium.model.EpitomeBeam
import com.github.gfx.helium.model.EpitomeEntry

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject
import javax.inject.Singleton

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import rx.Observable
import rx.functions.Func1

@ParametersAreNonnullByDefault
@Singleton
class EpitomeClient
@Inject
constructor(client: OkHttpClient) {

    internal val service: EpitomeService

    init {
        val retrofit = Retrofit.Builder().baseUrl(ENDPOINT).client(client).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build()

        service = retrofit.create<EpitomeService>(EpitomeService::class.java)
    }

    val entries: Observable<List<EpitomeEntry>>
        get() = service.beam.map { epitomeBeam -> epitomeBeam.sources }

    internal interface EpitomeService {

        val beam: Observable<EpitomeBeam>
    }

    companion object {

        internal val ENDPOINT = "https://ja.epitomeup.com/"
    }
}
