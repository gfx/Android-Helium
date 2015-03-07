package com.github.gfx.helium.model;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class HatebuEntry {
    @Element(name = "title")
    public String title;

    @Element(name = "link")
    public String link;

    @Element(name = "description", required = false)
    public String description = "";

    @Namespace(prefix = "dc")
    @Element(name = "date")
    public String date;

    @Namespace(prefix = "dc")
    @Element(name = "subject")
    public String subject;

    @Namespace(prefix = "hatena")
    @Element(name = "bookmarkcount")
    public String bookmarkCount;

    public DateTime getTimestamp() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(date);
    }

    @Override
    public String toString() {
        return title;
    }
}
