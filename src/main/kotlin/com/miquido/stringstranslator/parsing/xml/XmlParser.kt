package com.miquido.stringstranslator.parsing.xml

import org.simpleframework.xml.core.Persister
import java.io.File

open class XmlParser<T>(private val clazz: Class<T>) {

    fun parse(filePath: String): T {
        val file = File(filePath)
        return parse(file)
    }

    private fun parse(file: File): T {
        val serializer = Persister()
        return serializer.read(clazz, file)
    }
}