package com.github.gfx.hatebulet.api;

import android.support.annotation.Nullable;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HatebuFeedHandler extends DefaultHandler {
    static final String TAG = HatebuFeedHandler.class.getSimpleName();

    enum ParsingElement {
        NONE,
        TITLE,
        DESCRIPTION,
        LINK
    }

    List<HatebuEntry> items = new ArrayList<>();
    @Nullable
    HatebuEntry currentItem = null;

    ParsingElement parsing = ParsingElement.NONE;

    List<HatebuEntry> getItems() {
        return items;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch(qName) {
            case "item":
                currentItem = new HatebuEntry();
                break;
            case "title":
                parsing = ParsingElement.TITLE;
                break;
            case "description":
                parsing = ParsingElement.DESCRIPTION;
                break;
            case "link":
                parsing = ParsingElement.LINK;
                break;
            default:
                Log.w(TAG, "unknown element: " + qName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            items.add(currentItem);
            currentItem = null;
        }
        parsing = ParsingElement.NONE;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentItem == null) {
            return;
        }

        switch(parsing) {
            case TITLE:
                currentItem.title = new String(ch, start, length);
                break;
            case DESCRIPTION:
                currentItem.description = new String(ch, start, length);
                break;
            case LINK:
                currentItem.link = new String(ch, start, length);
                break;
        }
    }
}
