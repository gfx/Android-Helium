package com.github.gfx.helium.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;
import com.github.gfx.helium.util.HatebuSnippetParser;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.threeten.bp.ZonedDateTime;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.List;

@Table(constraints = "UNIQUE (link, creator)")
@Root(name = "item", strict = false)
public class HatebuEntry {

    @PrimaryKey(autoincrement = true)
    public long cacheId;

    @Column
    @Element(name = "title")
    public String title;

    @Column
    @Element(name = "link")
    public String link;

    @Column
    @Element(name = "description", required = false)
    public String description = "";

    @Column
    @Namespace(prefix = "dc")
    @Element(name = "date")
    public String date;

    @Column
    @Namespace(prefix = "dc")
    @ElementList(entry = "subject", inline = true, required = false)
    public List<String> subject = new ArrayList<>();

    @Column
    @Namespace(prefix = "hatena")
    @Element(name = "bookmarkcount")
    public String bookmarkCount;

    @Column
    @Namespace(prefix = "dc")
    @Element(name = "creator", required = false)
    public String creator;

    @Column
    @Namespace(prefix = "content")
    @Element(name = "encoded", required = false)
    public String snippet;

    @NonNull
    public ZonedDateTime getTimestampDateTime() {
        return ZonedDateTime.parse(date);
    }

    @NonNull
    public CharSequence getTimestamp() {
        long millis = getTimestampDateTime().toInstant().toEpochMilli();
        return DateUtils
                .getRelativeTimeSpanString(millis, System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE);

    }

    @NonNull
    public CharSequence getSummary() {
        return new HatebuSnippetParser(snippet).extractSummary();
    }

    public boolean looksLikeImageUrl() {
        return link.matches("https?://.*\\.(?:png|jpe?g|gif|webp)");
    }

    @NonNull
    public CharSequence getTags() {
        if (subject.isEmpty()) {
            return "";
        }
        List<String> tags = new ArrayList<>(subject.size());
        for (String tag : subject) {
            tags.add("#" + tag);
        }
        return TextUtils.join(" ", tags);
    }

    @Override
    public String toString() {
        return title;
    }
}
