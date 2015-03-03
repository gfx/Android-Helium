package com.github.gfx.helium.model;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class HatebuEntry {
    public String title;
    public String description;
    public String link;
    public String subject;
    public String bookmarkCount;
    public String date;

    public DateTime getTimestamp() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(date);
    }

    @Override
    public String toString() {
        return title;
    }
}
