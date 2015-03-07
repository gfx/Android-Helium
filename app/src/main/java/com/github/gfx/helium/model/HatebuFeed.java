package com.github.gfx.helium.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

@Namespace(prefix ="rdf")
@Root(name = "RDF", strict = false)
public class HatebuFeed {
    @ElementList(name = "item", inline = true)
    public List<HatebuEntry> items;
}
