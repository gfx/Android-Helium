package com.github.gfx.helium.model;

import com.google.gson.FieldNamingPolicy;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import org.threeten.bp.ZonedDateTime;

import java.util.List;

@JsonSerializable(
        fieldNamingPolicy = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
)
public class EpitomeEntry {

    final static String SCHEME_GISTS = "gists";

    public String id;

    public String scheme;

    public String title;

    public int views;

    public String epitomeUrl;

    public String upstreamUrl;

    public String publishedAt;

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

    @JsonSerializable
    public static class Gist {

        public String content;
    }
}
