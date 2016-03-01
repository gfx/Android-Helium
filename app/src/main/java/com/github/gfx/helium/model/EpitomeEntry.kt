package com.github.gfx.helium.model

import com.google.gson.annotations.SerializedName

import org.threeten.bp.ZonedDateTime

class EpitomeEntry {

    @SerializedName("id")
    lateinit var id: String

    @SerializedName("scheme")
    lateinit var scheme: String

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("views")
    var views: Int  = 0

    @SerializedName("epitome_url")
    lateinit var epitomeUrl: String

    @SerializedName("upstream_url")
    lateinit var upstreamUrl: String

    @SerializedName("published_at")
    lateinit var publishedAt: String

    @SerializedName("gists")
    var gists: List<Gist>? = null

    var timestamp: ZonedDateTime? = null
        get() = ZonedDateTime.parse(publishedAt)

    fun hasKnownScheme(): Boolean {
        return isGists
    }

    val isGists: Boolean
        get() = SCHEME_GISTS == scheme

    class Gist {

        @SerializedName("content")
        lateinit var content: String
    }

    companion object {

        internal val SCHEME_GISTS = "gists"
    }
}
