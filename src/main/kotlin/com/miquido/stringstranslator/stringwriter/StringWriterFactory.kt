package com.miquido.stringstranslator.stringwriter

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.configuration.Web

class StringWriterFactory {

    fun getStringWriter(platform: Platform, baseLangCode: String): StringWriter = when (platform) {
        is Android -> AndroidStringWriter(baseLangCode)
        is Ios -> IosStringWriter(baseLangCode)
        is Web -> WebStringWriter()
    }
}