package com.miquido.stringstranslator.parsing.strings

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.configuration.Web

class StringParserFactory {

    fun getStringParser(platform: Platform): StringParser = when (platform) {
        is Ios -> IosStringParser()
        is Android -> AndroidStringParser()
        is Web -> WebStringParser()
    }

}
