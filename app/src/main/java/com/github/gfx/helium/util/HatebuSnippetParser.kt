package com.github.gfx.helium.util

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import android.util.Xml

import java.io.IOException
import java.io.StringReader

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class HatebuSnippetParser(internal val content: String) {

    internal val xmlPullParser = Xml.newPullParser()

    fun extractSummary(): String {
        try {
            return parseSummary()
        } catch (e: IOException) {
            return ""
        } catch (e: XmlPullParserException) {
            return ""
        }

    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun parseSummary(): String {
        xmlPullParser.setInput(StringReader(content))

        // extract the second <p>...</p> inner text
        var eventType = xmlPullParser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {

            when (eventType) {
                XmlPullParser.START_TAG -> if (xmlPullParser.name.equals("p", ignoreCase = true)) {
                    if (xmlPullParser.next() == XmlPullParser.START_TAG) {
                        // it is a site image like <a><img/></a>
                        // TODO: extract images
                    } else {
                        return xmlPullParser.text
                    }
                }
            }
            eventType = xmlPullParser.next()
        }
        return ""
    }
}
