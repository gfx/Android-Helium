package com.github.gfx.helium.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import com.github.gfx.helium.util.HatebuSnippetParser

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import org.threeten.bp.ZonedDateTime
import android.text.TextUtils
import android.text.format.DateUtils

import java.util.ArrayList

@Table(constraints = arrayOf("UNIQUE (link, creator)"))
@Root(name = "item", strict = false)
class HatebuEntry {

    @PrimaryKey(autoincrement = true)
    var cacheId: Long = 0

    @Column
    @Element(name = "title")
    lateinit var title: String

    @Column
    @Element(name = "link")
    lateinit var link: String

    @Column
    @Element(name = "description", required = false)
    var description = ""

    @Column
    @Namespace(prefix = "dc")
    @Element(name = "date")
    lateinit var date: String

    @Column
    @Namespace(prefix = "dc")
    @ElementList(entry = "subject", inline = true, required = false)
    var subject: List<String> = ArrayList()

    @Column
    @Namespace(prefix = "hatena")
    @Element(name = "bookmarkcount")
    lateinit var bookmarkCount: String

    @Column
    @Namespace(prefix = "dc")
    @Element(name = "creator", required = false)
    lateinit var creator: String

    @Column
    @Namespace(prefix = "content")
    @Element(name = "encoded", required = false)
    lateinit var snippet: String

    val timestampDateTime: ZonedDateTime
        get() = ZonedDateTime.parse(date)

    val timestamp: CharSequence
        get() {
            val millis = timestampDateTime.toInstant().toEpochMilli()
            return DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE)

        }

    val summary: CharSequence
        get() = HatebuSnippetParser(snippet).extractSummary()

    fun looksLikeImageUrl(): Boolean {
        return link.matches("https?://.*\\.(?:png|jpe?g|gif|webp)".toRegex())
    }

    val tags: CharSequence
        get() {
            if (subject.isEmpty()) {
                return ""
            }
            val tags = ArrayList<String>(subject.size)
            for (tag in subject) {
                tags.add("#" + tag)
            }
            return TextUtils.join(" ", tags)
        }

    override fun toString(): String {
        return title
    }
}
