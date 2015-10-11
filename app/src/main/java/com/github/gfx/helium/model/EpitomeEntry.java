package com.github.gfx.helium.model;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.ZonedDateTime;

import java.util.List;

public class EpitomeEntry {

    final static String SCHEME_GISTS = "gists";

    @SerializedName("id")
    public String id;

    @SerializedName("scheme")
    public String scheme;

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

    public ZonedDateTime getTimestamp() {
        return ZonedDateTime.parse(publishedAt);
    }

    public boolean hasKnownScheme() {
        return isGists();
    }

    public boolean isGists() {
        return SCHEME_GISTS.equals(scheme);
    }

    public static class Gist {

        @SerializedName("content")
        public String content;
    }
}
