package com.github.gfx.hatebulet.api;


import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface HatebuFeedService {
    @GET("/hotentry?mode=rss")
    void getHotentries(Callback<List<HatebuEntry>> cb);

    @GET("/hotentry/{category}.rss")
    void getHotentry(@Path("category") String category, @Query("of") int of, Callback<List<HatebuEntry>> cb);
}
