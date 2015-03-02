package com.github.gfx.hatebulet.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EpitomeBeam {
    @SerializedName("sources")
    public List<EpitomeEntry> sources;
}
