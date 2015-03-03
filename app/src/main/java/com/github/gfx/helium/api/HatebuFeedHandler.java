package com.github.gfx.helium.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.gfx.helium.model.HatebuEntry;

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
        LINK,
        DESCRIPTION,
        DATE,
        SUBJECT,
        BOOKMARK_COUNT
    }

    List<HatebuEntry> items = new ArrayList<>();
    @Nullable
    HatebuEntry currentItem = null;

    @NonNull
    StringBuilder currentContent = new StringBuilder();

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
            case "link":
                parsing = ParsingElement.LINK;
                break;
            case "description":
                parsing = ParsingElement.DESCRIPTION;
                break;
            case "dc:date":
                parsing = ParsingElement.DATE;
                break;
            case "dc:subject":
                parsing = ParsingElement.SUBJECT;
                break;
            case "hatena:bookmarkcount":
                parsing = ParsingElement.BOOKMARK_COUNT;
                break;
        }

        currentContent.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            items.add(currentItem);
            currentItem = null;
        } else if (currentItem != null) {
            switch(parsing) {
                case TITLE:
                    currentItem.title = currentContent.toString();
                    break;
                case DESCRIPTION:
                    currentItem.description = currentContent.toString();
                    break;
                case LINK:
                    currentItem.link = currentContent.toString();
                    break;
                case SUBJECT:
                    currentItem.subject = currentContent.toString();
                    break;
                case BOOKMARK_COUNT:
                    currentItem.bookmarkCount = currentContent.toString();
                    break;
                case DATE:
                    currentItem.date = currentContent.toString();
                    break;
            }
        }

        parsing = ParsingElement.NONE;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentItem == null) {
            return;
        }

        currentContent.append(ch, start, length);
    }
}
