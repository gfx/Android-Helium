package com.github.gfx.hatebulet.model;

public class HatebuEntry {
    public String title;
    public String description;
    public String link;
    public String subject;
    public String bookmarkCount;
    public String date;

    @Override
    public String toString() {
        return title;
    }
}
