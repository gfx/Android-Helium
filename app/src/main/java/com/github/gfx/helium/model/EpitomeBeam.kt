package com.github.gfx.helium.model

import com.google.gson.annotations.SerializedName

class EpitomeBeam {

    @SerializedName("sources")
    lateinit var sources: List<EpitomeEntry>
}
