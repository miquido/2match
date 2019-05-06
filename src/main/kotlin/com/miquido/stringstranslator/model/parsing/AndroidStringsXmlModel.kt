package com.miquido.stringstranslator.model.parsing

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "resources")
class StringResources {
    @set:ElementList(required = false, inline = true)
    @get:ElementList(required = false, inline = true)
    var stringsList = mutableListOf<SingleStringModel>()
}

@Root(name = "string")
class SingleStringModel {

    @set:Attribute
    @get:Attribute
    var name: String = ""

    @set:Attribute(required = false)
    @get:Attribute(required = false)
    var translatable: Boolean = true

    @set:Attribute(required = false)
    @get:Attribute(required = false)
    var formatted: Boolean = true

    @set:Text(required = true)
    @get:Text(required = true)
    var text: String = ""
}

@Root(name = "resources")
class StringPluralsResources {
    @set:ElementList(required = false, inline = true)
    @get:ElementList(required = false, inline = true)
    var stringsList = mutableListOf<PluralStringModel>()

}

@Root(name = "plurals")
class PluralStringModel {

    @set:Attribute
    @get:Attribute
    var name: String = ""
    @set:ElementList(required = false, inline = true)
    @get:ElementList(required = false, inline = true)
    var quantityList = mutableListOf<Item>()

}

@Root(name = "item")
class Item {

    @set:Attribute
    @get:Attribute
    var quantity: String = ""

    @set:Text(required = true)
    @get:Text(required = true)
    var text: String = ""

}
