package com.github.gfx.helium.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Namespace(prefix = "rdf")
@Root(name = "RDF", strict = false)
class HatebuFeed {

    @ElementList(name = "item", inline = true)
    lateinit var items: List<HatebuEntry>
}
