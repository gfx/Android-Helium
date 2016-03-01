package com.github.gfx.helium.api

import com.github.gfx.helium.model.HatebuEntry
import com.github.gfx.helium.model.HatebuFeed

import android.net.Uri

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject
import javax.inject.Singleton

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable
import rx.functions.Func1

@ParametersAreNonnullByDefault
@Singleton
class HatenaClient
@Inject
constructor(client: OkHttpClient) {

    internal val feedburnerService: FeedburnerService

    internal val hatebuService: HatebuService

    init {
        val feedburnerRetrofit = Retrofit.Builder().client(client).baseUrl(FEEDBURNER_ENDPOINT).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(SimpleXmlConverterFactory.create()).build()
        feedburnerService = feedburnerRetrofit.create<FeedburnerService>(FeedburnerService::class.java)

        val hatebuRetrofit = Retrofit.Builder().client(client).baseUrl(HATEBU_ENDPOINT).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(SimpleXmlConverterFactory.create()).build()

        hatebuService = hatebuRetrofit.create<HatebuService>(HatebuService::class.java)
    }

    fun buildHatebuEntryUri(path: String): Uri {
        return HATEBU_ENTRY.buildUpon().appendPath(path).build()
    }

    fun buildHatebuIconUri(username: String): Uri {
        val uri = HATEBU_ICON.replace("{user_prefix}", username.substring(0, 2)).replace("{user}", username)
        return Uri.parse(uri)
    }

    val hotentries: Observable<List<HatebuEntry>>
        get() = feedburnerService.hotentries.map { hatebuFeed -> hatebuFeed.items }

    fun getHotentries(category: String): Observable<List<HatebuEntry>> {
        return hatebuService.getHotentries(category).map { hatebuFeed -> hatebuFeed.items }
    }

    fun getFavotites(user: String, of: Int): Observable<List<HatebuEntry>> {
        return hatebuService.getFavorites(user, of).map { hatebuFeed -> hatebuFeed.items }
    }

    fun getBookmark(user: String, of: Int): Observable<List<HatebuEntry>> {
        return hatebuService.getBookmark(user, of).map { hatebuFeed -> hatebuFeed.items }
    }

    internal interface FeedburnerService {

        val hotentries: Observable<HatebuFeed>
    }

    internal interface HatebuService {

        @GET("/hotentry/{category}.rss")
        fun getHotentries(@Path("category") category: String): Observable<HatebuFeed>

        @GET("/{user}/favorite.rss")
        fun getFavorites(@Path("user") user: String, @Query("of") of: Int): Observable<HatebuFeed>

        @GET("/{user}/bookmark.rss")
        fun getBookmark(@Path("user") user: String, @Query("of") of: Int): Observable<HatebuFeed>
    }

    companion object {

        val FEEDBURNER_ENDPOINT = "http://feeds.feedburner.com/"

        val HATEBU_ENDPOINT = "http://b.hatena.ne.jp/"

        val HATEBU_ENTRY = Uri.parse("http://b.hatena.ne.jp/entry/")

        val HATEBU_ICON = "http://cdn1.www.st-hatena.com/users/{user_prefix}/{user}/profile.gif"

        val KEY_USERNAME = "hatena_username"
    }
}
