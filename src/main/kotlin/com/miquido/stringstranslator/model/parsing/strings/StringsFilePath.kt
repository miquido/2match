package com.miquido.stringstranslator.model.parsing.strings

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.configuration.Web

interface StringsFilePath {
    val value: String

    fun isPluralStringsFilePath(): Boolean

    fun getLanguageCodeFromPath(defaultLanguage: String): String
}

class StringsFilePathFactory {
    fun getStringsFilePath(platform: Platform, path: String) = when (platform) {
        is Ios -> IosStringsFilePath(path)
        is Android -> AndroidStringsFilePath(path)
        is Web -> WebStringsFilePath(path)
    }
}