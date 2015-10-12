package com.github.gfx.helium.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.support.annotation.NonNull;
import android.util.Xml;

import java.io.IOException;
import java.io.StringReader;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HatebuSnippetParser {

    final XmlPullParser xmlPullParser = Xml.newPullParser();

    final String content;

    public HatebuSnippetParser(String content) {
        this.content = content;
    }

    @NonNull
    public String extractSummary() {
        try {
            return parseSummary();
        } catch (IOException | XmlPullParserException e) {
            return "";
        }
    }

    @NonNull
    public String parseSummary() throws IOException, XmlPullParserException {
        xmlPullParser.setInput(new StringReader(content));

        // extract the second <p>...</p> inner text
        for (int eventType = xmlPullParser.getEventType();
                eventType != XmlPullParser.END_DOCUMENT;
                eventType = xmlPullParser.next()) {

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xmlPullParser.getName().equalsIgnoreCase("p")) {
                        if (xmlPullParser.next() == XmlPullParser.START_TAG) {
                            // it is a site image like <a><img/></a>
                            // TODO: extract images
                        } else {
                            return xmlPullParser.getText();
                        }
                    }
            }
        }
        return "";
    }
}
