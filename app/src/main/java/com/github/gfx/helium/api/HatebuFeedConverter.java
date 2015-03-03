package com.github.gfx.helium.api;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

// TODO: replace it with retrofit.converter.SimpleXMLConverter
public class HatebuFeedConverter implements Converter {

    @Override
    public Object fromBody(TypedInput body, Type type)
            throws ConversionException {

        HatebuFeedHandler handler = new HatebuFeedHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(body.in(), handler);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new ConversionException("RSS Feed conversion error", e);
        }

        return handler.getItems();
    }

    @Override
    public TypedOutput toBody(Object object) {
        throw new UnsupportedOperationException("not supported");
    }
}
