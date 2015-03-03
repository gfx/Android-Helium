package com.github.gfx.helium.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EpitomeEntry {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("views")
    public int views;

    @SerializedName("epitome_url")
    public String epitomeUrl;

    @SerializedName("upstream_url")
    public String upstreamUrl;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("gists")
    public List<Gist> gists;

    public static class Gist {
        @SerializedName("content")
        public String content;
    }
}
