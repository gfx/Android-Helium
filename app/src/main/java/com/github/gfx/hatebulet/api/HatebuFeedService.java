package com.github.gfx.hatebulet.api;


import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface HatebuFeedService {
    @GET("/hotentry?mode=rss")
    void getEntries(Callback<List<HatebuEntry>> cb);
}
