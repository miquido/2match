package com.miquido.stringstranslator.parsing.strings

import com.miquido.stringstranslator.model.parsing.ParsedStringTranslationModel

interface StringParser {
    fun parseStringsFile(inputStringPath: String, baseLanguageCode: String)
            : ParsedStringTranslationModel
}